package com.example.hp.TranSaver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runDialog();
        getSupportActionBar().hide();

        Button loginButton = (Button) findViewById(R.id.button_log_in);
        Button signupButton = (Button) findViewById(R.id.button_sign_up);

        //logo animation
        ImageView imageviewy = (ImageView) findViewById(R.id.yellow);
        imageviewy.setVisibility(View.INVISIBLE);
        ImageView imageviewr = (ImageView) findViewById(R.id.red);
        imageviewr.setVisibility(View.INVISIBLE);
        ImageView imageviewb = (ImageView) findViewById(R.id.blue);
        imageviewb.setVisibility(View.INVISIBLE);
        ImageView imageviewd = (ImageView) findViewById(R.id.dark_blue);
        imageviewd.setVisibility(View.INVISIBLE);

        int xDesty;
        xDesty = (imageviewy.getMeasuredWidth());
        int yDesty;
        yDesty = imageviewy.getMeasuredHeight();
        TranslateAnimation anim = new TranslateAnimation(-400.0f, xDesty, -400.0f, yDesty);
        anim.setDuration(2000);
        anim.setFillAfter(true);
        imageviewy.startAnimation(anim);

        int xDestr;
        xDestr = (imageviewr.getMeasuredWidth());
        int yDestr;
        yDestr = imageviewr.getMeasuredHeight();
        TranslateAnimation anim2 = new TranslateAnimation(400.0f, xDestr, -400.0f, yDestr);
        anim2.setDuration(2000);
        anim2.setFillAfter(true);
        imageviewr.startAnimation(anim2);

        int xDestb;
        xDestb = (imageviewb.getMeasuredWidth());
        int yDestb;
        yDestb = imageviewb.getMeasuredHeight();
        TranslateAnimation anim3 = new TranslateAnimation(400.0f, xDestb, 400.0f, yDestb);
        anim3.setDuration(2000);
        anim3.setFillAfter(true);
        imageviewb.startAnimation(anim3);

        int xDestd;
        xDestd = (imageviewd.getMeasuredWidth());
        int yDestd;
        yDestd = imageviewd.getMeasuredHeight();
        TranslateAnimation anim4 = new TranslateAnimation(-400.0f, xDestd, 400.0f, yDestd);
        anim4.setDuration(2000);
        anim4.setFillAfter(true);
        imageviewd.startAnimation(anim4);

        //2 buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

    }

    public void runDialog() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.alert_dialog, null));
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public void startNow(View view) {
        Toast.makeText(this, "button clicked!", Toast.LENGTH_LONG).show();
        dialog.dismiss();
    }
}