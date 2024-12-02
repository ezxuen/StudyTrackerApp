package com.ezxuen.studytracker;

/**
 * Task represents a study task with details like name, topic, status, duration, and due date.
 */
public class Task {
    private int id;             // Unique identifier for the task
    private String name;        // Name of the task
    private String topic;       // Topic related to the task
    private String status;      // Current status of the task (e.g., "pending", "completed")
    private int duration;       // Duration of the task in minutes
    private String date;        // Due date of the task in "yyyy-MM-dd" format

    /**
     * Constructs a new Task object with the given details.
     * @param id The unique identifier of the task.
     * @param name The name of the task.
     * @param topic The topic associated with the task.
     * @param status The current status of the task.
     * @param duration The duration of the task in minutes.
     * @param date The due date of the task.
     */
    public Task(int id, String name, String topic, String status, int duration, String date) {
        this.id = id;
        this.name = name;
        this.topic = topic;
        this.status = status;
        this.duration = duration;
        this.date = date;
    }

    /**
     * Gets the name of the task.
     * @return The name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the topic associated with the task.
     * @return The topic of the task.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Gets the current status of the task.
     * @return The status of the task.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the due date of the task.
     * @return The due date in "yyyy-MM-dd" format.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the duration of the task.
     * @return The duration in minutes.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the unique identifier of the task.
     * @return The ID of the task.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets a new status for the task.
     * @param status The new status (e.g., "completed", "pending").
     */
    public void setStatus(String status) {
        this.status = status;
    }
}