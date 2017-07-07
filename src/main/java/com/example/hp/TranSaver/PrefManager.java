package com.example.hp.TranSaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.example.hp.TranSaver.LoginActivity.id;

public class PrefManager {

    Context context;
    private final String TAG = PrefManager.class.getSimpleName();

    PrefManager(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        Log.v(TAG, email);
        editor.putString("Password", password);
        Log.v(TAG, password);
        editor.commit();
    }

    public void saveID(String Id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SaveId", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ID", Id);
        id = sharedPreferences.getString("SaveId", id);
        Log.v(TAG, id);
        editor.commit();
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

    public String getPassword() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Password", "");
    }

    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
        return isEmailEmpty || isPasswordEmpty;
    }
}