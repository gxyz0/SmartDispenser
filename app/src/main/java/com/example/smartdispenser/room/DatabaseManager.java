package com.example.smartdispenser.room;

import android.content.Context;

import com.example.smartdispenser.room.medication.Medication;
import com.example.smartdispenser.room.medication.MedicationDao;
import com.example.smartdispenser.room.reminder.Reminder;
import com.example.smartdispenser.room.reminder.ReminderDao;
import com.example.smartdispenser.room.user.User;
import com.example.smartdispenser.room.user.UserDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseManager {
    private UserDao userDao;
    private MedicationDao medicationDao;
    private ReminderDao reminderDao;
    private ExecutorService executorService;

    public DatabaseManager(Context context) {
        // 获取表
        AllDatabase allDatabase = AllDatabase.getInstance(context);
        userDao = allDatabase.getUserDao();
        medicationDao = allDatabase.getMedicationDao();
        reminderDao = allDatabase.getReminderDao();
        // 创建一个单线程的ExecutorService
        executorService = Executors.newSingleThreadExecutor();
    }

    public <T> Future<T> executeDatabaseOperation(Callable<T> operation) {
        return executorService.submit(operation);
    }


    // 增inset 新增元素到User列表
    public void insertUsers(User... users) {
        executeDatabaseOperation(() -> {
            userDao.insertUsers(users);
            return null;
        });
    }

    // 增inset 新增元素到Medication列表
    public void insertMedications(Medication... medications) {
        executeDatabaseOperation(() -> {
            medicationDao.insertMedications(medications);
            return null;
        });
    }

    //增inset 新增元素到Reminder列表
    public void insertReminders(Reminder... reminders) {
        executeDatabaseOperation(() -> {
            reminderDao.insertReminders(reminders);
            return null;
        });
    }

    // 删delete 删除User列表中的元素
    public void deleteUsers(User... users) {
        executeDatabaseOperation(() -> {
            userDao.deleteUser(users);
            return null;
        });
    }

    // 删delete 删除Reminder列表中的元素
    public void deleteReminders(Reminder... reminders) {
        executeDatabaseOperation(() -> {
            reminderDao.deleteReminder(reminders);
            return null;
        });
    }

    // 改update 更新User列表
    public void updateUsers(User... users) {
        executeDatabaseOperation(() -> {
            userDao.updateUsers(users);
            return null;
        });
    }

    // 改update 更新Medication列表
    public void updateMedications(Medication... medications) {
        executeDatabaseOperation(() -> {
            medicationDao.updateMedications(medications);
            return null;
        });
    }

    // 改update 更新Reminder列表
    public void updateReminders(Reminder... reminders) {
        executeDatabaseOperation(() -> {
            reminderDao.updateReminders(reminders);
            return null;
        });
    }

    // 查get 根据userId查询Medication列表
    public Future<List<Medication>> getMedicationsByUserId(int userId) {
        return executeDatabaseOperation(() -> medicationDao.getMedicationsByUserId(userId));
    }

    // 查get 根据userId查询Reminder列表
    public Future<List<Reminder>> getRemindersByUserId(int userId) {
        return executeDatabaseOperation(() -> reminderDao.getRemindersByUserId(userId));
    }

    // 关闭ExecutorService
    public void shutdown() {
        executorService.shutdown();
    }
}
