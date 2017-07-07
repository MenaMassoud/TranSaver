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


public class SendVerificationMail extends AsyncTask<String, Void, String> {
    private final String TAG = SendVerificationMail.class.getName();

    private static Context mContext;
    private ProgressDialog progressDialog;
    String jsonStr;
    String message;
    String subject = "Signup | Verification";
    private Session session;
    String hash, name, password, email;

    //constructor
    SendVerificationMail(Context mContext) {

        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(mContext, "Sending verification Email", "Please wait...", false, false);
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            name = params[0];
            email = params[1];
            password = params[2];
            ;
            Log.e("TAG", "your email " + email);
            String link = "https://trackingsystem.000webhostapp.com/selectHash.php?email=" + email;
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
                        hash = jsonObj.getString("hash");
                        message = "please go to this link to activate your account \n" + "https://trackingsystem.000webhostapp.com/verify.php?email=" + email + "&hash=" + hash
                                + "\n \n \n Best of luck from TranSaver Team";
                        Log.e("hash=", hash);

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
                        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                        //Adding subject
                        mm.setSubject(subject);
                        //Adding message
                        mm.setText(message);

                        //Sending email
                        Transport.send(mm);

                    }
                } catch (Exception e) {
                    Log.e(TAG, " error: " + e.getMessage());
                }
            } else {

                Log.e("failed", "hash doesn't sent");
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
        progressDialog.dismiss();
        CharSequence text = "Please check your email to activate your account";
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        toast.show();

    }


}
