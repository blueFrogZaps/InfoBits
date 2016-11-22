package com.bitspilani.library.infobits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitspilani.library.infobits.R;

/**
 * Created by Abhishek on 3/10/2016.
 */
public class signup extends AppCompatActivity {

    private EditText name, bitsid, bitsmail, pass, retype_pass;
    private Button bt_signin,bt_login_page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = (EditText)findViewById(R.id.brand);
        bitsid = (EditText)findViewById(R.id.bitsid);
        bitsmail = (EditText)findViewById(R.id.bitsmail);
        pass = (EditText)findViewById(R.id.pass);
        retype_pass = (EditText)findViewById(R.id.retype_pass);
        bt_login_page = (Button)findViewById(R.id.bt_login_page);
        bt_signin = (Button)findViewById(R.id.bt_signin);
        setContentView(R.layout.user_register);
    }


    public void OnClick_login_page(View view){
        Intent i=new Intent(signup.this,login.class);
        startActivity(i);
    }

    public void OnClick_sign_up(View view) {
        String Name = name.getText().toString();
        String Bitsid = bitsid.getText().toString();
        String Bitsmail = bitsmail.getText().toString();
        String Pass = pass.getText().toString();
        String Retype_pass = retype_pass.getText().toString();
        Context context = getApplicationContext();
        if (Name.isEmpty() || Bitsid.isEmpty() || Bitsmail.isEmpty() || Pass.isEmpty() || Retype_pass.isEmpty()) {
            Toast toast = Toast.makeText(context, "Please fill in all the fields.", Toast.LENGTH_LONG);
            toast.show();
        } else {
            if (!(Pass.equals(Retype_pass))) {
                pass.setText("");
                retype_pass.setText("");
                Toast toast = Toast.makeText(context, "Passwords don't match.", Toast.LENGTH_LONG);
                toast.show();
            } else {
                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute("register",Name,Bitsid,Bitsmail,Pass);

            }
        }
    }



    public void userReg(){

    }
}
