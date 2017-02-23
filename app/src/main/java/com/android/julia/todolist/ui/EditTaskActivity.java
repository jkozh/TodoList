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


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.julia.todolist.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditTaskActivity extends AppCompatActivity {

    @BindView(R.id.radButton1) RadioButton mHighRadioButton;
    @BindView(R.id.radButton2) RadioButton mMediumRadioButton;
    @BindView(R.id.radButton3) RadioButton mLowRadioButton;
    @BindView(R.id.editTextTaskDescription) EditText mTaskDescriptionEditText;
    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;
    private int mPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        ButterKnife.bind(this);

        // Get extras
        mPosition = getIntent().getIntExtra("position", 0);
        String description = getIntent().getStringExtra("description");
        mPriority = getIntent().getIntExtra("priority", 1);

        // Set user's cursor in the text field at the end of the current text value
        // and focused by default
        mTaskDescriptionEditText.setText("");
        mTaskDescriptionEditText.append(description);

        if (mPriority == 1) {
            mHighRadioButton.setChecked(true);
        } else if (mPriority == 2) {
            mMediumRadioButton.setChecked(true);
        } else if (mPriority == 3) {
            mLowRadioButton.setChecked(true);
        }
    }


    /**
     * onClickSaveTask is called when the "SAVE" button is clicked.
     * It retrieves user input and inserts that edited task data into the underlying database.
     */
    public void onClickSaveTask(View view) {
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = mTaskDescriptionEditText.getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Prepare data intent
        Intent taskData = new Intent();
        taskData.putExtra("position", mPosition);
        taskData.putExtra("description", input);
        taskData.putExtra("priority", mPriority);

        // Activity finished ok, return the task data
        setResult(RESULT_OK, taskData);

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
