package be.heh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import be.heh.models.Automaton;

/**
 * This class manages the automaton's data from de database.
 *
 * @author DUCOBU Alexandre
 */
public class AutomatonAccessDB {

    private static final int VERSION = 1;
    private static final String NOM_DB = "MyProject.db";

    private static final String TABLE_AUTOMATON = "table_automaton";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_IP = "IP";
    private static final String COL_RACK = "RACK";
    private static final String COL_SLOT = "SLOT";
    private static final String COL_TYPE = "TYPE";
    private static final String COL_DATABLOC = "DATABLOC";

    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_NAME = 1;
    private static final int NUM_COL_IP = 2;
    private static final int NUM_COL_RACK = 3;
    private static final int NUM_COL_SLOT = 4;
    private static final int NUM_COL_TYPE = 5;
    private static final int NUM_COL_DATABLOC = 6;

    private SQLiteDatabase db;
    private MyProjectDBSQlite automatondb;

    /**
     * Constructor of an automaton object from the database.
     *
     * @param c
     *      The context of the application.
     */
    public AutomatonAccessDB(Context c) {
        automatondb = new MyProjectDBSQlite(c, NOM_DB, null, VERSION);
    }

    /**
     * Open the database to write in the automatons database.
     */
    public void openForWrite() {
        db = automatondb.getWritableDatabase();
    }

    /**
     * Open the database to read in the automatons database.
     */
    public void openForRead() {
        db = automatondb.getReadableDatabase();
    }

    /**
     * Close the automatons database.
     */
    public void Close() {
        db.close();
    }

    /**
     * Insert an automaton to the database.
     *
     * @param a
     *      The automaton to insert.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertAutomaton(Automaton a) {
        ContentValues content = new ContentValues();
        content.put(COL_NAME, a.getName());
        content.put(COL_IP, a.getIp());
        content.put(COL_RACK, a.getRack());
        content.put(COL_SLOT, a.getSlot());
        content.put(COL_TYPE, a.getType());
        content.put(COL_DATABLOC, a.getDataBloc());
        return db.insert(TABLE_AUTOMATON, null, content);
    }

    /**
     * Update an automaton from the database.
     *
     * @param i
     *      The ID of the automaton.
     * @param a
     *      The automaton to insert.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public int updateAutomaton(int i, Automaton a) {
        ContentValues content = new ContentValues();
        content.put(COL_NAME, a.getName());
        content.put(COL_IP, a.getIp());
        content.put(COL_RACK, a.getRack());
        content.put(COL_SLOT, a.getSlot());
        content.put(COL_TYPE, a.getType());
        content.put(COL_DATABLOC, a.getDataBloc());
        return db.update(TABLE_AUTOMATON, content, COL_ID + " = " + i, null);
    }

    /**
     * Remove an automaton from the database.
     *
     * @param name
     *      The name of the automaton.
     *
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     * To remove all rows and get a count pass "1" as the whereClause.
     */
    public int removeAutomaton(String name) {
        return db.delete(TABLE_AUTOMATON, COL_NAME + " = ?", new String[] {name});
    }

    /**
     * Return a list of all the automatons of the database.
     *
     * @return a list of all the automatons of the database.
     */
    public ArrayList<Automaton> getAllAutomatons() {
        Cursor c = db.query(TABLE_AUTOMATON, new String[]{
                COL_ID, COL_NAME, COL_IP, COL_RACK, COL_SLOT, COL_TYPE, COL_DATABLOC}, null, null, null, null, COL_ID);
        ArrayList<Automaton> tabAutomaton = new ArrayList<Automaton>();

        if (c.getCount() == 0) {
            c.close();
            return tabAutomaton;
        }

        while (c.moveToNext()) {
            Automaton automaton1 = new Automaton();
            automaton1.setId(c.getInt(NUM_COL_ID));
            automaton1.setName(c.getString(NUM_COL_NAME));
            automaton1.setIp(c.getString(NUM_COL_IP));
            automaton1.setRack(c.getString(NUM_COL_RACK));
            automaton1.setSlot(c.getString(NUM_COL_SLOT));
            automaton1.setType(c.getString(NUM_COL_TYPE));
            automaton1.setDataBloc(c.getString(NUM_COL_DATABLOC));
            tabAutomaton.add(automaton1);
        }

        c.close();
        return tabAutomaton;

    }

    /**
     * Get the chosen automaton.
     *
     * @param name
     *      The name of the chosen automaton.
     *
     * @return the chosen automaton if it exists.
     */
    public Automaton getAutomaton(String name) {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_AUTOMATON + " where " + COL_NAME + " = '" + name + "'", null);
        Automaton automaton = new Automaton();

        if (c.getCount() == 0) {
            c.close();
            return automaton;
        }

        while (c.moveToNext()) {
            automaton.setId(c.getInt(NUM_COL_ID));
            automaton.setName(c.getString(NUM_COL_NAME));
            automaton.setIp(c.getString(NUM_COL_IP));
            automaton.setRack(c.getString(NUM_COL_RACK));
            automaton.setSlot(c.getString(NUM_COL_SLOT));
            automaton.setType(c.getString(NUM_COL_TYPE));
            automaton.setDataBloc(c.getString(NUM_COL_DATABLOC));
        }

        c.close();
        return automaton;

    }

    /**
     * Verify if the name is already used by an existing automaton.
     *
     * @param name
     *      The name to verify.
     *
     * @return true if it's already used, false otherwise.
     */
    public boolean isAlreadyUsed(String name) {
        ArrayList<Automaton> tabAutomaton = this.getAllAutomatons();
        for (Automaton automaton : tabAutomaton) {
            if (automaton.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Get the number of automatons contained in the database.
     *
     * @return the number of automatons contained in the database.
     */
    public int getNumberOfAutomatons() {
        return this.getAllAutomatons().size();
    }

    /**
     * Get the number of 'pills' automatons.
     *
     * @return the number of 'pills' automatons.
     */
    public String getPills() {
        Cursor c = db.rawQuery("SELECT COUNT(*) AS nbPills FROM " + TABLE_AUTOMATON + " where " + COL_TYPE + " = '0'", null);

        c.moveToFirst();
        String res = String.valueOf(c.getInt(0));

        c.close();
        return res;
    }

    /**
     * Get the number of 'liquid' automatons.
     *
     * @return the number of 'liquid' automatons.
     */
    public String getLiquids() {
        Cursor c = db.rawQuery("SELECT COUNT(*) AS nbLiquid FROM " + TABLE_AUTOMATON + " where " + COL_TYPE + " = '1'", null);

        c.moveToFirst();
        String res = String.valueOf(c.getInt(0));

        c.close();
        return res;
    }

}
