package com.ezxuen.studytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper manages the SQLite database for the Study Tracker app.
 * It provides methods to perform CRUD operations on the `tasks` table.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudyTracker.db";
    private static final int DATABASE_VERSION = 3;

    // Table and column names
    public static final String TABLE_TASKS = "tasks";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TOPIC = "topic";
    public static final String STATUS = "status";
    public static final String DURATION = "duration";
    public static final String DATE = "date";

    /**
     * Constructor for DatabaseHelper.
     * @param context The context in which the database is accessed.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL command to create the tasks table
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                TOPIC + " TEXT, " +
                STATUS + " TEXT, " +
                DURATION + " INTEGER, " +
                DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    /**
     * Inserts a new task into the `tasks` table.
     * @param name The name of the task.
     * @param topic The topic associated with the task.
     * @param status The current status of the task.
     * @param duration The duration of the task in minutes.
     * @param date The due date of the task.
     * @return The row ID of the newly inserted task, or -1 if an error occurred.
     */
    public long insertTask(String name, String topic, String status, int duration, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(TOPIC, topic);
        values.put(STATUS, status);
        values.put(DURATION, duration);
        values.put(DATE, date);
        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result;
    }

    /**
     * Fetches tasks with a specific status.
     * @param status The status to filter tasks by (e.g., "pending", "completed").
     * @return A Cursor pointing to the result set.
     */
    public Cursor getTasksByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + STATUS + " = ?", new String[]{status});
    }

    /**
     * Fetches a task by its ID.
     * @param id The ID of the task.
     * @return A Task object containing the task's details, or null if not found.
     */
    public Task getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + ID + " = ?", new String[]{String.valueOf(id)});
        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOPIC)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STATUS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DATE))
            );
            cursor.close();
        }
        return task;
    }

    /**
     * Updates the status of a task.
     * @param id The ID of the task to update.
     * @param status The new status of the task.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateTaskStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS, status);

        int rowsUpdated = db.update(TABLE_TASKS, values, ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsUpdated > 0;
    }

    /**
     * Deletes a task by its ID.
     * @param id The ID of the task to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS, ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    /**
     * Updates a task's details by ID.
     * @param taskId The ID of the task to update.
     * @param name The updated name of the task.
     * @param topic The updated topic of the task.
     * @param status The updated status of the task.
     * @param duration The updated duration of the task in minutes.
     * @param date The updated due date of the task.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateTask(int taskId, String name, String topic, String status, int duration, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(TOPIC, topic);
        values.put(STATUS, status);
        values.put(DURATION, duration);
        values.put(DATE, date);
        int rowsUpdated = db.update(TABLE_TASKS, values, ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return rowsUpdated > 0;
    }

    /**
     * Fetches tasks that are due today or later.
     * @param todayDate The current date in "yyyy-MM-dd" format.
     * @return A Cursor pointing to the result set.
     */
    public Cursor getTasksDueTodayOrLater(String todayDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_TASKS + " WHERE DATE(" + DATE + ") >= DATE(?)",
                new String[]{todayDate}
        );
    }
}