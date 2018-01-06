package be.heh.models;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import be.heh.auth.LoginActivity;

/**
 * This class creates a Session object.
 *
 * @author DUCOBU Alexandre
 */
public class Session {

    // Shared Preferences reference
    public SharedPreferences pref;

    // Editor reference for Shared preferences
    public Editor editor;

    // Context
    public Context _context;

    // Shared pref mode
    public int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "UserSession";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_EMAIl = "email";
    public static final String KEY_RIGHTS = "rights";

    /**
     * Constructor of a Session object.
     *
     * @param context
     *      The context of the application.
     */
    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create a login session
     *
     * @param email
     *      The email address of the user.
     * @param rights
     *      The rights of the user.
     */
    public void createUserLoginSession(String email, String rights) {

        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(KEY_EMAIl, email);
        editor.putString(KEY_RIGHTS, rights);

        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     *
     * @return true if the user is the logged in user, false otherwise.
     */
    public boolean checkLogin() {

        if (!this.isUserLoggedIn()) {

            // user is not logged
            Intent i = new Intent(_context, LoginActivity.class);

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
    public HashMap<String, String> getUserDetails() {

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_EMAIl, pref.getString(KEY_EMAIl, null));
        user.put(KEY_RIGHTS, pref.getString(KEY_RIGHTS, null));

        return user;
    }

    /**
     * Clear the session details
     */
    public void logoutUser() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }


    /**
     * Check for login
     *
     * @return true if there's already a current user, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
