package com.android.julia.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoItemDatabase extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "itemsDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ITEMS = "items";

    // Table Columns
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_NAME = "name";

    private static TodoItemDatabase sInstance;


    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the tables
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY," +
                KEY_ITEM_NAME + " TEXT " +
                ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // This method is called when database is upgraded like
    // modifying the table structure,
    // adding constraints to database, etc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // SQL for upgrading the tables
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }
    }

    public static synchronized TodoItemDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

}