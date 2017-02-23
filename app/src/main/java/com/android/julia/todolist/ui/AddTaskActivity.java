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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.julia.todolist.R;
import com.android.julia.todolist.data.TaskContract;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddTaskActivity extends AppCompatActivity {

    @BindView(R.id.radButton1) RadioButton mHighRadioButton;
    @BindView(R.id.radButton2) RadioButton mMediumRadioButton;
    @BindView(R.id.radButton3) RadioButton mLowRadioButton;
    @BindView(R.id.editTextTaskDescription) EditText mTaskDescriptionEditText;
    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);

        // Initialize to highest mPriority by default (mPriority = 1)
        mHighRadioButton.setChecked(true);
        mPriority = 1;
    }


    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask(View view) {
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = mTaskDescriptionEditText.getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);
        // Insert the content values via a ContentResolver
        getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        // Finish activity (this returns back to MainActivity)
        finish();
    }


    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (mHighRadioButton.isChecked()) {
            mPriority = 1;
        } else if (mMediumRadioButton.isChecked()) {
            mPriority = 2;
        } else if (mLowRadioButton.isChecked()) {
            mPriority = 3;
        }
    }
}