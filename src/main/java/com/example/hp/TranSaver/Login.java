package com.example.hp.TranSaver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Password = "passwordKey";
    public static final String Email = "emailKey";

    public static SharedPreferences sharedpreferences;
    static TextView error_text;
    static ProgressDialog progress;
    String email;
    String password;

    static int autologin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!new PrefManager(this).isUserLogedOut()) {
            //user's email and password both are saved in preferences
            // new LoginActivity(Login.this).execute(new PrefManager(this).getEmail(),new PrefManager(this).getPassword());
            if (email == null || password == null) {
                PrefManager p = new PrefManager(this);
                email = p.getEmail();
                password = p.getPassword();
                EditText t1 = (EditText) findViewById(R.id.email_1);
                t1.setText(email);
                EditText t2 = (EditText) findViewById(R.id.password);
                t2.setText(password);
                autologin = 1;
            } else {
                Intent intent2 = new Intent(this, Home.class);
                this.startActivity(intent2);
            }
        }
        error_text = (TextView) findViewById(R.id.error_txt_login);
        Button LoginButton = (Button) findViewById(R.id.loginbutton);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        progress = new ProgressDialog(this);
        progress.setMessage("loading......");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);//we don't know how much time it is going to take.
        progress.setCanceledOnTouchOutside(false);//when anything else is touched the dialog doesn't disappear
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progress.dismiss();
                    }
                }, 3000);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get data that user enter to log-in
                EditText t1 = (EditText) findViewById(R.id.email_1);
                email = t1.getText().toString();
                EditText t2 = (EditText) findViewById(R.id.password);
                password = t2.getText().toString();
                if (t1.getText().toString().trim().length() == 0) {
                    t1.setError("User Name is required!");
                } else if (t2.getText().toString().trim().length() == 0) {
                    t2.setError("Password is required!");
                } else {
                    if (sharedpreferences == null) {
                        saveLoginDetails(email, password);
                    }
                    error_text.setText("");
                    progress.show();
                    new LoginActivity(Login.this).execute(email, password);
                }
            }
        });
    }

    private void saveLoginDetails(String email, String password) {
        new PrefManager(this).saveLoginDetails(email, password);
    }

    public void signup_link(View view) {
        Intent int_sign = new Intent(Login.this, Signup.class);
        startActivity(int_sign);
    }

    public void forgetpassword_link(View view) {
        Intent int_forgetpass = new Intent(Login.this, ForgetPassword.class);
        startActivity(int_forgetpass);
    }
}