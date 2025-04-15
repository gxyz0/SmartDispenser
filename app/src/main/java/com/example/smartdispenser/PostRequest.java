package com.example.smartdispenser;

import android.content.Context;
import android.util.Log;

import com.example.smartdispenser.database.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostRequest {
    private static OkHttpClient client = new OkHttpClient();

    public boolean isUrlReachable(String URL) {
        if(URL == null) return false;
        try {
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            Response response = client.newCall(request).execute();
            return response.code() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void postUserId(User user, String URL) {
        // 将数据转换为JSON
        Gson gson = new Gson();
        String userIdJson = gson.toJson(user);
        // 创建RequestBody
        RequestBody body = RequestBody.create(userIdJson, MediaType.parse("application/json; charset=utf-8"));
        // 构建请求
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理请求失败
                Log.e("POST UserId", "Failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 处理请求成功
                    String responseString = response.body().string();
                    Log.d("ESP32 Response",responseString);
                } else {
                    // 处理请求失败
                    Log.e("ESP32 Response", "Failed");
                }
            }
        });
    }

//    public List<UploadMedication> getUploadMedicationList(Context context, int userId) {
//        List<Medication> medicationList;
//        List<UploadMedication> uploadMedicationList = new ArrayList<>();
//        // 获取medicationList
//        DatabaseManager databaseManager = new DatabaseManager(context);
//        Future<List<Medication>> future = databaseManager.getMedicationsByUserId(userId);
//        try {
//            medicationList = future.get();
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        databaseManager.shutdown();
//        // 获取uploadMedicationList
//        int medicationId;
//        String medicationName;
//        int medicationQuantity;
//        for (int i = 0; i < medicationList.size(); i++) {
//            medicationId = medicationList.get(i).getMedicationId();
//            medicationName = medicationList.get(i).getMedicationName();
//            medicationQuantity = medicationList.get(i).getMedicationQuantity();
//            uploadMedicationList.add(new UploadMedication(medicationId, medicationName, medicationQuantity));
//        }
//        return uploadMedicationList;
//    }
//
//    public List<UploadReminder> getUploadReminderList(Context context, int userId){
//        List<Reminder> reminderList;
//        List<UploadReminder> uploadReminderList = new ArrayList<>();
//        // 获取reminderList
//        DatabaseManager databaseManager = new DatabaseManager(context);
//        Future<List<Reminder>> reminderFuture = databaseManager.getRemindersByUserId(userId);
//        try {
//            reminderList = reminderFuture.get();
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        databaseManager.shutdown();
//        // 获取uploadReminderList
//        // 获取现在时间
//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        sdfTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
//        sdfDate.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
//        String currentDateStr = sdfDate.format(new Date());
//        String currentTimeStr = sdfTime.format(new Date());
//        Date currentDate = null;
//        try {
//            currentDate = sdfDate.parse(currentDateStr);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        Date currentTime = null;
//        try {
//            currentTime = sdfTime.parse(currentTimeStr);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        // 获取Reminder部分数据并保存至uploadReminderList
//        String medicationName;
//        int medicationTakingNum;
//        String reminderStartDateStr, reminderEndDateStr, reminderTimeStr;
//        Date reminderStartDate, reminderEndDate, reminderTime;
//        for (int i = 0; i < reminderList.size(); i++) {
//            medicationName = reminderList.get(i).getMedicationName();
//            medicationTakingNum = reminderList.get(i).getMedicationTakingNum();
//            reminderStartDateStr = reminderList.get(i).getReminderStartDate();
//            reminderEndDateStr = reminderList.get(i).getReminderEndDate();
//            reminderTimeStr = reminderList.get(i).getReminderTime();
//            try {
//                reminderStartDate = sdfDate.parse(reminderStartDateStr);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                reminderEndDate = sdfDate.parse(reminderEndDateStr);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                reminderTime = sdfTime.parse(reminderTimeStr);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            // 当前日期从开始日期开始
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(reminderStartDate);
//            // 循环直到当前日期大于结束日期
//            while (!calendar.getTime().after(reminderEndDate)) {
//                // 将超过实时日期的日期添加到列表中
//                if (calendar.getTime().equals(currentDate) && reminderTime.after(currentTime)) {
//                    String DateStr = sdfDate.format(calendar.getTime());
//                    uploadReminderList.add(new UploadReminder(medicationName, medicationTakingNum, DateStr, reminderTimeStr));
//                } else if (calendar.getTime().after(currentDate)) {
//                    String DateStr = sdfDate.format(calendar.getTime());
//                    uploadReminderList.add(new UploadReminder(medicationName, medicationTakingNum, DateStr, reminderTimeStr));
//                }
//                // 将当前日期增加一天
//                calendar.add(Calendar.DAY_OF_MONTH, 1);
//            }
//        }
//        return uploadReminderList;
//    }
//
//    public void postMedicationJson(List<UploadMedication> uploadMedicationList, String URL) {
//        // 将数据转换为JSON
//        Gson gson = new Gson();
//        String medicationJsonList = gson.toJson(uploadMedicationList);
//        // 创建RequestBody
//        RequestBody body = RequestBody.create(medicationJsonList, MediaType.parse("application/json; charset=utf-8"));
//        // 构建请求
//        Request request = new Request.Builder()
//                .url(URL)
//                .post(body)
//                .build();
//        // 发送请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                // 处理请求失败
//                Log.e("POST Medication", "Failed");
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    // 处理请求成功
//                    String responseString = response.body().string();
//                    Log.d("ESP32 Response",responseString);
//                } else {
//                    // 处理请求失败
//                    Log.e("ESP32 Response", "Failed");
//                }
//            }
//        });
//    }
//
//    public void postReminderJson(List<UploadReminder> uploadReminderList, String URL) {
//        // 将数据转换为JSON
//        Gson gson = new Gson();
//        String reminderJsonList = gson.toJson(uploadReminderList);
//        // 创建RequestBody
//        RequestBody body = RequestBody.create(reminderJsonList, MediaType.parse("application/json; charset=utf-8"));
//        // 构建请求
//        Request request = new Request.Builder()
//                .url(URL)
//                .post(body)
//                .build();
//        // 发送请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                // 处理请求失败
//                Log.e("POST Reminder", "Failed");
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    // 处理请求成功
//                    String responseString = response.body().string();
//                    Log.d("ESP32 Response",responseString);
//                } else {
//                    // 处理请求失败
//                    Log.e("ESP32 Response","Failed");
//                }
//            }
//        });
//    }
}
