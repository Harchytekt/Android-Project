package BDD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alexandre on 1/12/17.
 */

public class UserBDDSQlite extends SQLiteOpenHelper {

    private static final String TABLE_USER = "table_user";
    private static final String COL_ID = "ID";
    private static final String COL_LASTNAME = "LASTNAME";
    private static final String COL_FIRSTNAME = "FIRSTNAME";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_RIGHTS = "RIGHTS";

    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS " +
            TABLE_USER + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_LASTNAME + " TEXT NOT NULL, " +
            COL_FIRSTNAME + " TEXT NOT NULL, " +
            COL_PASSWORD + " TEXT NOT NULL, " +
            COL_EMAIL + " TEXT NOT NULL, " +
            COL_RIGHTS + " TEXT NOT NULL);";

    private static final String ADD_SUPER = "INSERT INTO "+ TABLE_USER +
            " (" + COL_LASTNAME + ", " +
            COL_FIRSTNAME + ", " + COL_PASSWORD + ", " +
            COL_EMAIL + ", " + COL_RIGHTS + ") VALUES (" +
            "'Super', 'Utilisateur'," +
            "'1bf95edc5009fe6f2174bc6bf2938c562a8aaedfdcc91ee16756990013c7692e4692aef843090d874a2d601fa9267983b2a86bd27eb6e27d4c2c5110bef4611a'," +
            "'android', '0');";

    private static final String ADD_USER = "INSERT INTO "+ TABLE_USER +
            " (" + COL_LASTNAME + ", " +
            COL_FIRSTNAME + ", " + COL_PASSWORD + ", " +
            COL_EMAIL + ", " + COL_RIGHTS + ") VALUES (" +
            "'Ducobu', 'Alexandre'," +
            "'a66d85048925a32ab6e288a78e7a20c98b937d7e1fdbdc5f8b38e6b89792d896a7fbfa13573326fe3b12a39a2478b0a43f40ebf49dec90d35236bcf3c96b09f2'," +
            "'alexandre@heh.be', '1');";

    public UserBDDSQlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        db.execSQL(ADD_SUPER);
        db.execSQL(ADD_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Dans cette méthode, vous devez gérer les révisions de version de votre base de données
        db.execSQL("DROP TABLE " + TABLE_USER);
        onCreate(db);
    }

}
