package com.bitspilani.library.infobits;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bitspilani.library.infobits.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Cover extends AppCompatActivity {

//    public final static String apiURL = "http://172.21.1.15/apis/";
//    public final static String imageApiURL = "http://172.21.1.15/uploads/";
    public final static String apiURL = "http://192.168.43.71:80/infoBITS/apis/";
    public final static String imageApiURL = "http://192.168.43.71:80/infoBITS/uploads/";
    String actString = "notices";
    int imgs;
    ProgressBar spinner;
    DBHandler dbhandler;
    JSONObject internal;
    File dir;
    public static String done = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbhandler = new DBHandler(this,null,null);
        internal = dbhandler.selectData(2,"1 ORDER BY id ASC");
        setContentView(R.layout.activity_cover);
        dir = getFilesDir();
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        dbhandler = new DBHandler(this,null,null);
        internal = dbhandler.selectData(2,"1 ORDER BY id ASC");
        updateNotice();
    }

    private class APICall extends AsyncTask<String,Integer,String> {

        String err;
        String type;
        @Override
        protected String doInBackground(String[] params){
            String urlString= params[0];
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            type = "image";
            if(params[0].contains("daily_news")){
                type = "daily_news";
            }
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
                    updateImageData(json, type);
                } catch (Exception e) {
                    Toast.makeText(Cover.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            else{
                if(!err.isEmpty()){
                    if(type.equals("image")){
                        Toast.makeText(Cover.this, err, Toast.LENGTH_LONG).show();
                        launchHome();
                    }
                }
            }
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        String filename, link, key, type;
        Integer tot;
        protected Bitmap doInBackground(String[] args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            filename = args[0].substring(args[0].lastIndexOf("/") + 1);
            type = args[1].substring(0, args[1].indexOf("-"));
            tot = Integer.parseInt(args[1].substring(args[1].indexOf("-") + 1, args[1].indexOf("%")));
            if(type.equals("botw")){
                key = "1";
                link = args[1].substring(args[1].indexOf("|") + 1) + "---" + args[1].substring(args[1].indexOf("%") + 1,  args[1].indexOf("|"));
            }
            else{
                link = args[1].substring(args[1].indexOf("|") + 1);
                key = args[1].substring(args[1].indexOf("%") + 1, args[1].indexOf("|"));
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            imgs++;
            if(image != null){
                String ext = filename.substring(filename.lastIndexOf("."));
                //filename = filename.substring(0, filename.lastIndexOf("."));
                File file = new File(dir, filename);
                FileOutputStream fileOut;
                try {
                    fileOut = new FileOutputStream(file);
                    //msg.append(ext);
                    if(ext.equals(".jpg") || ext.equals(".jpeg")){
                        image.compress(Bitmap.CompressFormat.JPEG, 50, fileOut);
                    }
                    else{
                        image.compress(Bitmap.CompressFormat.PNG, 50, fileOut);
                    }
                    fileOut.close();
                    //msg.append(filename + "\n");
                    updateImage(filename, link, key, type);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(imgs == tot){
                imgs = 0;
                launchHome();
            }
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            Toast.makeText(Cover.this, "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
            launchHome();
            return false;
        }
    }

    public void updateImageData(JSONObject json, String type){
        try {
            if(!json.get("data").toString().equals("[]")){
                JSONObject data = (JSONObject) json.get("data");
                if(type.equals("daily_news")){
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
                else{
                    JSONObject botw = dbhandler.selectData(3, "1");
                    Iterator<String> botwiter = botw.keys();
                    Iterator<String> iter = data.keys();
                    Iterator<String> intiter = internal.keys();
                    String intkey, botwkey;
                    while(intiter.hasNext()){
                        intkey = intiter.next();
                        JSONObject intval = (JSONObject) internal.get(intkey);
                        if(!data.has(intkey)){
                            File file = new File(dir, intval.get("image").toString());
                            if(file.delete()) {
                                dbhandler.deleteData(2, Integer.parseInt(intkey));
                                internal.remove(intkey);
                            }
                        }
                    }
                    while(botwiter.hasNext()){
                        botwkey = botwiter.next();
                        JSONObject botwval = (JSONObject) botw.get(botwkey);
                        if(data.has("botw")){
                            JSONObject bo = (JSONObject) data.get("botw");
                            if(!bo.get("title").toString().equals(botwval.get("title").toString()) || !bo.get("author").toString().equals(botwval.get("author").toString())) {
                                File file = new File(dir, botwval.get("image").toString());
                                if (file.delete()) {
                                    dbhandler.deleteData(3, Integer.parseInt(botwkey));
                                }
                            }
                        }
                    }
                    int loader = 0;
                    while(iter.hasNext()){
                        String key = iter.next();
                        JSONObject dataval = (JSONObject) data.get(key);
                        if(dataval.get("type").toString().equals("botw")){
                            botw = dbhandler.selectData(3, "1");
                            if(botw.length() == 0) {
                                try {
                                    new LoadImage().execute(imageApiURL + "book_of_the_month/" + dataval.get("image").toString(), dataval.get("type").toString() + "-" + String.valueOf(data.length()) + "%" + dataval.get("author").toString() + "|" + dataval.get("title").toString()).get(15000,TimeUnit.MILLISECONDS);
                                    loader++;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (TimeoutException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            if (!internal.has(key)) {
                                try {
                                    new LoadImage().execute(imageApiURL + "notices/images/" + dataval.get("image").toString(), dataval.get("type").toString() + "-" + String.valueOf(data.length()) + "%" + key + "|" + dataval.get("link").toString()).get(15000, TimeUnit.MILLISECONDS);
                                    loader++;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (TimeoutException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if(loader == 0){
                        launchHome();
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(Cover.this,e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void updateImage(String filename, String link, String key, String type) throws UnsupportedEncodingException {
        File file = new File(dir, filename);
        if(file.exists()){
            if(type.equals("botw")){
                String[] addvalues = {key, link.substring(0,link.indexOf("---")), link.substring(link.indexOf("---") + 3), filename};
                dbhandler.addData(3, addvalues);
                //Toast.makeText(this, key + " " + link + " " + type + " " + filename, Toast.LENGTH_LONG).show();
            }
            else{
                String[] addvalues = {key, filename, URLEncoder.encode(link, "UTF-8")};
                dbhandler.addData(2, addvalues);
                //Toast.makeText(this, key + " " + URLEncoder.encode(link, "UTF-8") + " " + type + " " + filename, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateNotice(){
        String urlString = apiURL + actString + ".php";
        String newsString = apiURL + "daily_news" + ".php?action=homepage";
        spinner.setVisibility(View.VISIBLE);
        if(isConnected()) {
            new APICall().execute(urlString);
            new APICall().execute(newsString);
        }
    }

    public void launchHome(){
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    Intent i = new Intent(Cover.this, homepage.class);
                    startActivity(i);
                    finish();
                }
            }, 3000
        );
    }
}
