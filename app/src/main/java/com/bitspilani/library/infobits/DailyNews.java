package com.bitspilani.library.infobits;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class DailyNews extends homepage{

    ListView newscast;
    DBHandler dbhandler;
    TextView msg, smsg;
    ProgressBar spinner;
    JSONObject internal;
    DatePicker start, end;
    String urlString = "", message = "", search_message = "", error = "", s = "", e = "", keyword = "", action = "update";
    ArrayList<Item> news = new ArrayList<Item>();
    public ArrayList<String> urls = new ArrayList<String>();
    public final static String actString = "daily_news";
    Dialog dialog;
    FloatingActionButton search, refresh;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_news);
        dialog = new Dialog(DailyNews.this);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        newscast = (ListView) findViewById(R.id.newsList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        msg = (TextView) findViewById(R.id.message);
        smsg = (TextView) findViewById(R.id.search_message);
        setSupportActionBar(toolbar);
        search = (FloatingActionButton) findViewById(R.id.search);
        refresh = (FloatingActionButton) findViewById(R.id.refresh);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.newssearch);
                start = ((DatePicker) dialog.findViewById(R.id.startDatePicker));
                end = ((DatePicker) dialog.findViewById(R.id.endDatePicker));
                Date today = new Date();
                Date last = new Date(0);
                try {
                    last = df.parse("1900-01-01");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                start.setMaxDate(today.getTime() + 19800000);
                end.setMaxDate(today.getTime() + 19800000);
                start.setMinDate(last.getTime());
                start.setMinDate(last.getTime());
                dialog.setTitle("Search News ...");
                dialog.show();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNews(true);
            }
        });
        newscast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!urls.get(position).isEmpty()) {
                    if (!urls.get(position).equals("header")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position)));
                        startActivity(browserIntent);
                    }
                } else {
                    Toast.makeText(DailyNews.this, "No Link Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput != null){
            setToolBarAvatar(profilepic);
        }
        dbhandler = new DBHandler(this, null, null);
        setNews(true);
    }

    public void setNews(boolean update){
        action = "update";
        newscast.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        smsg.setVisibility(View.GONE);
        //msg.setVisibility(View.GONE);
        if(update && isConnected()) {
            urlString = apiURL + actString + ".php?username=" + username + "&password=" + password + "&action=update";
            new APICall().execute(urlString);
        }
        else if(update){
            Toast.makeText(DailyNews.this,"Not Connected to BITS Intranet!",Toast.LENGTH_LONG).show();
            showNews();
        }
    }

    public void showNews(){
        Date today = new Date();
        news.clear();
        urls.clear();
        getNews("(JULIANDAY('" + df.format(today) + "') - JULIANDAY(date)) <= 7 ORDER BY date DESC");
        if(msg.getVisibility() == View.GONE) {
            MyAdapter adapter = new MyAdapter(this, news);
            newscast.setVisibility(View.VISIBLE);
            newscast.setAdapter(adapter);
        }
        spinner.setVisibility(View.GONE);
    }

    public void getNews(String sql){
        internal = dbhandler.selectData(1,sql);
        Iterator iter = internal.keys();
        String pH = "";
        msg.setVisibility(View.GONE);
        int i = 0, j = 0;
        while(iter.hasNext()){
            String key = iter.next().toString();
            try {
                JSONObject dataval = (JSONObject) internal.get(key);
                Date headerDate;
                SimpleDateFormat ndf = new SimpleDateFormat("EEE, MMM dd yyyy");
                try {
                    headerDate = df.parse(dataval.get("date").toString());
                    String nDS  = ndf.format(headerDate);
                    if(!pH.equals(nDS)) {
                        news.add(i, new SectionItem(nDS));
                        pH = nDS;
                        urls.add(i, "header");
                        i++;
                        j++;
                    }
                    news.add(i, new EntryItem(dataval.get("title").toString(), dataval.get("newspaper").toString() + ", pg. " + dataval.get("pages").toString()));
                    urls.add(i, dataval.get("url").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            i++;
        }
        if(action.equals("search")) {
            if (!search_message.equals("") && !search_message.isEmpty()) {
                smsg.setText(search_message);
                search_message = "";
            } else {
                search_message = (i - j) + " headline(s) found from" + " '" + s + "' to '" + e + "' for keywords ' " + keyword + " '";
            }
        }
        if (i == 0 && !message.isEmpty()) {
            msg.setText(message);
            msg.setVisibility(View.VISIBLE);
            message = "";
        } else if (i == 0) {
            if(action.equals("search")){
                msg.setText(R.string.no_news);
            }else{
                msg.setText(R.string.no_news_for_week);
            }
            msg.setVisibility(View.VISIBLE);
        }
    }

    public String getStringDate(DatePicker date){
        String sdate;
        if(date.getMonth() + 1 > 9){
           sdate  = String.valueOf(date.getYear()) + "-" + String.valueOf(date.getMonth() + 1);
        }
        else{
            sdate = String.valueOf(date.getYear()) + "-" + "0" + String.valueOf(date.getMonth() + 1);
        }
        if(date.getDayOfMonth() > 9){
            sdate = sdate + "-" + String.valueOf(date.getDayOfMonth());
        }
        else{
            sdate = sdate + "-" + "0" + String.valueOf(date.getDayOfMonth());
        }
        return sdate;
    }

    public void getSearchNews(View view){
        s = getStringDate(start);
        e = getStringDate(end);
        action = "search";
        Boolean incorrect = false;
        try {
            incorrect = df.parse(s).after(df.parse(e));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        if(incorrect){
            Toast.makeText(DailyNews.this,"Start Date can't be greater than End Date!",Toast.LENGTH_LONG).show();
        }
        else {
            keyword = ((EditText) dialog.findViewById(R.id.keywords)).getText().toString() + " ";
            dialog.dismiss();
            spinner.setVisibility(View.VISIBLE);
            newscast.setVisibility(View.GONE);
            smsg.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
            if (isConnected()) {
                urlString = apiURL + actString + ".php?username=" + username + "&password=" + password + "&action=search&startDate=" + s + "&endDate=" + e + "&keyword=" + keyword;
                new APICall().execute(urlString);
            } else {
                Toast.makeText(DailyNews.this, "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
                showSearchNews();
            }
        }
    }

    public void showSearchNews(){
        String sql = "";
        if(!keyword.equals("") && !keyword.equals(" ") && !keyword.isEmpty()){
            int st = 0;
            int en = keyword.indexOf(" ", st);
            for(int i = 0; en >= 0; i++){
                if(i > 0){
                    sql = sql + " OR ";
                }
                sql = sql + "keywords LIKE '%" + keyword.substring(st, en) + "%'";
                st = en + 1;
                en = keyword.indexOf(" ", st);
            }
        }
        if(!sql.equals("") && !sql.isEmpty()){
            sql =  " and (" + sql + ")";
        }
        news.clear();
        urls.clear();
        getNews("(JULIANDAY(date) - JULIANDAY('" + s + "')) >= 0 and (JULIANDAY(date) - JULIANDAY('" + e + "')) <= 0" + sql + " ORDER BY date DESC");
        if(msg.getVisibility() == View.GONE) {
            MyAdapter adapter = new MyAdapter(this, news);
            newscast.setVisibility(View.VISIBLE);
            newscast.setAdapter(adapter);
        }
        smsg.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        search_message = "";
    }

    public void updateInternalData(JSONObject json){
        try{
            if(json.has("err_message") && !json.get("err_message").toString().isEmpty()){
                spinner.setVisibility(View.GONE);
                error = json.get("err_message").toString();
                Toast.makeText(DailyNews.this, json.get("err_message").toString(), Toast.LENGTH_LONG).show();
            }
            if(json.has("message") && !json.get("message").toString().isEmpty()){
                message = json.get("message").toString();
            }
            if(json.has("search_message") && !json.get("search_message").toString().isEmpty()){
                search_message = json.get("search_message").toString();
            }
            if(error.isEmpty() && !json.get("data").toString().equals("[]")){
                JSONObject data = (JSONObject) json.get("data");
                Iterator<String> iter = data.keys();
                while(iter.hasNext()) {
                    String key = iter.next();
                    JSONObject dataval = (JSONObject) data.get(key);
                    JSONObject check = dbhandler.selectData(1, "id = " + key);
                    if (!check.has(key)) {
                        String[] addvalues = {key, dataval.get("news_type").toString(), dataval.get("title").toString(), dataval.get("url").toString(), dataval.get("date").toString(), dataval.get("added_by").toString(), dataval.get("newspaper").toString(), dataval.get("keywords").toString(), dataval.get("pages").toString()};
                        dbhandler.addData(1, addvalues);
                    }
                }
            }
            error = "";
        } catch (JSONException e) {
            Toast.makeText(DailyNews.this,e.toString(),Toast.LENGTH_LONG).show();
        }
        if(action.equals("search")){
            showSearchNews();
        }
        else{
            showNews();
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
            if(!result.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(result);
                    updateInternalData(json);
                } catch (Exception e) {
                    Toast.makeText(DailyNews.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }else{
                if(!err.isEmpty()){
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(DailyNews.this,err,Toast.LENGTH_LONG).show();
                }
                if(action.equals("search")){
                    showSearchNews();
                }
                else{
                    showNews();
                }
            }
        }
    }

    public class MyAdapter extends ArrayAdapter<Item> {

        private ArrayList<Item> news;
        public MyAdapter(Context context, ArrayList<Item> items) {
            super(context,0, items);
            news = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            final Item i = news.get(position);
            if (i != null) {
                v = getLayoutInflater().inflate(R.layout.newslistitem, null);
                final TextView header = (TextView) v.findViewById(R.id.header);
                final TextView title = (TextView)v.findViewById(R.id.title);
                final TextView paper = (TextView)v.findViewById(R.id.paper);
                if(i.isSection()){
                    title.setVisibility(View.GONE);
                    paper.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    SectionItem si = (SectionItem) i;
                    header.setText(si.getDate());
                }else{
                    title.setVisibility(View.VISIBLE);
                    paper.setVisibility(View.VISIBLE);
                    header.setVisibility(View.GONE);
                    EntryItem ei = (EntryItem) i;
                    title.setText(ei.title);
                    paper.setText(ei.paper);
                }
            }
            return v;
        }
    }

    interface Item {
        boolean isSection();
    }

    class SectionItem implements Item{
        private final String date;

        public SectionItem(String date) {
            this.date = date;
        }
        public String getDate(){
            return date;
        }
        @Override
        public boolean isSection() {
            return true;
        }
    }

    class EntryItem implements Item{
        public final String title;
        public final String paper;

        public EntryItem(String title, String paper) {
            this.title = title;
            this.paper = paper;
        }
        @Override
        public boolean isSection() {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        else{
            super.onBackPressed();
        }
    }
}
