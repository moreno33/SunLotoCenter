package com.sunlotocenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.sunlotocenter.dao.Company;
import com.sunlotocenter.dao.GameResult;
import com.sunlotocenter.dao.GameSchedule;
import com.sunlotocenter.dao.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * For company
     */
    public static final String COM_PREF= "com_pref";


    /**
     * For game schedules
     */
    public static final String GAME_SCHEDULES_PREF= "game_schedules_pref";

    /**
     * For game result
     */
    public static final String GAME_RESULT_PREF= "game_schedules_pref";

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
     * This helper method allows to save an company into
     * the shared preferences
     * @param company
     */
    public void setRegisteredCompany(Company company){

        SharedPreferences.Editor editor= prefs.edit();

        try {
            editor.putString(COM_PREF, ObjectSerializer.serialize(company));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This helper method allows to retrieve the company that
     * has been saved into the shared preferences
     * @return
     */
    public Company getRegisteredCompany(){
        Company company= null;
        try {
            company = (Company) ObjectSerializer.deserialize(prefs.getString(COM_PREF, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return company;
    }

    /**
     * This helper method allows to retrieve the game
     * schedule data
     * @return
     */
    public ArrayList<GameSchedule> getGameSchedules(){
        ArrayList<GameSchedule> gameSchedules= null;
        try {
            gameSchedules= (ArrayList<GameSchedule>) ObjectSerializer.deserialize(prefs.getString(GAME_SCHEDULES_PREF, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameSchedules;
    }

    /**
     * This helper method allows to save the list of game schedules into
     * the shared preferences
     * @param gameSchedules
     */
    public void setGameSchedules(ArrayList<GameSchedule> gameSchedules){

        SharedPreferences.Editor editor= prefs.edit();
        try {
            editor.putString(GAME_SCHEDULES_PREF, ObjectSerializer.serialize(gameSchedules));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This helper method allows to retrieve the game
     * result data
     * @return
     */
    public GameResult getGameResult(){
        GameResult gameResult= null;
        try {
            gameResult= (GameResult) ObjectSerializer.deserialize(prefs.getString(GAME_RESULT_PREF, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameResult;
    }

    /**
     * This helper method allows to save the latest game result into
     * the shared preferences
     * @param gameResult
     */
    public void setGameResult(GameResult gameResult){

        SharedPreferences.Editor editor= prefs.edit();
        try {
            editor.putString(GAME_RESULT_PREF, ObjectSerializer.serialize(gameResult));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
