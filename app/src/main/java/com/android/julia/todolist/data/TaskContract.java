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


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class TaskContract {

    // The authority, which is how your code knows which Content Provider to access
    static final String AUTHORITY = "com.android.julia.todolist";
    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    static final String PATH_TASKS = "tasks";
    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /* TaskEntry is an inner class that defines the contents of the task table */
    public static final class TaskEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();


        // Task table and column names
        public static final String TABLE_NAME = "tasks";

        // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";


        /*
         (Included) MIME types used in TaskContentProvider's getType() method .
         There are generally two MIME types of data we will be working with:
           1) a directory of items, which can be a row or set of rows
           OR 2) a single item, which is a single row of data
         */

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;

        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows
        Note: Because this implements BaseColumns, the _id column is generated automatically
        tasks
         - - - - - - - - - - - - - - - - - - - - - -
        | _id  |    description     |    priority   |
         - - - - - - - - - - - - - - - - - - - - - -
        |  1   |  Complete lesson   |       1       |
         - - - - - - - - - - - - - - - - - - - - - -
        |  2   |    Go shopping     |       3       |
         - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - -
        | 43   |   Learn guitar     |       2       |
         - - - - - - - - - - - - - - - - - - - - - -

         */

    }
}
