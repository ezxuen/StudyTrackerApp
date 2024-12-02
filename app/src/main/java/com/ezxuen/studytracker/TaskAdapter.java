package com.ezxuen.studytracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * TaskAdapter binds a list of Task objects to a RecyclerView.
 * It handles the display, interaction, and status updates for each task.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private List<Task> taskList;

    /**
     * Constructs a TaskAdapter with the given context and task list.
     * @param context The application or activity context.
     * @param taskList The list of Task objects to be displayed.
     */
    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    /**
     * Creates a new ViewHolder when the RecyclerView needs one.
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new TaskViewHolder.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a given position.
     * @param holder The TaskViewHolder to bind data to.
     * @param position The position of the item in the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Populate task details into the ViewHolder
        holder.taskName.setText(task.getName());
        holder.taskTopic.setText(task.getTopic());
        holder.taskDate.setText(task.getDate());
        holder.taskDuration.setText(task.getDuration() + " mins");

        // Update the background color based on task status
        if (task.getStatus().equals("completed")) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        }

        // Reset the checkbox to avoid triggering listeners during binding
        holder.checkBoxComplete.setOnCheckedChangeListener(null);
        holder.checkBoxComplete.setChecked(task.getStatus().equals("completed"));

        // Handle checkbox state changes
        holder.checkBoxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DatabaseHelper dbHelper = new DatabaseHelper(context);

            if (isChecked) {
                // Mark the task as completed in the database and UI
                dbHelper.updateTaskStatus(task.getId(), "completed");
                task.setStatus("completed");
                Toast.makeText(context, "Task marked as completed", Toast.LENGTH_SHORT).show();
            } else {
                // Mark the task as pending in the database and UI
                dbHelper.updateTaskStatus(task.getId(), "pending");
                task.setStatus("pending");
                Toast.makeText(context, "Task marked as pending", Toast.LENGTH_SHORT).show();
            }

            // Notify the adapter to refresh the item
            notifyItemChanged(position);
        });

        // Handle item click to navigate to the task editing screen
        holder.itemView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_activity_main);
            Bundle bundle = new Bundle();
            bundle.putInt("TASK_ID", task.getId());
            navController.navigate(R.id.navigation_add_task, bundle);
        });
    }

    /**
     * Returns the total number of items in the dataset.
     * @return The size of the task list.
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * ViewHolder class for the TaskAdapter.
     * It holds references to the views for each task item.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskTopic, taskDate, taskDuration;
        CheckBox checkBoxComplete;
        androidx.cardview.widget.CardView cardView;

        /**
         * Constructs a new TaskViewHolder and initializes the views.
         * @param itemView The itemView representing a single task.
         */
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskTopic = itemView.findViewById(R.id.taskTopic);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskDuration = itemView.findViewById(R.id.taskDuration);
            checkBoxComplete = itemView.findViewById(R.id.checkBoxComplete);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}