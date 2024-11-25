package com.example.smartdispenser.room.reminder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public int _id;

    @ColumnInfo(name = "reminder_id")
    public int reminderId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "reminder_name")
    public String reminderName;

    @ColumnInfo(name = "medication_name")
    public String medicationName;

    @ColumnInfo(name = "medication_taking_num")
    public int medicationTakingNum;

    @ColumnInfo(name = "reminder_time")
    public String reminderTime;

    @ColumnInfo(name = "reminder_start_date")
    public String reminderStartDate;

    @ColumnInfo(name = "reminder_end_date")
    public String reminderEndDate;

    @ColumnInfo(name = "reminder_active")
    public int reminderActive;

    public Reminder(int reminderId, int userId, String reminderName, String medicationName, int medicationTakingNum, String reminderTime, String reminderStartDate, String reminderEndDate, int reminderActive) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.reminderName = reminderName;
        this.medicationName = medicationName;
        this.medicationTakingNum = medicationTakingNum;
        this.reminderTime = reminderTime;
        this.reminderStartDate = reminderStartDate;
        this.reminderEndDate = reminderEndDate;
        this.reminderActive = reminderActive;
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

    public String getMedicationName() {
        return medicationName;
    }

    public int getMedicationTakingNum() {
        return medicationTakingNum;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public String getReminderStartDate() {
        return reminderStartDate;
    }

    public String getReminderEndDate() {
        return reminderEndDate;
    }

    public int getReminderActive() {
        return reminderActive;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public void setReminder(String reminderName, String medicationName, int medicationTakingNum, String reminderTime, String reminderStartDate, String reminderEndDate) {
        this.reminderName = reminderName;
        this.medicationName = medicationName;
        this.medicationTakingNum = medicationTakingNum;
        this.reminderTime = reminderTime;
        this.reminderStartDate = reminderStartDate;
        this.reminderEndDate = reminderEndDate;
    }
}
