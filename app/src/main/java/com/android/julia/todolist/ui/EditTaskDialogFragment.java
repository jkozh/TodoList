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


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.julia.todolist.R;


public class EditTaskDialogFragment extends DialogFragment implements View.OnClickListener {

    RadioButton mHighRadioButton;
    RadioButton mMediumRadioButton;
    RadioButton mLowRadioButton;
    EditText mTaskDescriptionEditText;
    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;
    private int mPosition;

    public EditTaskDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditTaskDialogFragment newInstance(int pos, String description, int priority) {
        EditTaskDialogFragment frag = new EditTaskDialogFragment();
        Bundle args = new Bundle();
        args.putInt("position", pos);
        args.putString("description", description);
        args.putInt("priority", priority);
        frag.setArguments(args);
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.title_edit_task_dialog));
        alertDialogBuilder.setPositiveButton(getString(R.string.action_save_edit_task_dialog),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickSaveTask();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.action_cancel_edit_task_dialog),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Fetch arguments from bundle and set position, description, and priority
        mPosition = getArguments().getInt("position", 0);
        String description = getArguments().getString("description");
        mPriority = getArguments().getInt("priority", 1);

        View v = View.inflate(getContext(), R.layout.fragment_edit_task, null);

        // Get field from view
        mTaskDescriptionEditText = (EditText) v.findViewById(R.id.editTextTaskDescription);
        // Set user's cursor in the text field at the end of the current text value
        // and focused by default
        mTaskDescriptionEditText.setText("");
        mTaskDescriptionEditText.append(description);
        // Show soft keyboard automatically and request focus to field
        mTaskDescriptionEditText.requestFocus();

        mHighRadioButton = (RadioButton) v.findViewById(R.id.radButton1);
        mMediumRadioButton = (RadioButton) v.findViewById(R.id.radButton2);
        mLowRadioButton = (RadioButton) v.findViewById(R.id.radButton3);
        mHighRadioButton.setOnClickListener(this);
        mMediumRadioButton.setOnClickListener(this);
        mLowRadioButton.setOnClickListener(this);

        if (mPriority == 1) {
            mHighRadioButton.setChecked(true);
        } else if (mPriority == 2) {
            mMediumRadioButton.setChecked(true);
        } else if (mPriority == 3) {
            mLowRadioButton.setChecked(true);
        }

        // Set to adjust screen height automatically, when soft keyboard appears on screen
        Window window = alertDialogBuilder.create().getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        alertDialogBuilder.setView(v);
        return alertDialogBuilder.create();
    }


    /**
     * onClickSaveTask is called when the "SAVE" button is clicked.
     * It retrieves user input and inserts that edited task data into the underlying database.
     */
    public void onClickSaveTask() {
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = mTaskDescriptionEditText.getText().toString();
        if (input.length() == 0) {
            return;
        }

        EditTaskDialogListener listener = (EditTaskDialogListener) getActivity();
        listener.onFinishEditTaskDialog(mPosition, input, mPriority);
    }


    /**
     * Called when a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (mHighRadioButton.isChecked()) {
            mPriority = 1;
        } else if (mMediumRadioButton.isChecked()) {
            mPriority = 2;
        } else if (mLowRadioButton.isChecked()) {
            mPriority = 3;
        }
    }
}
