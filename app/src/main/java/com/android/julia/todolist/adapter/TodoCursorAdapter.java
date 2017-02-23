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

package com.android.julia.todolist.adapter;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.julia.todolist.R;
import com.android.julia.todolist.data.TaskContract;
import com.android.julia.todolist.ui.TaskClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * TodoCursorAdapter creates and binds ViewHolders,
 * that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class TodoCursorAdapter extends RecyclerView.Adapter<TodoCursorAdapter.TaskViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    private TaskClickListener mListener;


    /**
     * Constructor for the TodoCursorAdapter that initializes the Context.
     *
     * @param context the current Context
     */
    public TodoCursorAdapter(Context context, TaskClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int priorityIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        final String description = mCursor.getString(descriptionIndex);
        final int priority = mCursor.getInt(priorityIndex);

        //Set values
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(description);

        // Programmatically set the text and color for the priority TextView
        String priorityString = "";
        switch (priority) {
            case 1:
                priorityString = "HIGH";
                break;
            case 2:
                priorityString = "MEDIUM";
                break;
            case 3:
                priorityString = "LOW";
                break;
        }
        holder.priorityView.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);

        // Add listener for item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTaskClick(v, (int)holder.itemView.getTag(), description, priority);
            }
        });
    }


    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        @BindView(R.id.taskDescription) TextView taskDescriptionView;
        @BindView(R.id.priorityTextView) TextView priorityView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}