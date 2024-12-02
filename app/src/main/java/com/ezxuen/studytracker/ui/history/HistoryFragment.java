package com.ezxuen.studytracker.ui.history;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ezxuen.studytracker.DatabaseHelper;
import com.ezxuen.studytracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * HistoryFragment displays the list of completed study tasks grouped by date.
 * Each task includes details such as task name, topic, and duration.
 */
public class HistoryFragment extends Fragment {

    private TableLayout tableLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        // Find the TableLayout where the history will be displayed
        tableLayout = root.findViewById(R.id.tableLayoutHistory);

        // Initialize the database helper and fetch completed tasks
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Cursor cursor = dbHelper.getTasksByStatus("completed");

        // Check if there are no completed tasks
        if (cursor.getCount() == 0) {
            // Display a message indicating no completed tasks
            TextView emptyView = new TextView(getContext());
            emptyView.setText("No completed tasks yet.");
            emptyView.setTextSize(16);
            emptyView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            emptyView.setPadding(16, 16, 16, 16);
            tableLayout.addView(emptyView);
        } else {
            // Add a sub-header row for table columns
            tableLayout.addView(createSubHeaderRow());

            String currentDate = ""; // To track and group tasks by date
            while (cursor.moveToNext()) {
                // Fetch task details from the database cursor
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DATE));
                String taskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.NAME));
                String topic = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TOPIC));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.DURATION));

                // Add a header row for a new date
                if (!date.equals(currentDate)) {
                    currentDate = date;
                    tableLayout.addView(createHeaderRow("Completed Tasks: " + formatDate(currentDate)));
                }

                // Add a row with task details
                tableLayout.addView(createDataRow(taskName, topic, String.valueOf(duration)));
            }
        }

        // Close the database cursor to release resources
        cursor.close();
        return root;
    }

    /**
     * Creates a table row with a date header.
     * @param title The title for the header row (e.g., "Completed Tasks: Dec 01, 2024").
     * @return A styled TableRow for the header.
     */
    private TableRow createHeaderRow(String title) {
        TableRow headerRow = new TableRow(getContext());
        TextView headerText = new TextView(getContext());
        headerText.setText(title);
        headerText.setTextSize(18);
        headerText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        headerText.setPadding(16, 16, 16, 16);
        headerText.setGravity(Gravity.CENTER);
        headerRow.addView(headerText);
        headerRow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Highlight header row
        return headerRow;
    }

    /**
     * Creates a sub-header row with column labels: Task Name, Topic, and Duration.
     * @return A styled TableRow for the sub-header.
     */
    private TableRow createSubHeaderRow() {
        TableRow subHeaderRow = new TableRow(getContext());

        subHeaderRow.addView(createTextView("Task Name", Typeface.BOLD, 2f));
        subHeaderRow.addView(createTextView("Topic", Typeface.BOLD, 1f));
        subHeaderRow.addView(createTextView("Duration (min)", Typeface.BOLD, 1f));

        subHeaderRow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Highlight sub-header row
        return subHeaderRow;
    }

    /**
     * Creates a table row with task details: name, topic, and duration.
     * @param taskName The name of the task.
     * @param topic The topic of the task.
     * @param duration The duration of the task in minutes.
     * @return A TableRow containing task details.
     */
    private TableRow createDataRow(String taskName, String topic, String duration) {
        TableRow taskRow = new TableRow(getContext());

        taskRow.addView(createTextView(taskName, Typeface.NORMAL, 2f));
        taskRow.addView(createTextView(topic, Typeface.NORMAL, 1f));
        taskRow.addView(createTextView(duration, Typeface.NORMAL, 1f));

        return taskRow;
    }

    /**
     * Creates a TextView for a TableRow with specified styling and weight.
     * @param text The text to display.
     * @param typefaceStyle The typeface style (e.g., normal or bold).
     * @param weight The weight for layout width distribution.
     * @return A styled TextView.
     */
    private TextView createTextView(String text, int typefaceStyle, float weight) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTypeface(Typeface.defaultFromStyle(typefaceStyle));
        textView.setPadding(16, 8, 16, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(
                0, // Width is 0 to allow weight-based distribution
                TableRow.LayoutParams.WRAP_CONTENT,
                weight // Weight determines the width
        ));
        return textView;
    }

    /**
     * Formats a date string from "yyyy-MM-dd" to "MMM dd, yyyy".
     * @param date The date string in "yyyy-MM-dd" format.
     * @return The formatted date string (e.g., "Dec 01, 2024").
     */
    private String formatDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Return the original date string if formatting fails
        }
    }
}