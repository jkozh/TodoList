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

package com.android.julia.todolist.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.android.julia.todolist.R;
import com.android.julia.todolist.adapter.TodoCursorAdapter;
import com.android.julia.todolist.data.TaskContract;
import com.android.julia.todolist.data.TaskDbHelper;
import com.facebook.stetho.Stetho;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, EditTaskDialogListener {

    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    SQLiteDatabase mDatabase;

    @BindView(R.id.recyclerViewTasks) RecyclerView mRecyclerView;
    @BindView(R.id.fab) FloatingActionButton mFabButton;

    // Member variables for the adapter and RecyclerView
    private TodoCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Stetho.initializeWithDefaults(this);

        // Get DBHelper to read from database
        final TaskDbHelper helper = TaskDbHelper.getInstance(this);
        mDatabase = helper.getReadableDatabase();

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TodoCursorAdapter(this, new TaskClickListener() {
            @Override
            public void onTaskClick(View v, int position, String description, int priority) {

                showEditTaskDialog(position, description, priority);

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Construct the URI for the item to delete.
                // Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        // A gray divider line at the bottom of each task
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    /**
     * This method is called after user clicks on any task to edit it.
     *
     * @param position    The position of the clicked task
     * @param description The description of the clicked task
     * @param priority    The priority of the clicked task
     */
    private void showEditTaskDialog(int position, String description, int priority) {
        FragmentManager fm = getSupportFragmentManager();
        // Pass along the position, text, and priority of clicked item
        // to the EditTaskDialogFragment
        EditTaskDialogFragment editTaskDialogFragment =
                EditTaskDialogFragment.newInstance(position, description, priority);
        editTaskDialogFragment.show(fm, "fragment_edit_task");
    }


    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority

                try {
                    return getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.COLUMN_PRIORITY);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    /*
     * Attach an OnClickListener to FAB,
     * so that when it's clicked, a new intent will be created
     * to launch the AddTaskActivity.
     */
    @OnClick(R.id.fab)
    public void addTask(View view) {
        // Create a new intent to start an AddTaskActivity
        Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
        startActivity(addTaskIntent);
    }


    /**
     * This method is called after the user presses 'Save' button in EditTaskDialogFragment.
     * Edited data of the task puts into ContentResolver, then database updates, and then
     * restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    public void onFinishEditTaskDialog(int position, String description, int priority) {
        // Update new task data via a ContentResolver.
        // Create new empty ContentValues object.
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected priority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, priority);
        // Create uri path with id of edited task
        Uri uri = Uri.withAppendedPath(
                TaskContract.TaskEntry.CONTENT_URI, Integer.toString(position));
        // Update the content values via a ContentResolver
        getContentResolver().update(uri, contentValues, null, null);
        // Restart the loader to re-query for all tasks after an editing
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
    }
}
