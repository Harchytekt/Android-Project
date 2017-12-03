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

    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS " +
            TABLE_USER + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_LASTNAME + " TEXT NOT NULL, " +
            COL_FIRSTNAME + " TEXT NOT NULL, " +
            COL_PASSWORD + " TEXT NOT NULL, " +
            COL_EMAIL + " TEXT NOT NULL);";

    private static final String ADD_SUPER = "INSERT INTO "+ TABLE_USER +
            " (" + COL_LASTNAME + ", " +
            COL_FIRSTNAME + ", " + COL_PASSWORD + ", " +
            COL_EMAIL + ") VALUES (" +
            "'Super', 'Utilisateur', 'android3', 'android');";

    public UserBDDSQlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        db.execSQL(ADD_SUPER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Dans cette méthode, vous devez gérer les révisions de version de votre base de données
        db.execSQL("DROP TABLE " + TABLE_USER);
        onCreate(db);
    }

}
