package com.example.suryansh.infobits;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class lfmsAllItems extends homepage {

    private ProgressDialog pDialog;
    String message = "", actString = "get_all_items", urlString = "";
    ArrayList<HashMap<String, String>> ItemsList = new ArrayList<HashMap<String, String>>();
    ListView lv;
    TextView msg;
    DBHandler dbhandler;
    JSONObject internal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfms_all_items);
        lv = (ListView) findViewById(R.id.list);
        msg = (TextView) findViewById(R.id.message);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        internal = dbhandler.selectData(4,"1 ORDER BY id DESC");
        if(isConnected()){
            urlString = apiURL + actString + ".php?username=" + username + "&password=" + password;
            new APICall().execute(urlString);
        }
        else{
            setList(internal);
        }
    }

    private class APICall extends AsyncTask<String,Integer,String> {

        String err;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(lfmsAllItems.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

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
                    Toast.makeText(lfmsAllItems.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            else{
                if(!err.isEmpty()){
                    Toast.makeText(lfmsAllItems.this,err,Toast.LENGTH_LONG).show();
                }
                setList(internal);
            }
            if (pDialog != null)
                pDialog.dismiss();
        }
    }

    public void updateInternalData(JSONObject json){
        try{
            if(json.has("err_message") && !json.get("err_message").toString().isEmpty()){
                Toast.makeText(lfmsAllItems.this, json.get("err_message").toString(), Toast.LENGTH_LONG).show();
            }
            if(json.has("message") && !json.get("message").toString().isEmpty()){
                message = json.get("message").toString();
            }
            if(message.isEmpty() && !json.get("data").toString().equals("[]")){
                JSONObject data = (JSONObject) json.get("data");
                Iterator<String> iter = data.keys();
                while(iter.hasNext()) {
                    String key = iter.next();
                    JSONObject dataval = (JSONObject) data.get(key);
                    if (!internal.has(key)) {
                        String[] addvalues = {key, dataval.get("particulars").toString(), dataval.get("brand").toString(), dataval.get("date").toString(), dataval.get("time").toString(), dataval.get("status").toString()};
                        dbhandler.addData(4, addvalues);
                    }
                    else{
                        internal.remove(key);
                    }
                }
                setList(data);
                Iterator<String> intiter = internal.keys();
                while(intiter.hasNext()){
                    dbhandler.deleteData(4, Integer.parseInt(intiter.next()));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(lfmsAllItems.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void setList(JSONObject json){
        try{
            Iterator<String> iter = json.keys();
            while(iter.hasNext()) {
                String key = iter.next();
                JSONObject data = (JSONObject) json.get(key);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("sno", key);
                map.put("status", data.get("status").toString());
                map.put("brand", data.get("brand").toString());
                map.put("particulars", data.get("particulars").toString());
                ItemsList.add(map);
            }
        } catch (JSONException e) {
            Toast.makeText(lfmsAllItems.this,e.toString(),Toast.LENGTH_LONG).show();
        }
        showList();
    }

    public void showList(){
        if(message.isEmpty()){
            MyAdapter adapter = new MyAdapter(getApplicationContext(), ItemsList);
            lv.setAdapter(adapter);
        }
        else{
            lv.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
            msg.setText(message);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    public class MyAdapter extends ArrayAdapter<HashMap<String, String>> {

        private ArrayList<HashMap<String, String>> lf;
        public MyAdapter(Context context, ArrayList<HashMap<String, String>> items) {
            super(context,0, items);
            lf = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            final HashMap i = lf.get(position);
            if (i != null) {
                v = getLayoutInflater().inflate(R.layout.list_item, null);
                ((TextView) v.findViewById(R.id.sno)).setText(i.get("sno").toString());
                TextView nametext = ((TextView) v.findViewById(R.id.name));
                nametext.setText("Particulars: " + i.get("particulars").toString());
                if(!i.get("brand").toString().isEmpty()) {
                    nametext.append("\nBrand: " + i.get("brand").toString());
                }
                if(i.get("status").toString().equals("0")){
                    v.findViewById(R.id.claimed).setVisibility(View.VISIBLE);
                    v.setBackgroundColor(Color.rgb(192,192,192));
                    v.setClickable(false);
                }
                else{
                    v.setClickable(true);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(getApplicationContext(), Claim.class);
                            in.putExtra("sno", i.get("sno").toString());
                            startActivityForResult(in, 100);
                        }
                    });
                }
            }
            return v;
        }
    }
}
