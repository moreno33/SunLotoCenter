package com.sunlotocenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.sunlotocenter.dao.User;

import java.io.IOException;

/**
 * This class is like a helper class to deal we data
 * that we need to store in the shared preferences
 * Created by Ing Moreno on 7/28/2018.
 */
public class Prefs {

    /**
     * For account
     */
    public static final String USER_PREF= "user_pref";

    /**
     * Here we declare our shared preferences
     */
    public SharedPreferences prefs;

    /**
     * Here is the context.
     */
    public Context context;

    /**
     * Here we our constructor.
     * @param context
     * @param preferenceType
     */
    public Prefs(Context context, PreferenceType preferenceType){
        this.context= context;
        prefs= context.getSharedPreferences(preferenceType.toString(), Context.MODE_PRIVATE);
    }

    /**
     * This helper method allows to save an account into
     * the shared preferences
     * @param user
     */
    public void setRegisteredUser(User user){

        SharedPreferences.Editor editor= prefs.edit();

        try {
            editor.putString(USER_PREF, ObjectSerializer.serialize(user));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This helper method allows to retrieve the account that
     * has been saved into the shared preferences
     * @return
     */
    public User getRegisteredUser(){
        User user= null;
        try {
             user= (User) ObjectSerializer.deserialize(prefs.getString(USER_PREF, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }


    /**
     * This method is to clear the shared preferences
     */
//    public void clear(){
//        prefs.edit().clear().apply();
//    }

    public  void logout(){
        prefs.edit().clear().apply();
    }
}
