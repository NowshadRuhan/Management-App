package com.capsulestudio.schoolmanagement.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.capsulestudio.schoolmanagement.Activity.UserLoginActivity;

import java.util.HashMap;

/**
 * Created by Shuvo on 12/7/2017.
 */

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "school_management";
    public static final String KEY_FNAME = "first_name";
    public static final String KEY_LNAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "pass";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_SCHOOL_NAME = "school_name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_REG_DATE = "reg_date";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    //method to let the student login
    //this method will store the student data in shared preferences
    public void userLogin(int id_user, String fname, String lname, String email, String pass, String mobile, String school_name, String status, String reg_date) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FNAME, fname);
        editor.putString(KEY_LNAME, lname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, pass);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_SCHOOL_NAME, school_name);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_REG_DATE, reg_date);
        editor.apply();
    }

    /*//this method will checker whether user is already logged in or not
    public int isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(KEY_ID_USER, -1);
    }*/

    public HashMap<String, String> getUserDetails(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        HashMap<String, String> user = new HashMap<String, String>();


        user.put(KEY_FNAME, sharedPreferences.getString(KEY_FNAME, null));

        user.put(KEY_LNAME, sharedPreferences.getString(KEY_LNAME, null));

        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));

        user.put(KEY_PASS, sharedPreferences.getString(KEY_PASS, null));

        user.put(KEY_MOBILE, sharedPreferences.getString(KEY_MOBILE, null));

        user.put(KEY_SCHOOL_NAME, sharedPreferences.getString(KEY_SCHOOL_NAME, null));

        user.put(KEY_STATUS, sharedPreferences.getString(KEY_STATUS, null));

        user.put(KEY_REG_DATE, sharedPreferences.getString(KEY_REG_DATE, null));

        // return user
        return user;
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, UserLoginActivity.class));
    }

}
