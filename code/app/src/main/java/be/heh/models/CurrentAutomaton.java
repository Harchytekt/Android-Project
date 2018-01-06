package be.heh.models;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import be.heh.main.ListAutomatonsActivity;

/**
 * This class creates a CurrentAutomaton object.
 *
 * @author DUCOBU Alexandre
 */
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

    /**
     * Constructor of a CurrentAutomaton object.
     *
     * @param context
     *      The context of the application.
     */
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
     * Check if there is already an automaton in the Shared Preferences.
     *
     * @return true if the automaton is the current automaton, false otherwise.
     */
    public boolean checkCurrent() {

        if (!this.isThereACurrent()) {

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
     * Get the stored session data
     *
     * @return the stored session data
     */
    public HashMap<String, String> getAutomatonName() {

        HashMap<String, String> automaton = new HashMap<>();

        automaton.put(KEY_NAME, pref.getString(KEY_NAME, null));

        return automaton;
    }

    /**
     * Clear session details
     */
    public void clearCurrentAutomaton() {

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Check for login
     *
     * @return true if there's already a current automaton, false otherwise.
     */
    public boolean isThereACurrent() {
        return pref.getBoolean(IS_THERE_A_CURRENT, false);
    }
}
