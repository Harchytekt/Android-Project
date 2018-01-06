package be.heh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class creates the database.
 *
 * @author DUCOBU Alexandre
 */
public class MyProjectDBSQlite extends SQLiteOpenHelper {

    // USER
    private static final String TABLE_USER = "table_user";
    private static final String COL_USERID = "ID";
    private static final String COL_LASTNAME = "LASTNAME";
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_RIGHTS = "RIGHTS";

    // AUTOMATON
    private static final String TABLE_AUTOMATON = "table_automaton";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_IP = "IP";
    private static final String COL_RACK = "RACK";
    private static final String COL_SLOT = "SLOT";
    private static final String COL_TYPE = "TYPE";
    private static final String COL_DATABLOC = "DATABLOC";

    private static final String CREATE_USERDB = "CREATE TABLE IF NOT EXISTS " +
            TABLE_USER + " (" +
            COL_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_LASTNAME + " TEXT NOT NULL, " +
            COL_FIRSTNAME + " TEXT NOT NULL, " +
            COL_PASSWORD + " TEXT NOT NULL, " +
            COL_EMAIL + " TEXT NOT NULL, " +
            COL_RIGHTS + " TEXT NOT NULL);";

    private static final String ADD_SUPER = "INSERT INTO "+ TABLE_USER +
            " (" + COL_LASTNAME + ", " +
            COL_FIRSTNAME + ", " + COL_PASSWORD + ", " +
            COL_EMAIL + ", " + COL_RIGHTS + ") VALUES (" +
            "'Ducobu', 'Alexandre'," +
            "'1bf95edc5009fe6f2174bc6bf2938c562a8aaedfdcc91ee16756990013c7692e4692aef843090d874a2d601fa9267983b2a86bd27eb6e27d4c2c5110bef4611a'," +
            "'android', '0');";

    private static final String ADD_USER = "INSERT INTO "+ TABLE_USER +
            " (" + COL_LASTNAME + ", " +
            COL_FIRSTNAME + ", " + COL_PASSWORD + ", " +
            COL_EMAIL + ", " + COL_RIGHTS + ") VALUES (" +
            "'Ducobu', 'Alexandre'," +
            "'a66d85048925a32ab6e288a78e7a20c98b937d7e1fdbdc5f8b38e6b89792d896a7fbfa13573326fe3b12a39a2478b0a43f40ebf49dec90d35236bcf3c96b09f2'," +
            "'alexandre@heh.be', '1');";

    private static final String CREATE_AUTOMATONDB = "CREATE TABLE IF NOT EXISTS " +
            TABLE_AUTOMATON + " (" +
            COL_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_IP   + " TEXT NOT NULL, " +
            COL_RACK + " TEXT NOT NULL, " +
            COL_SLOT + " TEXT NOT NULL, " +
            COL_TYPE + " TEXT NOT NULL, " +
            COL_DATABLOC + " TEXT NOT NULL);";

    private static final String ADD_PILLS_VM = "INSERT INTO "+ TABLE_AUTOMATON +
            " (" + COL_NAME + ", " + COL_IP + ", " +
            COL_RACK + ", " + COL_SLOT + ", " + COL_TYPE + ", " + COL_DATABLOC + ") VALUES (" +
            "'Comprimés VM', '192.168.1.130', '0', '2', '0', '5');";

    private static final String ADD_LIQUID_VM = "INSERT INTO "+ TABLE_AUTOMATON +
            " (" + COL_NAME + ", " + COL_IP + ", " +
            COL_RACK + ", " + COL_SLOT + ", " + COL_TYPE + ", " + COL_DATABLOC + ") VALUES (" +
            "'Liquide VM', '192.168.1.130', '0', '2', '1', '5');";

    private static final String ADD_PILLS_SCHOOL = "INSERT INTO "+ TABLE_AUTOMATON +
            " (" + COL_NAME + ", " + COL_IP + ", " +
            COL_RACK + ", " + COL_SLOT + ", " + COL_TYPE + ", " + COL_DATABLOC + ") VALUES (" +
            "'Comprimés École', '10.1.0.119', '0', '1', '0', '25');";

    private static final String TEST_LIQUID_SCHOOL = "INSERT INTO "+ TABLE_AUTOMATON +
            " (" + COL_NAME + ", " + COL_IP + ", " +
            COL_RACK + ", " + COL_SLOT + ", " + COL_TYPE + ", " + COL_DATABLOC + ") VALUES (" +
            "'Test Liquide', '10.1.0.111', '0', '1', '1', '25');";


    /**
     * Constructor of the MyProjectDBSQlite object.
     *
     * @param context
     *      The context of the application.
     * @param name
     *      The name of the database.
     * @param factory
     *      ?
     * @param version
     *      The version of the database.
     *
     */
    public MyProjectDBSQlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
    }

    /**
     * Create the database and populate it with the tables and data created.
     *
     * @param db
     *      The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERDB);
        db.execSQL(ADD_SUPER);
        db.execSQL(ADD_USER);

        db.execSQL(CREATE_AUTOMATONDB);
        db.execSQL(ADD_PILLS_VM);
        db.execSQL(ADD_LIQUID_VM);
        db.execSQL(ADD_PILLS_SCHOOL);
        db.execSQL(TEST_LIQUID_SCHOOL);
    }

    /**
     * Update de database by dropping the tables and calling the onCreate method.
     *
     * @param db
     *      The database
     * @param oldVersion
     *      The old version of the database
     * @param newVersion
     *      The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTOMATON);
        onCreate(db);
    }

}
