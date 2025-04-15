package com.example.smartdispenser.database;

public class ReminderDateTime {
    private int id;
    private int userId;
    private int medicationId;
    private int medicationTakingNum;
    private String reminderDateTime;
    private boolean isTaking;

    public ReminderDateTime(int id, int userId, int medicationId, int medicationTakingNum, String reminderDateTime, boolean isTaking) {
        this.id = id;
        this.userId = userId;
        this.medicationId = medicationId;
        this.medicationTakingNum = medicationTakingNum;
        this.reminderDateTime = reminderDateTime;
        this.isTaking = isTaking;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public int getMedicationTakingNum() {
        return medicationTakingNum;
    }

    public String getReminderDateTime() {
        return reminderDateTime;
    }

    public boolean isTaking() {
        return isTaking;
    }

    public void setIsTaking(boolean isTaking) {
        this.isTaking = isTaking;
    }
}
