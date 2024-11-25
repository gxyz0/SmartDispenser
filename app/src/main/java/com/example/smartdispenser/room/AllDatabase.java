package com.example.smartdispenser.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smartdispenser.room.medication.Medication;
import com.example.smartdispenser.room.medication.MedicationDao;
import com.example.smartdispenser.room.reminder.Reminder;
import com.example.smartdispenser.room.reminder.ReminderDao;
import com.example.smartdispenser.room.user.User;
import com.example.smartdispenser.room.user.UserDao;

@Database(entities = {User.class, Medication.class, Reminder.class}, version = 1, exportSchema = false)
public abstract class AllDatabase extends RoomDatabase {
    // 暴露Dao
    public abstract UserDao getUserDao();
    public abstract MedicationDao getMedicationDao();
    public abstract ReminderDao getReminderDao();

    // 单例模式 返回数据库
    private static AllDatabase INSTANCE;
    public static synchronized AllDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AllDatabase.class, "SmartDispenser").build();
        }
        return INSTANCE;
    }
}
