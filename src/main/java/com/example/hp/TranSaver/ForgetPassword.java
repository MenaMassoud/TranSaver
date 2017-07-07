package com.example.hp.TranSaver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

//public class ForgetPassword extends AppCompatActivity {
//    public static String Email_p;
//    private static Context mContext;
//    public static TextView no_email_found;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_forget_password);
//        getSupportActionBar().hide();
//    }
//    public  void reset(View view)
//    {
//        no_email_found= (TextView) findViewById(R.id.no_email_found);
//        EditText tp = (EditText)findViewById(R.id.email_forgetpass);
//        Email_p = tp.getText().toString().trim(); //email to get its password
//        Log.e("TAG", "your email"+ Email_p);
//        tp.setText("");
//        no_email_found.setText("");
//        if(tp.getText().toString().trim().length()==0)
//        {
//            tp.setError("Required");
//        }
//        else
//        {
//            //call the class to send mail with password
//            new Send_Email(mContext).execute(Email_p);
//        }
//    }
//
//    public void login_fromforget(View view) {
//        Intent login = new Intent(ForgetPassword.this,Login.class);
//        startActivity(login);
//    }
//}
public class ForgetPassword extends AppCompatActivity {

    public static String Email_p;
    private static Context mContext;
    public static TextView no_email_found;
private final String TAG = ForgetPassword.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().hide();
    }

    public void reset(View view) {
        no_email_found = (TextView) findViewById(R.id.no_email_found);
        EditText tp = (EditText) findViewById(R.id.email_forgetpass);
        Email_p = tp.getText().toString().trim(); //email to get its password
        Log.e(TAG, "your email" + Email_p);


        if (tp.getText().toString().trim().length() == 0) {
            tp.setError("Required");
        } else {
            //call the class to send mail with password
            new Send_Email(ForgetPassword.this).execute(Email_p);
        }
        tp.setText("");
        no_email_found.setText("");
    }
    public void login_fromforget(View view) {
        Intent login = new Intent(ForgetPassword.this,Login.class);
        startActivity(login);
    }
}