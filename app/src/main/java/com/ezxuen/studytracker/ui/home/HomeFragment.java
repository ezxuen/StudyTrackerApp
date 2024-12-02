package com.ezxuen.studytracker.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezxuen.studytracker.DatabaseHelper;
import com.ezxuen.studytracker.R;
import com.ezxuen.studytracker.Task;
import com.ezxuen.studytracker.TaskAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * HomeFragment displays a list of tasks that are due today or later.
 * Users can navigate to the Add Task screen via a FloatingActionButton.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView and set its layout manager
        recyclerView = root.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the database helper and task list
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        taskList = new ArrayList<>();

        // Get the current date in "yyyy-MM-dd" format
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Fetch tasks due today or later from the database
        Cursor cursor = dbHelper.getTasksDueTodayOrLater(todayDate);
        if (cursor.moveToFirst()) {
            do {
                // Create a Task object from the cursor data
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TOPIC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.DURATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DATE))
                );

                // Add the task to the list
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        // Close the cursor to release resources
        cursor.close();

        // Initialize the adapter and set it to the RecyclerView
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerView.setAdapter(taskAdapter);

        // Handle FloatingActionButton click to navigate to Add Task screen
        FloatingActionButton fabAddTask = root.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_add_task);
        });

        return root;
    }
}