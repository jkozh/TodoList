/*
* Copyright 2017 Julia Kozhukhovskaya
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.android.julia.todolist.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TaskDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "tasksDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    private static TaskDbHelper sInstance;

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static synchronized TaskDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TaskDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /*
     * These is where we need to write create table statements.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating tasks table
        final String CREATE_TABLE = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + "(" +
                TaskContract.TaskEntry._ID                + " INTEGER PRIMARY KEY, " +
                TaskContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COLUMN_PRIORITY    + " INTEGER NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // SQL for upgrading the tables
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
            onCreate(db);
        }
    }

}