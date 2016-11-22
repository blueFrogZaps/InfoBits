package com.bitspilani.library.infobits;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;
import com.bitspilani.library.infobits.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundTask extends AsyncTask<String,Void,String>{
    String register_url ="register.php";
    String login_url = "login.php";
    String reset_url = "reset.php";

    Context context;
    Activity activity;
    AlertDialog.Builder builder;
    ProgressDialog progressdialog;



    public BackgroundTask() {
        this.context=context;
        activity =(Activity)context;
    }

    @Override
    protected void onPreExecute() {
       builder = new AlertDialog.Builder(context);
        progressdialog = new ProgressDialog(context);
        progressdialog.setTitle("Please Wait...");
        progressdialog.setMessage("Connecting to Server....");
        progressdialog.setIndeterminate(true);
        progressdialog.setCancelable(false);
        progressdialog.show();

    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0];
        if(method.equals("register")){
            try {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String NAME = params[1];
                String BITSID = params[2];
                String BITSMAIL = params[3];
                String PASS = params[4];
                String data = URLEncoder.encode("Name","UTF-8")+"="+URLEncoder.encode(NAME,"UTF-8")+"&"+
                        URLEncoder.encode("BITSID","UTF-8")+"="+URLEncoder.encode(BITSID,"UTF-8")+"&"+
                        URLEncoder.encode("Bitsmail","UTF-8")+"="+URLEncoder.encode(BITSMAIL,"UTF-8")+"&"+
                        URLEncoder.encode("Pass","UTF-8")+"="+URLEncoder.encode(PASS,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
                //Read the response
                StringBuilder stringBuilder =new StringBuilder();
                String line= "";
                while((line=bufferedReader.readLine())!=null ){
                    stringBuilder.append("\n");
                }
                httpURLConnection.disconnect();
                Thread.sleep(5000);
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("login")){
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream =httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String EMAIL = params[1];
                String PASSWORD = params[2];
                String data = URLEncoder.encode("Email","UTF-8")+"="+URLEncoder.encode(EMAIL,"UTF-8")+"&"+
                        URLEncoder.encode("Password","UTF-8")+"="+URLEncoder.encode(PASSWORD,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
                //Read the response
                StringBuilder stringBuilder =new StringBuilder();
                String line= "";
                while((line=bufferedReader.readLine())!=null ){
                    stringBuilder.append("\n");
                }
                httpURLConnection.disconnect();
                Thread.sleep(5000);
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("reset")){
            try {
                URL url = new URL(reset_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream =httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String RESET_MAIL = params[1];
                String data = URLEncoder.encode("Reset_Mail","UTF-8")+"="+URLEncoder.encode(RESET_MAIL,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String json) {
        //decode the json data
        try {
            progressdialog.dismiss();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            JSONObject jo = jsonArray.getJSONObject(0);
            String code = jo.getString("code");
            String message = jo.getString("message");
            if(code.equals("reg_true")){
                showDialog("Registration Success",message,code);
            }else if (code.equals("reg_false")) {
                showDialog("Registration Failed",message,code);
            }else if(code.equals("login_true")){
                Intent intent = new Intent(activity,homepage.class);
                intent.putExtra("message",message);
                activity.startActivity(intent);
            } else if(code.equals("login_false")){
                showDialog("Login failed...",message,code);
            }else if(code.equals("reset_true")) {
                showDialog("Password Reset Succesful....",message,code);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showDialog(String title, String message, String code){
        builder.setTitle(title);
        if(code.equals("reg_true")||code.equals("reg_false")){
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
        }
        else if(code.equals("login_false")){

            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText email, password;
                    email = (EditText) activity.findViewById(R.id.email);
                    password = (EditText) activity.findViewById(R.id.pass);

                    email.setText("");
                    password.setText("");
                    dialog.dismiss();
                }
            });

        }else if(code.equals("reset_true")) {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}