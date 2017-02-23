package com.android.julia.todolist.ui;

import android.view.View;

public interface TaskClickListener {
    void onTaskClick(View v, int position, String description, int priority);
}
