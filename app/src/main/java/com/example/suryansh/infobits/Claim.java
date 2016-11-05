package com.example.suryansh.infobits;

import android.app.Activity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Claim extends lfmsAllItems {

    TextView particulars, found;
    Button claim;
    public static String sno;
    JSONObject details;
    private ProgressDialog pDialog;
    private static final String url_update_item = apiURL + "/update_item.php?username=" + username + "&password=" + password + "&sno=";
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"), ndf = new SimpleDateFormat("MMM dd yyyy hh:mm a");
    Date date;
    Integer success;
    DBHandler dbhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);
        claim = (Button) findViewById(R.id.claim);
        particulars = (TextView) findViewById(R.id.particulars);
        found = (TextView) findViewById(R.id.found);
        toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        Bundle b = getIntent().getExtras();
        sno = b.get("sno").toString();
        dbhandler = new DBHandler(this, null, null);
        try {
            details = ((JSONObject) dbhandler.selectData(4,"id = " + sno).get(sno));
            particulars.setText("Particulars: " + details.get("particulars").toString() + "\n\nBrand: " + details.get("brand").toString());
            String found_at = details.get("date").toString() + " " + details.get("time").toString();
            try {
                date = df.parse(found_at);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            found.setText("Found: " + ndf.format(date));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(isConnected()){
                    new SaveItemDetails().execute(url_update_item + sno);
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
    }

    class SaveItemDetails extends AsyncTask<String, Integer, String> {

        String err;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Claim.this);
            pDialog.setMessage("Setting Claim ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
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
            if (!result.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(result);
                    updateStatus(json);
                } catch (Exception e) {
                    Toast.makeText(Claim.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                if (!err.isEmpty()) {
                    Toast.makeText(Claim.this, err, Toast.LENGTH_LONG).show();
                }
            }
            pDialog.dismiss();
        }
    }

    public void updateStatus(JSONObject json){
        try{
            if(json.has("err_message") && !json.get("err_message").toString().isEmpty()){
                Toast.makeText(Claim.this, json.get("err_message").toString(), Toast.LENGTH_LONG).show();
            }
            else if(json.has("message") && !json.get("message").toString().isEmpty()){
                Toast.makeText(Claim.this, json.get("message").toString(), Toast.LENGTH_LONG).show();
                if(json.has("success") && !json.get("success").toString().isEmpty()){
                    success = Integer.parseInt(json.get("success").toString());
                    String[] status = {"0"};
                    dbhandler.updateData(4,status,Integer.parseInt(sno));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Claim.this, "3", Toast.LENGTH_LONG).show();
        }
        if(success == 1){
            Intent resultIntent = new Intent(Claim.this,lfmsAllItems.class);
            resultIntent.putExtra("update", success);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
