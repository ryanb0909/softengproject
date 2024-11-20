package main.code;

import java.io.Serializable;

class Chore implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private int difficulty;
    private boolean completed; // New field to track completion

    public Chore(String description, int difficulty) {
        this.description = description;
        this.difficulty = difficulty;
        this.completed = false;
    }

    public void markComplete() {
        this.completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        return description + " (Difficulty: " + difficulty + ") " + (completed ? "[Completed]" : "[Incomplete]");
    }
}

