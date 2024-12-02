package com.ezxuen.studytracker.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ezxuen.studytracker.DatabaseHelper;
import com.ezxuen.studytracker.R;
import com.ezxuen.studytracker.Task;

import java.util.Calendar;

/**
 * AddTaskFragment allows users to add, update, or delete study tasks.
 * It handles task details like name, topic, duration, and due date.
 */
public class AddTaskFragment extends Fragment {

    private EditText editTaskName, editTaskTopic, editTaskDuration;
    private TextView txtDueDate;
    private Button btnSetDate, btnAddTask, btnDeleteTask;

    private String selectedDate;
    private int taskId = -1; // Default value indicating a new task
    private boolean isTaskCompleted = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);

        // Initialize UI elements
        editTaskName = root.findViewById(R.id.editTaskName);
        editTaskTopic = root.findViewById(R.id.editTaskTopic);
        editTaskDuration = root.findViewById(R.id.taskDuration);
        txtDueDate = root.findViewById(R.id.txtDueDate);
        btnSetDate = root.findViewById(R.id.btnSetDate);
        btnAddTask = root.findViewById(R.id.btnAddTask);
        btnDeleteTask = root.findViewById(R.id.btnDeleteTask);

        // Check if fragment has arguments for editing an existing task
        if (getArguments() != null) {
            taskId = getArguments().getInt("TASK_ID", -1);
            if (taskId != -1) {
                // Load task details if editing
                loadTaskDetails(taskId);
                btnDeleteTask.setVisibility(View.VISIBLE);
                btnAddTask.setText("Update Task");
                requireActivity().setTitle("Edit Task");
            } else {
                // Set title for adding a new task
                requireActivity().setTitle("Add Task");
            }
        }

        // Handle "Set Date" button click to show date picker
        btnSetDate.setOnClickListener(v -> showDatePicker(date -> {
            selectedDate = date;
            txtDueDate.setText("Due Date: " + selectedDate);
        }));

        // Handle "Add/Update Task" button click
        btnAddTask.setOnClickListener(v -> {
            // Retrieve user inputs
            String name = editTaskName.getText().toString();
            String topic = editTaskTopic.getText().toString();
            String durationStr = editTaskDuration.getText().toString();
            int duration = 0;

            // Validate duration input
            if (!durationStr.isEmpty()) {
                try {
                    duration = Integer.parseInt(durationStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid duration value", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Check if all required fields are filled
            if (name.isEmpty() || topic.isEmpty() || selectedDate == null) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String status = "pending";
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());

            if (taskId == -1) {
                // Add a new task to the database
                long result = dbHelper.insertTask(name, topic, status, duration, selectedDate);
                if (result != -1) {
                    Toast.makeText(getContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Update an existing task in the database
                boolean updated = dbHelper.updateTask(taskId, name, topic, status, duration, selectedDate);
                if (updated) {
                    Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update task", Toast.LENGTH_SHORT).show();
                }
            }

            // Navigate back to the home screen
            navigateToHome();
        });

        // Handle "Delete Task" button click
        btnDeleteTask.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            boolean deleted = dbHelper.deleteTask(taskId);
            if (deleted) {
                Toast.makeText(getContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
            }
            navigateToHome();
        });

        return root;
    }

    /**
     * Load task details for editing an existing task.
     * @param taskId The ID of the task to edit.
     */
    private void loadTaskDetails(int taskId) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Task task = dbHelper.getTaskById(taskId);

        if (task != null) {
            editTaskName.setText(task.getName());
            editTaskTopic.setText(task.getTopic());
            txtDueDate.setText("Due Date: " + task.getDate());
            editTaskDuration.setText(String.valueOf(task.getDuration()));
            selectedDate = task.getDate();
        }
    }

    /**
     * Display a date picker dialog to select a due date.
     * @param callback The callback to handle the selected date.
     */
    private void showDatePicker(DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    callback.onDateSet(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Navigate back to the home screen.
     */
    private void navigateToHome() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigateUp();
    }

    /**
     * Interface for handling selected date from the date picker.
     */
    interface DatePickerCallback {
        void onDateSet(String date);
    }
}