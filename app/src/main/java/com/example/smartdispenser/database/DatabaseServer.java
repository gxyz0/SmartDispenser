//package com.example.smartdispenser.database;
//
//import android.content.Context;
//
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//public class DatabaseServer {
//    private static DatabaseManager databaseManager;
//    private static ExecutorService executorService;
//
//    public static void initialize(Context context) {
//        // 创建一个单线程的ExecutorService
//        executorService = Executors.newSingleThreadExecutor();
//        // 创建databaseManager
//        databaseManager = DatabaseManager.getInstance(context);
//    }
//
//    // 关闭ExecutorService
//    public static void shutdown() {
//        executorService.shutdown();
//    }
//
//    public interface Callback<T> {
//        void onResult(T result);
//    }
//
//    private static <T> Future<T> executeDatabaseOperation(Callable<T> operation, Callback<T> callback) {
//        Callable<T> wrappedOperation = () -> {
//            T result = operation.call();
//            callback.onResult(result);
//            return result;
//        };
//        return executorService.submit(wrappedOperation);
//    }
//
//    public static void startConnection() {
//        executeDatabaseOperation(() -> {
//            databaseManager.startConnection();
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static void insertUser(User user) {
//        executeDatabaseOperation(() -> {
//            databaseManager.insertUser(user);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static void updateUser(User user) {
//        executeDatabaseOperation(() -> {
//            databaseManager.updateUser(user);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static Future<User> getUser(int userId, Callback<User> callback) {
//        return executeDatabaseOperation(() -> databaseManager.getUser(userId), callback);
//    }
//
//    public static Future<Integer> getUserIdByName(String userName, Callback<Integer> callback) {
//        return executeDatabaseOperation(() -> databaseManager.getUserIdByName(userName), callback);
//    }
//
//    public static Future<Integer> getUserIdByNamePassword(String userName, String userPassword, Callback<Integer> callback) {
//        return executeDatabaseOperation(() -> databaseManager.getUserIdByNamePassword(userName, userPassword), callback);
//    }
//
//    public static void insertUserInfo(UserInfo userinfo) {
//        executeDatabaseOperation(() -> {
//            databaseManager.insertUserInfo(userinfo);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static void updateUserInfo(UserInfo userinfo) {
//        executeDatabaseOperation(() -> {
//            databaseManager.updateUserInfo(userinfo);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static Future<UserInfo> getUserInfo(int userId, Callback<UserInfo> callback) {
//        return executeDatabaseOperation(() -> databaseManager.getUserInfo(userId), callback);
//    }
//
//
//    public static void insertMedication(Medication medication) {
//        executeDatabaseOperation(() -> {
//            databaseManager.insertMedication(medication);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static void updateMedication(Medication medication) {
//        executeDatabaseOperation(() -> {
//            databaseManager.updateMedication(medication);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static Future<List<Medication>> getMedication(int userId, Callback<List<Medication>> callback) {
//        return executeDatabaseOperation(() -> databaseManager.getMedication(userId), callback);
//    }
//
//
//    public static void insertReminder(Reminder reminder) {
//        executeDatabaseOperation(() -> {
//            databaseManager.insertReminder(reminder);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static void deleteReminder(Reminder reminder) {
//        executeDatabaseOperation(() -> {
//            databaseManager.deleteReminder(reminder);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static void updateReminder(Reminder reminder) {
//        executeDatabaseOperation(() -> {
//            databaseManager.updateReminder(reminder);
//            return null;
//        }, new Callback<Void>() {
//            @Override
//            public void onResult(Void result) {
//            }
//        });
//    }
//
//    public static Future<List<Reminder>> getReminder(int userId, Callback<List<Reminder>> callback) {
//        return executeDatabaseOperation(() -> databaseManager.getReminder(userId), callback);
//    }
//
//}
