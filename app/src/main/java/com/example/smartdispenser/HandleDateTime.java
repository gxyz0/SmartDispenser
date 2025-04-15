package com.example.smartdispenser;

import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.ReminderDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HandleDateTime {
    // 拆分日期时间
    public static void splitDateTime(Reminder reminder, List<ReminderDateTime> remidnerDateTimeList) {
        // 清空remidnerDateTimeList
        remidnerDateTimeList.clear();
        // 时间格式基本定义
        Date startDateTime, endDateTime;
        String dateTimeStr;
        String startDateStr = reminder.getReminderStartDate();
        String endDateStr = reminder.getReminderEndDate();
        String timeStr = reminder.getReminderTime();
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.getDefault());
        sdfDateTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        try {
            startDateTime = sdfDateTime.parse(startDateStr + "-" + timeStr);
            endDateTime = sdfDateTime.parse(endDateStr + "-" + timeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 日期循环增加
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDateTime);
        while (!calendar.getTime().after(endDateTime)) {
            Date date = calendar.getTime();
            dateTimeStr = sdfDateTime.format(date);
            ReminderDateTime reminderDateTime = new ReminderDateTime(0, reminder.getUserId(), reminder.getMedicationId(), reminder.getMedicationTakingNum(), dateTimeStr, false);
            remidnerDateTimeList.add(reminderDateTime);
            // 将当前日期增加一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // 获取最早日期
    public static String getEarliestDateTime(List<ReminderDateTime> remidnerDateTimeList) {
        Date currentDateTime, reminderDateTime, earlistDateTime = null;
        String currentDateTimeStr, reminderDateTimeStr, earlistDateTimeStr = null;
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.getDefault());
        sdfDateTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        currentDateTimeStr = sdfDateTime.format(new Date());
        try {
            currentDateTime = sdfDateTime.parse(currentDateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 日期循环增加 找到最近日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDateTime);
        for (int i = 0; i < remidnerDateTimeList.size(); i++) {
            reminderDateTimeStr = remidnerDateTimeList.get(i).getReminderDateTime();
            try {
                reminderDateTime = sdfDateTime.parse(reminderDateTimeStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (!currentDateTime.before(reminderDateTime)) continue;
            if (earlistDateTime == null || earlistDateTime.after(reminderDateTime))
            {
                earlistDateTime = reminderDateTime;
            }
        }
        earlistDateTimeStr = sdfDateTime.format(earlistDateTime);
        return earlistDateTimeStr;
    }
}
