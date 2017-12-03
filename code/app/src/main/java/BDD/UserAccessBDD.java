package BDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by alexandre on 1/12/17.
 */

public class UserAccessBDD {

    private static final int VERSION = 1;
    private static final String NOM_DB = "User.db";

    private static final String TABLE_USER = "table_user"; private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_LASTNAME = "LASTNAME"; private static final int NUM_COL_LASTNAME = 1;
    private static final String COL_FIRSTNAME = "FIRSTNAME"; private static final int NUM_COL_FIRSTNAME = 2;
    private static final String COL_PASSWORD = "PASSWORD"; private static final int NUM_COL_PASSWORD = 3;
    private static final String COL_EMAIL = "EMAIL"; private static final int NUM_COL_EMAIL = 4;

    private SQLiteDatabase db;
    private UserBDDSQlite userdb;

    public UserAccessBDD(Context c) {
        userdb = new UserBDDSQlite(c, NOM_DB, null, VERSION);
    }

    public void openForWrite() {
        db = userdb.getWritableDatabase();
    }

    public void openForRead() {
        db = userdb.getReadableDatabase();
    }

    public void Close() {
        db.close();
    }

    public long insertUser(User u) {
        ContentValues content = new ContentValues();
        content.put(COL_LASTNAME, u.getLastname());
        content.put(COL_FIRSTNAME, u.getFirstname());
        content.put(COL_PASSWORD, u.getPassword());
        content.put(COL_EMAIL, u.getEmail());
        return db.insert(TABLE_USER, null, content);
    }

    public int updateUser(int i, User u) {
        ContentValues content = new ContentValues();
        content.put(COL_LASTNAME, u.getLastname());
        content.put(COL_FIRSTNAME, u.getFirstname());
        content.put(COL_PASSWORD, u.getPassword());
        content.put(COL_EMAIL, u.getEmail());
        return db.update(TABLE_USER, content, COL_ID + " = " + i, null);
    }

    public int removeUser(String email) {
        return db.delete(TABLE_USER, COL_EMAIL + " = " + email, null);
    }

    public ArrayList<User> getAllUser() {
        Cursor c = db.query(TABLE_USER, new String[]{
                COL_ID, COL_LASTNAME, COL_FIRSTNAME, COL_PASSWORD, COL_EMAIL}, null, null, null, null, COL_EMAIL);
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
            tabUser.add(user1);
        }

        c.close();
        return tabUser;

    }

}
