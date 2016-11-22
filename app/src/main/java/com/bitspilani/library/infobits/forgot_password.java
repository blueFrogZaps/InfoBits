package com.bitspilani.library.infobits;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bitspilani.library.infobits.R;

public class forgot_password extends AppCompatActivity {

    EditText input_mail;
    private  String reset_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        input_mail = (EditText) findViewById(R.id.input_mail);
        reset_mail = input_mail.getText().toString();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }


    public void OnClick_reset(View view) {
        Context context = getApplicationContext();
        if (reset_mail.isEmpty()) {
            Toast toast = Toast.makeText(context, "Please enter your BITS mail", Toast.LENGTH_LONG);
            toast.show();
        } else {
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute("reset", reset_mail);

        }
    }
}
