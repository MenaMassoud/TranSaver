package com.example.hp.TranSaver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.example.hp.TranSaver.ForgetPassword.Email_p;
import static com.example.hp.TranSaver.ForgetPassword.no_email_found;

public class Send_Email extends AsyncTask<String, Void, String> {
    private final String TAG = Send_Email.class.getName();
    private static Context mContext;
    private ProgressDialog progressDialog;
    String jsonStr;
    String password;
    String message;
    String subject = "your forgeted password";
    private Session session;

    //constructor
    Send_Email(Context mContext) {

        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(mContext, "Sending Email", "Please wait...", false, false);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            Email_p = params[0];
            Log.e("TAG", "your email" + Email_p);
            String link = "https://trackingsystem.000webhostapp.com/forget_pass.php?email=" + Email_p;
            HttpHandler sh = new HttpHandler();

            jsonStr = sh.makeServiceCall(link);
            Log.e(TAG, "Response from url: " + jsonStr);
            while (jsonStr == null || jsonStr.equals("Connection failed: MySQL server has gone away")) {
                jsonStr = sh.makeServiceCall(link);
            }
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if (jsonObj.length() > 0) {
                        Properties props = new Properties();
                        password = jsonObj.getString("password");
                        message = "your password is " + password;
                        Log.e("TAG", "password" + password);
                        //Email="omniascience811@gmail.com";
                        props.put("mail.smtp.host", "smtp.gmail.com");
                        props.put("mail.smtp.socketFactory.port", "465");
                        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                        props.put("mail.smtp.auth", "true");
                        props.put("mail.smtp.port", "465");

                        //Creating a new session
                        session = Session.getDefaultInstance(props,
                                new javax.mail.Authenticator() {
                                    //Authenticating the password
                                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                                        return new javax.mail.PasswordAuthentication(Config.EMAIL_from, Config.PASSWORD_from);
                                    }
                                });
                        // message="your password"+password;
                        Log.e("TAG", "message:" + message);
                        //Creating MimeMessage object
                        MimeMessage mm = new MimeMessage(session);

                        //Setting sender address
                        mm.setFrom(new InternetAddress(Config.EMAIL_from));
                        //Adding receiver
                        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(Email_p));
                        //Adding subject
                        mm.setSubject(subject);
                        //Adding message
                        mm.setText(message);

                        //Sending email
                        Transport.send(mm);

                    }
                } catch (Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {

                Log.e("failed", "password doesn't sent");
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
            progressDialog.dismiss();
            Toast.makeText(mContext, "Email Sent", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.dismiss();
            no_email_found.setText("This email doesnt exist");
        }
    }

}