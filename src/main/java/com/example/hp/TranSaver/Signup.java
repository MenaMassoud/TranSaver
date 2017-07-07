package com.example.hp.TranSaver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import static com.example.hp.TranSaver.Login.progress;

public class Signup extends AppCompatActivity {

    EditText t3, t5;
    static EditText t4;
    static TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        t3 = (EditText) findViewById(R.id.user_name2);
        t4 = (EditText) findViewById(R.id.email2);

        t5 = (EditText) findViewById(R.id.password2);

        textView1 = (TextView) findViewById(R.id.error_txt_signup);
        progress = new ProgressDialog(this);
        progress.setMessage("loading......");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);//we don't know how much time it is going to take.
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // onLoginSuccess();
                        // onLoginFailed();
                        progress.dismiss();
                    }
                }, 3000);
        Button sig = (Button) findViewById(R.id.button_sign_Up);
        sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // variables contains data that user enter
                String username = t3.getText().toString();
                String email = t4.getText().toString();
                String password = t5.getText().toString();

                if (t3.getText().toString().trim().length() == 0) {
                    t3.setError("User name is required!");
                } else if (t4.getText().toString().trim().length() == 0) {
                    t4.setError("Email is required!");
                } else if (t4.getText().toString().trim().length() > 0 && !checkEmail(email)) {
                    t4.setError("Invalid email");
                } else if (t5.getText().toString().trim().length() == 0) {
                    t5.setError("Password is required!");
                } else {
                    //spinner.setVisibility(View.VISIBLE);
                    progress.show();
                    new SignupActivity(Signup.this).execute(username, email, password);

                }

            }
        });
    }

    public void login_link(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public static boolean checkEmail(String email) {
        Pattern EMAIL_ADDRESS_PATTERN = Pattern
                .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + ")+");
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public class SignupActivity extends AsyncTask<String, Void, String> {
        private final String TAG = SignupActivity.class.getName();
        private Context mContext;
        String jsonStr;
        String name, email, password;

        //constructor
        public SignupActivity(Context mContext) {

            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... params) {

            name = params[0];
            email = params[1];
            password = params[2];
            String link = "https://trackingsystem.000webhostapp.com/signup.php?name=" + name + "&email=" + email + "&password=" + password;
            HttpHandler sh = new HttpHandler();

            jsonStr = sh.makeServiceCall(link);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr == null) {
                try {

                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.e("data inserted", "successfully");
                    return sb.toString();
                } catch (Exception e) {
                    Log.e("unable to connect", "error");
                    return new String("Exception: " + e.getMessage());

                }

            } else {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if (jsonObj.length() > 0) {
                        String user_name = jsonObj.getString("name");
                        String user_email = jsonObj.getString("email");
                        Log.e("TAG", "user_name:" + user_name);
                        Log.e("TAG", "user_email:" + user_email);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progress.dismiss();
                        }
                    }, 3000);
            if (jsonStr != null) {

                t4.setError("email already exist");
            } else {
                new SendVerificationMail(mContext).execute(name, email, password);

            }

        }
    }
}