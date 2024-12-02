package com.ezxuen.studytracker.ui.reminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ezxuen.studytracker.DatabaseHelper;
import com.ezxuen.studytracker.R;
import com.ezxuen.studytracker.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ReminderFragment manages task timers and break reminders.
 * Users can select a task, start/pause timers, and set custom break reminders.
 */
public class ReminderFragment extends Fragment {

    private Spinner spinnerTasks;
    private TextView txtTimer, txtBreakTimer;
    private Button btnStartTimer, btnPauseTimer, btnResumeTimer, btnSetBreakReminder, btnResetBreak;
    private CountDownTimer taskTimer, breakTimer;
    private long remainingTaskTime, remainingBreakTime;
    private Task selectedTask;
    private boolean isPaused = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reminder, container, false);

        // Initialize UI components
        spinnerTasks = root.findViewById(R.id.spinnerTasks);
        txtTimer = root.findViewById(R.id.txtTimer);
        txtBreakTimer = root.findViewById(R.id.txtBreakTimer);
        btnStartTimer = root.findViewById(R.id.btnStartTimer);
        btnPauseTimer = root.findViewById(R.id.btnPauseTimer);
        btnResumeTimer = root.findViewById(R.id.btnResumeTimer);
        btnSetBreakReminder = root.findViewById(R.id.btnSetBreakReminder);
        btnResetBreak = root.findViewById(R.id.btnResetBreak);

        // Load tasks with "pending" status into the Spinner
        loadPendingTasks();

        // Handle "Start Timer" button click
        btnStartTimer.setOnClickListener(v -> {
            if (selectedTask == null) {
                Toast.makeText(getContext(), "Please select a task", Toast.LENGTH_SHORT).show();
                return;
            }
            startTaskTimer(selectedTask.getDuration() * 60 * 1000L); // Convert duration to milliseconds
        });

        // Handle "Pause Timer" button click
        btnPauseTimer.setOnClickListener(v -> pauseTaskTimer());

        // Handle "Resume Timer" button click
        btnResumeTimer.setOnClickListener(v -> resumeTaskTimer());

        // Handle "Set Break Reminder" button click
        btnSetBreakReminder.setOnClickListener(v -> showBreakReminderDialog());

        // Handle "Reset Break Timer" button click
        btnResetBreak.setOnClickListener(v -> resetBreakTimer());

        return root;
    }

    /**
     * Loads tasks with "pending" status into the Spinner for selection.
     */
    private void loadPendingTasks() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<String> spinnerItems = new ArrayList<>();

        // Add default "Please select a task" option
        spinnerItems.add("Please select a task");

        // Fetch tasks with "pending" status
        Cursor cursor = dbHelper.getTasksByStatus("pending");
        if (cursor.moveToFirst()) {
            do {
                String taskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.NAME));
                String taskTopic = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TOPIC));
                spinnerItems.add(taskName + " - " + taskTopic);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Populate the Spinner with task names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTasks.setAdapter(adapter);

        // Handle task selection
        spinnerTasks.setSelection(0);
        spinnerTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedTask = null; // No task selected
                    return;
                }

                // Load selected task details
                int taskPosition = position - 1; // Adjust for default option
                Cursor taskCursor = dbHelper.getTasksByStatus("pending");
                if (taskCursor.moveToPosition(taskPosition)) {
                    selectedTask = new Task(
                            taskCursor.getInt(taskCursor.getColumnIndexOrThrow(DatabaseHelper.ID)),
                            taskCursor.getString(taskCursor.getColumnIndexOrThrow(DatabaseHelper.NAME)),
                            taskCursor.getString(taskCursor.getColumnIndexOrThrow(DatabaseHelper.TOPIC)),
                            taskCursor.getString(taskCursor.getColumnIndexOrThrow(DatabaseHelper.STATUS)),
                            taskCursor.getInt(taskCursor.getColumnIndexOrThrow(DatabaseHelper.DURATION)),
                            taskCursor.getString(taskCursor.getColumnIndexOrThrow(DatabaseHelper.DATE))
                    );
                }
                taskCursor.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTask = null; // No task selected
            }
        });
    }

    /**
     * Starts a task timer for the specified duration.
     * @param duration The duration of the task timer in milliseconds.
     */
    private void startTaskTimer(long duration) {
        taskTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTaskTime = millisUntilFinished;
                txtTimer.setText(formatTime(remainingTaskTime));
            }

            @Override
            public void onFinish() {
                txtTimer.setText("00:00:00");
                Toast.makeText(getContext(), "Task completed!", Toast.LENGTH_SHORT).show();
            }
        }.start();
        btnStartTimer.setVisibility(View.GONE);
        btnPauseTimer.setVisibility(View.VISIBLE);
        btnResumeTimer.setVisibility(View.GONE);
    }

    /**
     * Pauses the task timer.
     */
    private void pauseTaskTimer() {
        if (taskTimer != null) {
            taskTimer.cancel();
            isPaused = true;
            btnPauseTimer.setVisibility(View.GONE);
            btnResumeTimer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Resumes the task timer with the remaining time.
     */
    private void resumeTaskTimer() {
        startTaskTimer(remainingTaskTime);
        btnPauseTimer.setVisibility(View.VISIBLE);
        btnResumeTimer.setVisibility(View.GONE);
    }

    /**
     * Shows a dialog to set a break reminder duration.
     */
    private void showBreakReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_break_reminder, null);
        builder.setView(dialogView);

        EditText editBreakDuration = dialogView.findViewById(R.id.editBreakDuration);

        builder.setPositiveButton("Set", (dialog, which) -> {
            String input = editBreakDuration.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a break duration", Toast.LENGTH_SHORT).show();
                return;
            }
            int breakDuration = Integer.parseInt(input);
            startBreakTimer(breakDuration * 60 * 1000L); // Convert minutes to milliseconds
        }).setNegativeButton("Cancel", null).create().show();
    }

    /**
     * Starts a break timer for the specified duration.
     * @param breakDuration The break duration in milliseconds.
     */
    private void startBreakTimer(long breakDuration) {
        if (taskTimer != null) pauseTaskTimer();

        breakTimer = new CountDownTimer(breakDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingBreakTime = millisUntilFinished;
                txtBreakTimer.setText("Break: " + formatTime(remainingBreakTime));
            }

            @Override
            public void onFinish() {
                txtBreakTimer.setText("Break Over");
                Toast.makeText(getContext(), "Break over! Resume studying.", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    /**
     * Resets the break timer.
     */
    private void resetBreakTimer() {
        if (breakTimer != null) {
            breakTimer.cancel();
            txtBreakTimer.setText("No Break Active");
        }
    }

    /**
     * Formats milliseconds into HH:mm:ss format.
     * @param millis The time in milliseconds.
     * @return A formatted time string.
     */
    @SuppressLint("DefaultLocale")
    private String formatTime(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }
}