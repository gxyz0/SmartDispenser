package com.example.smartdispenser.database;

public class Reminder {
    private int id;
    private int reminderId;
    private int userId;
    private String reminderName;
    private int medicationId;
    private int medicationTakingNum;
    private String reminderStartDate;
    private String reminderEndDate;
    private String reminderTime;
    private boolean reminderActive;

    public Reminder(int id, int reminderId, int userId, String reminderName, int medicationId, int medicationTakingNum, String reminderStartDate, String reminderEndDate, String reminderTime, boolean reminderActive) {
        this.id = id;
        this.reminderId = reminderId;
        this.userId = userId;
        this.reminderName = reminderName;
        this.medicationId = medicationId;
        this.medicationTakingNum = medicationTakingNum;
        this.reminderStartDate = reminderStartDate;
        this.reminderEndDate = reminderEndDate;
        this.reminderTime = reminderTime;
        this.reminderActive = reminderActive;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public void setReminderActive(boolean reminderActive) {
        this.reminderActive = reminderActive;
    }

    public void setReminder(String reminderName, int medicationId, int medicationTakingNum, String reminderStartDate, String reminderEndDate, String reminderTime) {
        this.reminderName = reminderName;
        this.medicationId = medicationId;
        this.medicationTakingNum = medicationTakingNum;
        this.reminderStartDate = reminderStartDate;
        this.reminderEndDate = reminderEndDate;
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public int getReminderId() {
        return reminderId;
    }

    public int getUserId() {
        return userId;
    }

    public String getReminderName() {
        return reminderName;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public int getMedicationTakingNum() {
        return medicationTakingNum;
    }

    public String getReminderStartDate() {
        return reminderStartDate;
    }

    public String getReminderEndDate() {
        return reminderEndDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public boolean getReminderActive() {
        return reminderActive;
    }
}
