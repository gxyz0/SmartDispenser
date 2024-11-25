package com.example.smartdispenser.room.reminder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao {
    //增
    @Insert
    void insertReminders(Reminder... reminders);

    //删
    @Delete
    void deleteReminder(Reminder... reminders);

    //改
    @Update
    void updateReminders(Reminder... reminders);

    //根据userId删除所有
    @Query("DELETE FROM Reminder WHERE user_id = :userId")
    void deleteRemindersByUserId(int userId);

    //根据userId查询所有
    @Query("SELECT * FROM Reminder WHERE user_id = :userId ORDER BY reminder_id ASC")
    List<Reminder> getRemindersByUserId(int userId);
}
