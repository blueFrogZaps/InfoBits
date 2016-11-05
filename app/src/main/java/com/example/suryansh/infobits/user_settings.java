package com.example.suryansh.infobits;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.example.suryansh.infobits.Responses.UserSettingsResponse;
import com.example.suryansh.infobits.network.VolleySingleton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import java.util.Hashtable;
import com.android.volley.AuthFailureError;
import android.graphics.drawable.BitmapDrawable;

import org.json.JSONException;
import org.json.JSONObject;

public class user_settings extends homepage implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE =1;
    EditText mobile;
    TextInputLayout oldPassword;
    TextInputLayout newPassword;
    TextInputLayout confirmPassword;
    Button uploadBtn;
    Button updateMobile;
    Button updatePassword;
    TextView changePassword;
    ImageButton imageButton;
    ImageView image;
    LinearLayout nameLayout;
    LinearLayout emailLayout;
    LinearLayout mobileLayout;
    ProgressBar spinner;
    Class previous;
    private ImageLoader mImageLoader;
    private GoogleApiClient client;
    String imageString;
    Boolean reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);
        login_info = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        edit_login_info = login_info.edit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reload = false;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        Bundle b = getIntent().getExtras();
        previous = (Class) b.get("previous");
        image = (ImageView) findViewById(R.id.profile);
        mobile = (EditText) findViewById(R.id.mobileText);
        changePassword = (TextView) findViewById(R.id.changePassword);
        oldPassword = (TextInputLayout) findViewById(R.id.oldPassLayout);
        newPassword = (TextInputLayout) findViewById(R.id.newPassLayout);
        confirmPassword = (TextInputLayout) findViewById(R.id.confirmPassLayout);
        uploadBtn = (Button) findViewById(R.id.upload);
        updateMobile = (Button) findViewById(R.id.update1);
        updatePassword = (Button) findViewById(R.id.update2);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        nameLayout = (LinearLayout) findViewById(R.id.NameLayout);
        emailLayout = (LinearLayout)findViewById(R.id.EmailLayout);
        mobileLayout = (LinearLayout) findViewById(R.id.MobileLayout);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        File profilepic = new File(dir, avatar);
        try {
            fileInput = new FileInputStream(profilepic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileInput == null){
            image.setImageResource(R.mipmap.logo);
        }else{
            image.setImageBitmap(BitmapFactory.decodeStream(fileInput));
            setToolBarAvatar(profilepic);
        }
        uploadBtn.setOnClickListener(this);
        updateMobile.setOnClickListener(this);
        updatePassword.setOnClickListener(this);
        imageButton.setOnClickListener(this);

        if (isConnected()) {
            spinner.setVisibility(View.VISIBLE);
            serverCalls("User Settings");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab:
                if (uploadBtn.getVisibility() == Button.VISIBLE) {
                   showHideEdits(true);
                } else {
                    showHideEdits(false);
                }

                break;

            case R.id.update1:
                Snackbar.make(v, "Mobile number update Server call", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (isConnected()) {
                    spinner.setVisibility(View.VISIBLE);
                    serverCalls("Update Mobile");
                } else {
                    Toast.makeText(getApplicationContext(), "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.update2:
                Snackbar.make(v, "Password update Server call", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                boolean validity = checkPasswordValidity(newPassword.getEditText().getText().toString(), confirmPassword.getEditText().getText().toString());
                if (validity){
                    if (isConnected()) {
                        spinner.setVisibility(View.VISIBLE);
                        serverCalls("Change Password");
                    } else {
                        Toast.makeText(getApplicationContext(), "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.upload:
                Snackbar.make(v, "Upload Image Server call", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (isConnected()) {
                    spinner.setVisibility(View.VISIBLE);
                    uploadImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Not Connected to BITS Intranet!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imageButton:
                    showFileChooser();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!= null){
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "user_settings Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.suryansh.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "user_settings Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.suryansh.infobits/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(reload) {
            finishAffinity();
            Intent i12 = new Intent(user_settings.this, homepage.class);
            startActivity(i12);
        }
    }

    public void serverCalls(String type){

        spinner.setVisibility(View.VISIBLE);
        // Instantiate the RequestQueue.
        final RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        switch (type){
            case "User Settings":{
                String url = apiURL + "user_settings.php?username=" + username +"&password=" + password;
                // Request a string response from the provided URL.

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            spinner.setVisibility(View.GONE);
                            nameLayout.setVisibility(LinearLayout.VISIBLE);
                            emailLayout.setVisibility(LinearLayout.VISIBLE);
                            image.setVisibility(ImageView.VISIBLE);
                            mobileLayout.setVisibility(LinearLayout.VISIBLE);
                            FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
                            fab.setVisibility(FloatingActionButton.VISIBLE);
                            updateUserDetails(response, queue);
                           // Toast.makeText(getApplicationContext(), "Response is: "+ response, Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            spinner.setVisibility(View.GONE);
                            nameLayout.setVisibility(LinearLayout.VISIBLE);
                            emailLayout.setVisibility(LinearLayout.VISIBLE);
                            image.setVisibility(ImageView.VISIBLE);
                            TextView userName = (TextView) findViewById(R.id.textView2);
                            userName.setText(name);
                            TextView emailID = (TextView) findViewById(R.id.textView4);
                            emailID.setText(email);
                            File profilepic = new File(dir, avatar);
                            try {
                                fileInput = new FileInputStream(profilepic);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(fileInput == null){
                                image.setImageResource(R.mipmap.logo);
                            }else{
                                image.setImageBitmap(BitmapFactory.decodeStream(fileInput));
                            }
//
                            Toast.makeText(getApplicationContext(), "ERROR: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                break;
            }

            case "Change Password":{
                String url = apiURL + "user_settings.php?username="+username +"&password="+password+"&new_value="+ newPassword.getEditText().getText().toString() +"&change_type=0";
                updateCall(url, queue);
                break;
            }
            case "Update Mobile":{
                String url = apiURL + "user_settings.php?username="+username +"&password="+password+"&new_value="+ mobile.getText().toString() +"&change_type=1";
                updateCall(url, queue);
                break;
            }
            default:
                break;
        }
    }

    private void updateUserDetails(String json, RequestQueue queue){
        UserSettingsResponse userResponse = new UserSettingsResponse(json);
        userResponse.parseJSON();
        TextView userName = (TextView) findViewById(R.id.textView2);
        userName.setText(userResponse.name);
        TextView emailID = (TextView) findViewById(R.id.textView4);
        emailID.setText(userResponse.email);
        TextView mobileNo = (TextView) findViewById(R.id.mobileText);
        mobileNo.setText(userResponse.mobile);
//        mImageLoader = VolleySingleton.getInstance().getImageLoader();
//        //Image URL - This can point to any image file supported by Android
//        mImageLoader.get(userResponse.imageUrl, ImageLoader.getImageListener(image,
//                R.mipmap.logo, R.mipmap.logo));
//        image.setImageUrl(userResponse.imageUrl, mImageLoader);
    }

    private boolean checkPasswordValidity(String password, String confirmPassword){
        if (password.equals(confirmPassword)){
            return true;
        }
        else {
            return false;
        }
    }
    private void showHideEdits(Boolean hide) {
        if (hide) {
            imageButton.setVisibility(Button.INVISIBLE);
            uploadBtn.setVisibility(Button.INVISIBLE);
            updateMobile.setVisibility(Button.INVISIBLE);
            updatePassword.setVisibility(Button.INVISIBLE);
            oldPassword.setVisibility(TextInputLayout.INVISIBLE);
            newPassword.setVisibility(TextInputLayout.INVISIBLE);
            confirmPassword.setVisibility(TextInputLayout.INVISIBLE);
            changePassword.setVisibility(TextView.INVISIBLE);
            mobile.setEnabled(false);
            mobile.setInputType(InputType.TYPE_NULL);
            mobile.setFocusableInTouchMode(false);
            mobile.clearFocus();
        } else {
            imageButton.setVisibility(Button.VISIBLE);
            uploadBtn.setVisibility(Button.VISIBLE);
            updateMobile.setVisibility(Button.VISIBLE);
            updatePassword.setVisibility(Button.VISIBLE);
            oldPassword.setVisibility(TextInputLayout.VISIBLE);
            newPassword.setVisibility(TextInputLayout.VISIBLE);
            confirmPassword.setVisibility(TextInputLayout.VISIBLE);
            changePassword.setVisibility(TextView.VISIBLE);
            mobile.setEnabled(true);
            mobile.setInputType(InputType.TYPE_CLASS_NUMBER);
            mobile.setFocusableInTouchMode(true);
            mobile.requestFocus();
        }
    }

    private void updateCall(String url, RequestQueue queue){

        final String urlS = url;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    spinner.setVisibility(View.GONE);
                    showHideEdits(true);
                    Toast.makeText(getApplicationContext(), "Response is: "+ response, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject json = new JSONObject(response);
                        if(urlS.contains("change_type=0") && json.get("status").toString().equals("1")){
                            edit_login_info.putString("password", urlS.substring(urlS.indexOf("new_value=") + 10, urlS.indexOf("&change_type=")));
                            edit_login_info.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "ERROR: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        );

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){

        final ImageLoader imageLoader = VolleySingleton.getInstance().getImageLoader();

        final String url = apiURL + "user_settings.php?username="+username +"&password="+password+"&change_type=2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), s , Toast.LENGTH_LONG).show();
                    try {
                        JSONObject json = new JSONObject(s);
                        if(url.contains("change_type=2") && json.get("status").toString().equals("1")){
                            File file = new File(dir, json.get("file_name").toString());
                            FileOutputStream fileOut;
                            try {
                                fileOut = new FileOutputStream(file);
                                ((BitmapDrawable) image.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 50, fileOut);
                                edit_login_info.putString("avatar", file.getName());
                                edit_login_info.commit();
                                reload = true;
                                fileOut.close();
                            }catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                imageString = getStringImage( ((BitmapDrawable)image.getDrawable()).getBitmap());

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("new_value", imageString);

                //returning parameters
                return params;
            }

        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), RESULT_LOAD_IMAGE);
    }
}


