package be.heh.models;

/**
 * Created by alexandre on 7/12/17.
 */

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import be.heh.main.ListAutomatonsActivity;

public class CurrentAutomaton {

    // Shared Preferences reference
    public SharedPreferences pref;

    // Editor reference for Shared preferences
    public Editor editor;

    // Context
    public Context _context;

    // Shared pref mode
    public int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "CurrentAutomaton";

    // All Shared Preferences Keys
    private static final String IS_THERE_A_CURRENT = "IsThereACurrent";
    public static final String KEY_NAME = "name";

    public CurrentAutomaton(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createCurrentAutomaton(String name) {

        editor.putBoolean(IS_THERE_A_CURRENT, true);

        editor.putString(KEY_NAME, name);

        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect to the automatons' list
     * Else do anything
     * */
    public boolean checkCurrent() {

        if (!this.isThereACurrent()) {

            // user is not logged
            Intent i = new Intent(_context, ListAutomatonsActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);

            return true;
        }
        return false;
    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getAutomatonName() {

        //Use hashmap to store user credentials
        HashMap<String, String> automaton = new HashMap<>();

        automaton.put(KEY_NAME, pref.getString(KEY_NAME, null));

        return automaton;
    }

    /**
     * Clear session details
     * */
    public void clearCurrentAutomaton() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        //Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //_context.startActivity(i);
    }


    /**
     * Check for login
     */
    public boolean isThereACurrent() {
        return pref.getBoolean(IS_THERE_A_CURRENT, false);
    }
}
