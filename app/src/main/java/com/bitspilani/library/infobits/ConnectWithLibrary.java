package com.bitspilani.library.infobits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.bitspilani.library.infobits.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ConnectWithLibrary extends homepage {

    DrawerLayout drawerlayout;
    NavigationView navigationView;
    int cat, n;
    ListView convlist;
    DBHandler dbhandler;
    TextView msg;
    String id, status;
    JSONObject internal = new JSONObject();
    public final static String[] cats = {"breco","ill","ao","grieve","breview","feedback"}, catnames = {"Book Recommendation","Documents Not Found","Inaccessible Database","Service Issues","Book Review","Feedback"};
    public ArrayList<HashMap<String,String>> talks = new ArrayList<HashMap<String,String>>();
    String urlString = "", message = "", error = "", category = cats[0];
    ProgressBar spinner;
    int[] comms = {R.id.comm1, R.id.comm2, R.id.comm3, R.id.comm4};
    int start = 0, catint = 1, total = 0;
    public final static String actString = "communication_panel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_panel);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        convlist = (ListView) findViewById(R.id.convList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        msg = (TextView) findViewById(R.id.message);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader = navigationView.getHeaderView(0);
        ((TextView) navHeader.findViewById(R.id.brand)).setText(name);
        ((TextView) navHeader.findViewById(R.id.email)).setText(email);
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput == null){
            ((ImageView) navHeader.findViewById(R.id.profile)).setImageResource(R.mipmap.logo);
        }else{
            ((ImageView) navHeader.findViewById(R.id.profile)).setImageBitmap(BitmapFactory.decodeStream(fileInput));
            setToolBarAvatar(profilepic);
        }
        cat = navigationView.getMenu().getItem(0).getItemId();
        navigationView.setItemIconTintList(null);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConnectWithLibrary.this, CommForms.class);
                Bundle b = new Bundle();
                b.putInt("cat", catint);
                i.putExtras(b);
                startActivityForResult(i, 1);
            }
        });
        FloatingActionButton prev = (FloatingActionButton) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start > 0) {
                    start = start - 4;
                    printComms();
                }
            }
        });
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total > start + 4) {
                    start = start + 4;
                    printComms();
                }
            }
        });
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setComm(false);
            }
        });
        FloatingActionButton reply = (FloatingActionButton) findViewById(R.id.reply);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("open")) {
                    findViewById(R.id.replyLayout).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ConnectWithLibrary.this, "The conversation has been terminated by Administrator", Toast.LENGTH_LONG).show();
                }
            }
        });
        FloatingActionButton send = (FloatingActionButton) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replied = ((EditText) findViewById(R.id.replyText)).getText().toString().replace(" ", "%20");
                try {
                    urlString = apiURL + actString + ".php?username=" + username + "&password=" + password + "&action=reply&id=" + id + "&cat=" + catint + "&reply=" + URLEncoder.encode(replied, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(ConnectWithLibrary.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (isConnected()) {
                    findViewById(R.id.convList).setVisibility(View.GONE);
                    findViewById(R.id.convMenu).setVisibility(View.GONE);
                    findViewById(R.id.replyLayout).setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    new APICall().execute(urlString);
                }
            }
        });
        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlString = apiURL + actString + ".php?username=" + username + "&password=" + password + "&action=delete&id=" + id + "&cat=" + catint;
                if (isConnected()) {
                    findViewById(R.id.convList).setVisibility(View.GONE);
                    findViewById(R.id.convMenu).setVisibility(View.GONE);
                    findViewById(R.id.replyLayout).setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    new APICall().execute(urlString);
                }
            }
        });
        actionBarDrawerToggle.syncState();
        dbhandler = new DBHandler(this, null, null);
        setComm(true);
        // Delete data after 2 months
    }

    public void setComm(Boolean update){
        id = "";
        spinner.setVisibility(View.VISIBLE);
        convlist.setVisibility(View.GONE);
        findViewById(R.id.CommScroll).setVisibility(View.VISIBLE);
        findViewById(R.id.replyLayout).setVisibility(View.GONE);
        findViewById(R.id.commMenu).setVisibility(View.GONE);
        findViewById(R.id.convMenu).setVisibility(View.GONE);
        findViewById(R.id.comm1).setVisibility(View.GONE);
        findViewById(R.id.comm2).setVisibility(View.GONE);
        findViewById(R.id.comm3).setVisibility(View.GONE);
        findViewById(R.id.comm4).setVisibility(View.GONE);
        if(update && isConnected()) {
            internal = dbhandler.selectData(0,"status like '%open%' and cat = '" + category + "' ORDER BY id ASC LIMIT 1");
            String id = "1";
            if(internal.keys().hasNext()){
                id = internal.keys().next();
            }
            urlString = apiURL + actString + ".php?username=" + username + "&password=" + password + "&action=update&id=" + id + "&cat=" + catint;
            new APICall().execute(urlString);
        }
        else{
            printComms();
        }
    }

    public void setConv(){
        findViewById(R.id.replyLayout).setVisibility(View.GONE);
        findViewById(R.id.CommScroll).setVisibility(View.GONE);
        findViewById(R.id.commMenu).setVisibility(View.GONE);
        findViewById(R.id.convMenu).setVisibility(View.VISIBLE);
        MyAdapter adapter = new MyAdapter(this, talks,
                android.R.layout.two_line_list_item,
                new String[] { "info","talk" },
                new int[] {android.R.id.text1, android.R.id.text2
        });
        convlist.setAdapter(adapter);
//        if(catint == 1){
//            convlist.getChildAt(1).setMinimumHeight(n * 200);
//        }
        convlist.setVisibility(View.VISIBLE);
    }

    public void conversation1(View view){
        try {
            JSONObject json = dbhandler.selectData(0, "status not like '%inactive%' and cat = '" + category + "' ORDER BY id DESC LIMIT " + start + ",1");
            id = json.keys().next();
            JSONObject data = (JSONObject) json.get(id);
            status = data.get("status").toString();
            getConv(data.get("talk").toString(), data.get("admins").toString());
        }catch(JSONException e){
            //Toast.makeText(ConnectWithLibrary.this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void conversation2(View view){
        try {
            JSONObject json = dbhandler.selectData(0, "status not like '%inactive%' and cat = '" + category + "' ORDER BY id DESC LIMIT " + (start+1) + ",1");
            id = json.keys().next();
            JSONObject data = (JSONObject) json.get(id);
            status = data.get("status").toString();
            getConv(data.get("talk").toString(),data.get("admins").toString());
        }catch(JSONException e){
            //Toast.makeText(ConnectWithLibrary.this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void conversation3(View view){
        try {
            JSONObject json = dbhandler.selectData(0, "status not like '%inactive%' and cat = '" + category + "' ORDER BY id DESC LIMIT " + (start+2) + ",1");
            id = json.keys().next();
            JSONObject data = (JSONObject) json.get(id);
            status = data.get("status").toString();
            getConv(data.get("talk").toString(), data.get("admins").toString());
        }catch(JSONException e){
            //Toast.makeText(ConnectWithLibrary.this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void conversation4(View view){
        try {
            JSONObject json = dbhandler.selectData(0, "status not like '%inactive%' and cat = '" + category + "' ORDER BY id DESC LIMIT " + (start+3) + ",1");
            id = json.keys().next();
            JSONObject data = (JSONObject) json.get(id);
            status = data.get("status").toString();
            getConv(data.get("talk").toString(),data.get("admins").toString());
        }catch(JSONException e){
            //Toast.makeText(ConnectWithLibrary.this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void getConv(String data, String admins){
        int p = 0;
        int x;
//        int x = admins.indexOf(";", 0);
//        while(x > 0 && !admins.isEmpty()){
//            adminsarr.add(i, admins.substring(p, x));
//            p = x + 2;
//            x = admins.indexOf(";", p);
//            i = i + 1;
//        }
//        i = 0;
//        p = 0;
        int x1, x2, x3;
        talks.clear();
        while(data.indexOf("//", p) >= 0) {
            HashMap<String,String> talk = new HashMap<>();
            x = data.indexOf(")|", p);
            x1 = data.indexOf("//", p);
            x2 = data.indexOf("(Date-", p);
            x3 = data.indexOf(",Time-", p);
            if (!(data.substring(p, x2).equals("From Library") && usercat.equals("Admin"))){
                    talk.put("info", data.substring(p, x2) + "\nDate: " + data.substring(x2 + 6, x3) + "  Time: " + data.substring(x3 + 6, x));
                try {
                    talk.put("talk", "\n" + URLDecoder.decode(data.substring(x + 3, x1 - 1).replaceAll("<br />", "\n"), "UTF-8"));
                    talks.add(talk);
                } catch (UnsupportedEncodingException e) {
                    //Toast.makeText(ConnectWithLibrary.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            p = x1 + 3;
        }
        setConv();
    }
    public void updateInternalData(JSONObject json, String action){
        try{
            if(json.has("err_message") && !json.get("err_message").toString().isEmpty()){
                spinner.setVisibility(View.GONE);
                error = json.get("err_message").toString();
                Toast.makeText(ConnectWithLibrary.this,json.get("err_message").toString(),Toast.LENGTH_LONG).show();
            }
            if(json.has("message") && !json.get("message").toString().isEmpty()){
                message = json.get("message").toString();
            }
            if(action.equals("update")){
                if(!json.get("data").toString().equals("[]")){
                    JSONObject data = (JSONObject) json.get("data");
                    Iterator<String> iter = data.keys();
                    Iterator<String> intiter = internal.keys();
                    String intkey;
                    JSONObject intval;
                    while(iter.hasNext()) {
                        String key = iter.next();
                        JSONObject dataval = (JSONObject) data.get(key);
                        if(intiter.hasNext()) {
                            intkey = intiter.next();
                            if (key.equals(intkey)) {
                                intval = (JSONObject) internal.get(intkey);
                                intval.put("admins", dataval.get("admins").toString());
                                intval.put("talk", dataval.get("talk").toString());
                                intval.put("status", dataval.get("status").toString());
                                String[] updatevalues = {dataval.get("admins").toString(), dataval.get("talk").toString(), dataval.get("status").toString()};
                                dbhandler.updateData(0, updatevalues, Integer.parseInt(intkey));
                            } else {
                                JSONObject check = dbhandler.selectData(0, "id = " + key);
                                if (!check.has(key)) {
                                    String[] addvalues = {key, dataval.get("bitsid").toString(), dataval.get("category").toString(), dataval.get("name").toString(), dataval.get("topic").toString(), dataval.get("date").toString(), dataval.get("time").toString(), dataval.get("admins").toString(), dataval.get("talk").toString(), json.get("cat").toString(), dataval.get("status").toString()};
                                    dbhandler.addData(0, addvalues);
                                }
                            }
                        }
                        else{
                            JSONObject check = dbhandler.selectData(0, "id = " + key);
                            if (!check.has(key)) {
                                String[] addvalues = {key, dataval.get("bitsid").toString(), dataval.get("category").toString(), dataval.get("name").toString(), dataval.get("topic").toString(), dataval.get("date").toString(), dataval.get("time").toString(), dataval.get("admins").toString(), dataval.get("talk").toString(), json.get("cat").toString(), dataval.get("status").toString()};
                                dbhandler.addData(0, addvalues);
                            }
                        }
                    }
                }
                printComms();
                message = "";
            }
            else if (action.equals("delete")) {
                if(error.isEmpty()){
                    JSONObject dataval = (JSONObject) dbhandler.selectData(0,"id = " + id).get(id);
                    String[] updatevalues = {dataval.get("admins").toString(), dataval.get("talk").toString(), "inactive"};
                    dbhandler.updateData(0, updatevalues, Integer.parseInt(id));
                    Toast.makeText(ConnectWithLibrary.this,message,Toast.LENGTH_LONG).show();
                    printComms();
                    message = "";
                }
                else{
                    findViewById(R.id.convList).setVisibility(View.VISIBLE);
                    findViewById(R.id.convMenu).setVisibility(View.VISIBLE);
                    findViewById(R.id.replyLayout).setVisibility(View.VISIBLE);
                    error = "";
                }
            }
            else if(action.equals("reply")){
                JSONObject data = (JSONObject) ((JSONObject) json.get("data")).get(id);
                String[] updatevalues = {data.get("admins").toString(), data.get("talk").toString(), data.get("status").toString()};
                dbhandler.updateData(0, updatevalues, Integer.parseInt(id));
                if(error.isEmpty()){
                    Toast.makeText(ConnectWithLibrary.this,message,Toast.LENGTH_LONG).show();
                    message = "";
                    getConv(data.get("talk").toString(),data.get("admins").toString());
                }
                else{
                    findViewById(R.id.convList).setVisibility(View.VISIBLE);
                    findViewById(R.id.convMenu).setVisibility(View.VISIBLE);
                    findViewById(R.id.replyLayout).setVisibility(View.VISIBLE);
                    error = "";
                }
            }
            else if(action.equals("new")){
                if(error.isEmpty()){
                    JSONObject data = (JSONObject) json.get("data");
                    String id = data.keys().next();
                    JSONObject dataval = (JSONObject) data.get(id);
                    String[] updatevalues = {id, dataval.get("bitsid").toString(), dataval.get("category").toString(), dataval.get("name").toString(),dataval.get("topic").toString(),dataval.get("date").toString(),dataval.get("time").toString(),dataval.get("admins").toString(),dataval.get("talk").toString(),dataval.get("cat").toString(),dataval.get("status").toString()};
                    dbhandler.addData(0, updatevalues);
                    Toast.makeText(ConnectWithLibrary.this,message,Toast.LENGTH_LONG).show();
                    message = "";
                }
                else{
                    error = "";
                }
            }
        } catch (JSONException e) {
            //Toast.makeText(ConnectWithLibrary.this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class APICall extends AsyncTask<String,Integer,String> {

        String err;
        @Override
        protected String doInBackground(String[] params) {
            String urlString= params[0];
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
            } catch (Exception e ) {
                err = "Network Error! Ensure you're connected to BITS Intranet";
            }
            return responseStrBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            spinner.setVisibility(View.GONE);
            if(!result.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(result);
                    updateInternalData(json, json.get("action").toString());
                } catch (Exception e) {
                    //Toast.makeText(ConnectWithLibrary.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }else{
                if(!err.isEmpty()){
                    Toast.makeText(ConnectWithLibrary.this,err,Toast.LENGTH_LONG).show();
                }
                if(urlString.contains("action=update")) {
                    printComms();
                }
                else{
                    findViewById(R.id.convList).setVisibility(View.VISIBLE);
                    findViewById(R.id.convMenu).setVisibility(View.VISIBLE);
                    findViewById(R.id.replyLayout).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void printComms(){
        id = "";
        spinner.setVisibility(View.GONE);
        findViewById(R.id.message).setVisibility(View.GONE);
        findViewById(R.id.commMenu).setVisibility(View.VISIBLE);
        findViewById(R.id.CommScroll).setVisibility(View.VISIBLE);
        convlist.setVisibility(View.GONE);
        findViewById(R.id.replyLayout).setVisibility(View.GONE);
        findViewById(R.id.convMenu).setVisibility(View.GONE);
        findViewById(R.id.comm1).setVisibility(View.GONE);
        findViewById(R.id.comm2).setVisibility(View.GONE);
        findViewById(R.id.comm3).setVisibility(View.GONE);
        findViewById(R.id.comm4).setVisibility(View.GONE);
        total = dbhandler.getNum(0,"status not like '%inactive%' and cat like '%" + category + "%'");
        if(start > 0){
            findViewById(R.id.prev).setClickable(true);
        }
        else{
            findViewById(R.id.prev).setClickable(false);
        }
        if(total > start + 4){
            findViewById(R.id.next).setClickable(true);
        }
        else{
            findViewById(R.id.next).setClickable(false);
        }

        JSONObject dispjson = dbhandler.selectData(0, "status not like '%inactive%' and cat like '%" + category + "%' ORDER BY status desc, id desc LIMIT " + start + ",4");
        Iterator<String> iter = dispjson.keys();
        JSONObject data;
        String text;
        try {
            if(iter.hasNext()) {
                for (int i = 0; iter.hasNext(); i++) {
                    data = (JSONObject) dispjson.get(iter.next());
                    text = data.get("topic").toString() + " (" + data.get("status").toString() + ")\n" + data.get("date").toString() + " | " + data.get("time").toString();
                    if(!data.get("admins").toString().isEmpty()) {
                        text = text + "\n" + data.get("admins").toString();
                    }
                    findViewById(comms[i]).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(comms[i])).setText(text);
                }
            }
            else{
                msg.setVisibility(View.VISIBLE);
                msg.setText("No Conversations Found");
            }
        } catch (JSONException e) {
            //Toast.makeText(ConnectWithLibrary.this,e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(!item.toString().equals("Refresh Data")) {
            if(!item.isChecked())
                item.setChecked(true);
            else
                return false;
            cat = item.getItemId();
            catint = Arrays.asList(catnames).indexOf(item.toString()) + 1;
            start = 0;
            category = cats[catint - 1];
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        setComm(true);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("update");
                    if(newText.equals("yes")){
                        printComms();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.drawerlayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerlayout.closeDrawer(GravityCompat.START);
        }
        else if(findViewById(R.id.replyLayout) != null && findViewById(R.id.replyLayout).getVisibility() == View.VISIBLE){
            findViewById(R.id.replyLayout).setVisibility(View.GONE);
        }
        else if(!id.equals("")){
            setComm(false);
        }
        else{
            super.onBackPressed();
        }
    }

    public class MyAdapter extends SimpleAdapter{

        List<HashMap<String,String>> talks;

        public MyAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
            super(context, items, resource, from, to);
            talks = items;
        }

        public class ViewHolder{
            public TextView info;
            public TextView talk;
            public WebView table;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;
            if(convertView==null){
                vi = getLayoutInflater().inflate(R.layout.convlistitem, null);
                holder = new ViewHolder();
                holder.info = (TextView) vi.findViewById(R.id.Info);
                holder.talk = (TextView) vi.findViewById(R.id.Talk);
                holder.table = (WebView) vi.findViewById(R.id.Table);
                vi.setTag( holder );
            }
            else
                holder=(ViewHolder)vi.getTag();
            HashMap<String,String> data = talks.get(position);
            holder.info.setText(data.get("info"));
            if(data.get("talk").contains("<table>")){
                n = 0;
                int start = data.get("talk").indexOf("<tr>");
                while(start >= 0){
                    start = data.get("talk").indexOf("<tr>", start + 4);
                    n++;
                }
                vi.setMinimumHeight(getCorrectPixels(2 * (350 + (n - 1) * 300)/5));
                holder.talk.setVisibility(View.GONE);
                holder.table.loadData(data.get("talk"),"text/html",null);
                holder.table.setVisibility(View.VISIBLE);
            }
            else{
                holder.talk.setVisibility(View.VISIBLE);
                holder.table.setVisibility(View.GONE);
                holder.talk.setText(data.get("talk"));
            }
            return vi;
        }
    }
}
