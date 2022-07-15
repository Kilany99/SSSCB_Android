package com.example.ssscb_android;

import android.content.Context;
import android.content.SharedPreferences;

public class SeassionManger {
    SharedPreferences userSeassions;
    SharedPreferences.Editor editor;
    Context context;
    private static final String IS_LOGIN ="IsLoggedIn";
    public SeassionManger(Context _context){
        context=_context;
        userSeassions = context.getSharedPreferences("UserLoginSession",Context.MODE_PRIVATE);
        editor = userSeassions.edit();
    }
}
