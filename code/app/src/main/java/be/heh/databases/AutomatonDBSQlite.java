package be.heh.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alexandre on 1/12/17.
 */

public class AutomatonDBSQlite extends SQLiteOpenHelper {

    private static final String TABLE_AUTOMATON = "table_automaton";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_IP = "IP";
    private static final String COL_RACK = "RACK";
    private static final String COL_SLOT = "SLOT";

    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS " +
            TABLE_AUTOMATON + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_IP + " TEXT NOT NULL, " +
            COL_RACK + " TEXT NOT NULL, " +
            COL_SLOT + " TEXT NOT NULL);";

    private static final String ADD_PILLS = "INSERT INTO "+ TABLE_AUTOMATON +
            " (" + COL_NAME + ", " + COL_IP + ", " +
            COL_RACK + ", " + COL_SLOT + ") VALUES (" +
            "'Conditionnement de comprimés', '10.1.0.130', '0', '1');";

    private static final String ADD_LIQUID = "INSERT INTO "+ TABLE_AUTOMATON +
            " (" + COL_NAME + ", " + COL_IP + ", " +
            COL_RACK + ", " + COL_SLOT + ") VALUES (" +
            "'Asservissement de niveau de liquide', '10.1.0.130', '0', '1');";

    public AutomatonDBSQlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        db.execSQL(ADD_PILLS);
        db.execSQL(ADD_LIQUID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Dans cette méthode, vous devez gérer les révisions de version de votre base de données
        db.execSQL("DROP TABLE " + TABLE_AUTOMATON);
        onCreate(db);
    }

}
