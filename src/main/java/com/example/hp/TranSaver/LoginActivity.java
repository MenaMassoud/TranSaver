package com.example.hp.TranSaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import static com.example.hp.TranSaver.Login.error_text;
import static com.example.hp.TranSaver.Login.progress;


class LoginActivity extends AsyncTask<String, Void, String> {

    static String id;
    private Context mContext;
    private String jsonStr;
    String email;
    String password;
    private final String TAG = LoginActivity.class.getSimpleName();

    LoginActivity(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            email = params[0];
            password = params[1];
            String link = "https://trackingsystem.000webhostapp.com/login.php?email=" + email + "&password=" + password;
            HttpHandler sh = new HttpHandler();

            jsonStr = sh.makeServiceCall(link);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (Login.autologin == 1) {
                while (jsonStr == null) {
                    jsonStr = sh.makeServiceCall(link);
                }
            }

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if (jsonObj.length() > 0) {
                        id = jsonObj.getString("id");
                        new PrefManager(mContext).saveID(id);
                        String user_name = jsonObj.getString("name");
                        String user_email = jsonObj.getString("email");
                        Log.e("TAG", "user_name:" + user_name);
                        Log.e("TAG", "user_email:" + user_email);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("failed", "email or password is incorrect");
                SharedPreferences preferences = mContext.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
            }

        } catch (Exception e) {
            Log.e("connection", "error");
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (jsonStr != null) {
            Intent intent2 = new Intent(mContext, Home.class);
            mContext.startActivity(intent2);

        } else {

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progress.dismiss();
                        }
                    }, 3000);
            error_text.setText("email or password is incorrect");
        }
    }
}