package com.bitspilani.library.infobits;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bitspilani.library.infobits.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class login extends homepage {

    private EditText username;
    private EditText password;
    private Button bt_signin, bt_forgot;
    private ImageButton bt_show_pass;
    String UserName, Password;
    ProgressBar spinner;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login_info = getSharedPreferences("login_info", MODE_PRIVATE);
        setContentView(R.layout.user_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        username = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        bt_forgot = (Button) findViewById(R.id.bt_forgot);
        bt_signin = (Button) findViewById(R.id.bt_signin);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        bt_show_pass = (ImageButton) findViewById(R.id.showPass);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void OnClick_sign_in(View view) {
        if(isConnected()) {
            UserName = username.getText().toString();
            Password = password.getText().toString();
            if (UserName.isEmpty() || Password.isEmpty()) {
                Toast.makeText(login.this, "Please fill in all the fields.", Toast.LENGTH_LONG).show();
            } else {
                String urlString = apiURL + "login.php?username=" + UserName + "&password=" + Password;
                new APICall().execute(urlString);
                toggleInterface(View.VISIBLE, false);
            }
        }
    }

    public void showPass(View view) {
        Password = password.getText().toString();
        if (!Password.isEmpty()) {
            if(password.getTransformationMethod() == null){
                password.setTransformationMethod(new PasswordTransformationMethod());
                bt_show_pass.clearColorFilter();
            }
            else{
                password.setTransformationMethod(null);
                bt_show_pass.setColorFilter(R.color.colorPrimaryDark);
            }
        }
    }

    public void OnClick_forgot_pass(View view) {
        if(isConnected()) {
            UserName = username.getText().toString();
            if (UserName.isEmpty()) {
                Toast.makeText(login.this, "Please fill the username.", Toast.LENGTH_LONG).show();
            } else {
                String urlString = apiURL + "forgot_password.php?username=" + UserName;
                new APICall().execute(urlString);
                toggleInterface(View.VISIBLE, false);
            }
        }
    }


    public void userReg(View view) {
        if(isConnected()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(openURL + "account/signup.php"));
            startActivity(browserIntent);
        }
    }

    private class APICall extends AsyncTask<String,Integer,String> {

        String err;
        @Override
        protected String doInBackground(String[] params){
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
                    login_user(json);
                } catch (Exception e) {
                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    toggleInterface(View.GONE, true);
                }
            }
            else{
                if(!err.isEmpty()){
                    Toast.makeText(login.this, err, Toast.LENGTH_LONG).show();
                    toggleInterface(View.GONE, true);
                }
            }
        }
    }

    public void login_user(JSONObject json){
        try{
            if(!json.get("data").toString().equals("[]")) {
                JSONObject data = (JSONObject) json.get("data");
                edit_login_info.putString("username", data.get("userid").toString());
                edit_login_info.putString("name", data.get("name").toString());
                edit_login_info.putString("email", data.get("email").toString());
                edit_login_info.putString("category", data.get("category").toString());
                edit_login_info.putString("password", Password);
                edit_login_info.putString("avatar", data.get("avatar").toString());
                if(edit_login_info.commit()){
                    String avatar = login_info.getString("avatar","");
                    if(!avatar.isEmpty()){
                        File profilepic = new File(dir, avatar);
                        if(!profilepic.exists()){
                            new LoadImage().execute(imageApiURL + "profilepics/" + avatar);
                        }
                        else{
                            launchHome();
                        }
                    }else{
                        launchHome();
                    }
                }else{
                    Toast.makeText(login.this,"Something went Wrong!",Toast.LENGTH_LONG).show();
                }
            }
            else if(json.has("err_message") && !json.get("err_message").toString().isEmpty()){
                Toast.makeText(login.this,json.get("err_message").toString(),Toast.LENGTH_LONG).show();
                toggleInterface(View.GONE, true);
            }
        } catch(JSONException e) {
            Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
            toggleInterface(View.GONE, true);
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        String filename;
        protected Bitmap doInBackground(String[] args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            filename = args[0].substring(args[0].lastIndexOf("/") + 1);
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            File file = new File(dir, filename);
            FileOutputStream fileOut;
            String ext = filename.substring(filename.lastIndexOf("."));
            try {
                fileOut = new FileOutputStream(file);
                if(ext.equals(".jpg") || ext.equals(".jpeg")){
                    image.compress(Bitmap.CompressFormat.JPEG, 50, fileOut);
                }
                else{
                    image.compress(Bitmap.CompressFormat.PNG, 50, fileOut);
                }
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            launchHome();
        }
    }

    public void launchHome(){
        finishAffinity();
        Intent i12 = new Intent(login.this, homepage.class);
        startActivity(i12);
    }

    public void toggleInterface(int spinview, Boolean handle){
        spinner.setVisibility(spinview);
        username.setEnabled(handle);
        password.setEnabled(handle);
        bt_signin.setClickable(handle);
        bt_forgot.setClickable(handle);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bitspilani.library.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bitspilani.library.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
