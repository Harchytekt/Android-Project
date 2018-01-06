package be.heh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import be.heh.models.User;

/**
 * This class manages the user's data from de database.
 *
 * @author DUCOBU Alexandre
 */
public class UserAccessDB {

    private static final int VERSION = 1;
    private static final String NOM_DB = "MyProject.db";

    private static final String TABLE_USER = "table_user";
    private static final String COL_ID = "ID";
    private static final String COL_LASTNAME = "LASTNAME";
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_RIGHTS = "RIGHTS";

    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_LASTNAME = 1;
    private static final int NUM_COL_FIRSTNAME = 2;
    private static final int NUM_COL_PASSWORD = 3;
    private static final int NUM_COL_EMAIL = 4;
    private static final int NUM_COL_RIGHTS = 5;

    private SQLiteDatabase db;
    private MyProjectDBSQlite myprojectdb;

    /**
     * Constructor of a user object from the database.
     *
     * @param c
     *      The context of the application.
     */
    public UserAccessDB(Context c) {
        myprojectdb = new MyProjectDBSQlite(c, NOM_DB, null, VERSION);
    }

    /**
     * Open the database to write in the users database.
     */
    public void openForWrite() {
        db = myprojectdb.getWritableDatabase();
    }

    /**
     * Open the database to read in the users database.
     */
    public void openForRead() {
        db = myprojectdb.getReadableDatabase();
    }

    /**
     * Close the users database.
     */
    public void Close() {
        db.close();
    }

    /**
     * Insert a user to the database.
     *
     * @param u
     *      The user to insert.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertUser(User u) {
        ContentValues content = new ContentValues();
        content.put(COL_LASTNAME, u.getLastname());
        content.put(COL_FIRSTNAME, u.getFirstname());
        content.put(COL_PASSWORD, u.getPassword());
        content.put(COL_EMAIL, u.getEmail());
        content.put(COL_RIGHTS, u.getRights());
        return db.insert(TABLE_USER, null, content);
    }

    /**
     * Update a user from the database.
     *
     * @param i
     *      The ID of the user.
     * @param u
     *      The user to insert.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public int updateUser(int i, User u) {
        ContentValues content = new ContentValues();
        content.put(COL_LASTNAME, u.getLastname());
        content.put(COL_FIRSTNAME, u.getFirstname());
        content.put(COL_PASSWORD, u.getPassword());
        content.put(COL_EMAIL, u.getEmail());
        content.put(COL_RIGHTS, u.getRights());
        return db.update(TABLE_USER, content, COL_ID + " = " + i, null);
    }

    /**
     * Update the user's rights from the database.
     *
     * @param id
     *      The ID of the user.
     * @param rights
     *      The new rights of the user.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public int updateUserRights(int id, String rights) {
        ContentValues content = new ContentValues();
        content.put(COL_RIGHTS, rights);
        return db.update(TABLE_USER, content, COL_ID + " = " + id, null);
    }

    /**
     * Update the user's password from the database.
     *
     * @param id
     *      The ID of the user.
     * @param password
     *      The new password of the user.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public int updateUserPassword(int id, String password) {
        ContentValues content = new ContentValues();
        content.put(COL_PASSWORD, password);
        return db.update(TABLE_USER, content, COL_ID + " = " + id, null);
    }

    /**
     * Remove a user  from the database.
     *
     * @param email
     *      The email address of the user.
     *
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     * To remove all rows and get a count pass "1" as the whereClause.
     */
    public int removeUser(String email) {
        return db.delete(TABLE_USER, COL_EMAIL + " = ?", new String[] {email});
    }

    /**
     * Return a list of all the users of the database.
     *
     * @return a list of all the users of the database.
     */
    public ArrayList<User> getAllUsers() {
        Cursor c = db.query(TABLE_USER, new String[]{
                COL_ID, COL_LASTNAME, COL_FIRSTNAME, COL_PASSWORD, COL_EMAIL, COL_RIGHTS}, null, null, null, null, COL_ID);
        ArrayList<User> tabUser = new ArrayList<User>();

        if (c.getCount() == 0) {
            c.close();
            return tabUser;
        }

        while (c.moveToNext()) {
            User user1 = new User();
            user1.setId(c.getInt(NUM_COL_ID));
            user1.setLastname(c.getString(NUM_COL_LASTNAME));
            user1.setFirstname(c.getString(NUM_COL_FIRSTNAME));
            user1.setPassword(c.getString(NUM_COL_PASSWORD));
            user1.setEmail(c.getString(NUM_COL_EMAIL));
            user1.setRights(c.getString(NUM_COL_RIGHTS));
            tabUser.add(user1);
        }

        c.close();
        return tabUser;

    }

    /**
     * Verify if the email is already used by an existing user.
     *
     * @param email
     *      The email address to verify.
     *
     * @return true if it's already used, false otherwise.
     */
    public boolean isAlreadyUsed(String email) {
        ArrayList<User> tabUser = this.getAllUsers();
        for (User user : tabUser) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }

    /**
     * Get the number of users contained in the database.
     *
     * @return the number of users contained in the database.
     */
    public int getNumberOfUsers() {
        return this.getAllUsers().size();
    }

    /**
     * Get the number of 'read-only' users.
     *
     * @return the number of 'read-only' users.
     */
    public String getRUsers() {
        Cursor c = db.rawQuery("SELECT COUNT(*) AS nbR FROM " + TABLE_USER + " where " + COL_RIGHTS + " = '2'", null);

        c.moveToFirst();
        String res = String.valueOf(c.getInt(0));

        c.close();
        return res;
    }

    /**
     * Get the number of 'read-write' users.
     *
     * @return the number of 'read-write' users.
     */
    public String getRWUsers() {
        Cursor c = db.rawQuery("SELECT COUNT(*) AS nbR FROM " + TABLE_USER + " where " + COL_RIGHTS + " = '1'", null);

        c.moveToFirst();
        String res = String.valueOf(c.getInt(0));

        c.close();
        return res;
    }

}
