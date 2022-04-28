package com.interview.practicall.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    //instance field
    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;

    // Jetli Jarur Hoi Etlu Add Karvanu
    public static final String id = "id";
    public static final String currentAddress = "currentAddress";
    public static final String name = "name";
    public static final String lname = "lname";
    public static final String email = "email";
    public static final String image = "image";
    public static final String visitingcard = "visitingcard";
    public static final String mobile = "mobile";
    public static final String companyname = "company name";
    public static final String Address = "Address";
    public static final String AuthToken = "AuthToken";
    public static final String selectedAddressId = "selectedAddressId";
    public static final String selectedAddress = "selectedAddress";
    // boolean value Add
    public static final String YES = "yes";
    public static final String NO = "no";
    //Shared Preference key
    private String KEY_PREFERENCE_NAME = "INTERVIEW"; // put Application Name
    //private keyS
    public String KEY_DEFAULT = null;
    public static String isLogin = "isLoggedIn";
    public static String isFromEmergency = "isFromEmergency";


    SharedPreferences.Editor editor;

    public MySharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreference.edit();
    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    public static boolean getIsLogin() {
        return mSharedPreference.contains(isLogin) ? mSharedPreference.getBoolean(isLogin, false) : false;
    }
    public static void setIsLogin(boolean isLoginn) {
        mSharedPreference.edit().putBoolean(isLogin, isLoginn);
        mSharedPreference.edit().commit();
    }


    //Method to store user Mobile number
    public boolean setKey(String keyname, String mobile) {
        mSharedPreference.edit().putString(keyname, mobile).apply();
        return false;
    }

    //Method to get User mobile number
    public String getKey(String keyname) {
        return mSharedPreference.getString(keyname, KEY_DEFAULT);
    }

    public Boolean chk(String key) {
        return mSharedPreference.contains(key);
    }

    public static void ClearAllData(){
        mSharedPreference.edit().clear().apply();
    }

}
