package com.example.smartdispenser.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.MainActivity;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.adapter.HomeCardTimeAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.ReminderDateTime;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取Context
        context = requireContext();
        databaseManager = DatabaseManager.getInstance(context);

        // 获取userId
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 1);

        // 定义ClockText
        timeText = view.findViewById(R.id.time_text);
        dateText = view.findViewById(R.id.date_text);
        timehandler.post(updateTimeRunnable); // 启动时间更新

        // 定义HomeText
        homeText = view.findViewById(R.id.home_text);

        // 设置RecyclerView
        homeCardTimeLayout = view.findViewById(R.id.home_card_time_layout);
        // 采用线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        homeCardTimeLayout.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置handler 获取数据后更新ui
        showData();
        // 获取Reminder数据库内容
        getReminderList();
        // 获取ReminderDateTime数据库内容
        getReminderDateTimeList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timehandler.removeCallbacks(updateTimeRunnable); // 停止时间更新
    }

    // 定义userId
    int userId = 1;
    // 定义Context
    private Context context;
    // 定义databaseManger
    private DatabaseManager databaseManager;
    // 定义RecyclerView
    private RecyclerView homeCardTimeLayout;
    // 定义ClockText
    private TextView timeText;
    private TextView dateText;
    // 定义HomeText
    private TextView homeText;
    // 定义reminderList reminderDateTimeList
    List<Reminder> reminderList;
    List<ReminderDateTime> reminderDateTimeList;
    // 设置数据handler
    private Handler handler;

    // 设置定时器
    private Handler timehandler = new Handler(Looper.getMainLooper());
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            timehandler.postDelayed(this, 1000); // 每秒更新一次
        }
    };

    // 更新时间
    private void updateTime() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd\nEEEE", Locale.getDefault());
        sdfTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        sdfDate.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        String currentTime = sdfTime.format(new Date());
        String currentDate = sdfDate.format(new Date());
        timeText.setText(currentTime);
        dateText.setText(currentDate);
    }

    // 获取Reminder数据库内容
    private void getReminderList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                reminderList = databaseManager.getReminder(userId);
                System.out.println("HomeFragment: Reminder获取完成！");
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 更新最新的reminder
//                        homeText.setText("Latest Reminder\nwithin 30 days:\n" + getEarliestDateTime());
//                    }
//                });
            }
        }).start();
    }

    // 获取ReminderDateTime数据库内容
    private void getReminderDateTimeList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                reminderDateTimeList = databaseManager.getReminderDateTime(userId);
                System.out.println("HomeFragment: ReminderDateTime获取完成！");
                // 发送消息到Handler
                Message msg = new Message();
                msg.what = 2; // 设置消息类型
                msg.obj = reminderDateTimeList; // 将数据放入消息
                handler.sendMessage(msg);
            }
        }).start();
    }

    // 设置handler 获取数据后更新ui
    private void showData() {
        // 初始化Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) { // 检查消息类型
                    // 更新UI
                    // 更新最新的reminder
                    homeText.setText("Latest Reminder\nwithin 30 days:\n" + getEarliestDateTime());
                }
                else if (msg.what == 2) { // 检查消息类型
                    // 更新UI
                    HomeCardTimeAdapter homeCardTimeAdapter = new HomeCardTimeAdapter(context, reminderDateTimeList);
                    homeCardTimeLayout.setAdapter(homeCardTimeAdapter);
                }
            }
        };
    }


    // 获取最早的DateTime
    private String getEarliestDateTime() {
        // 获取现在时间
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdfTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        sdfDate.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        String currentDateStr = sdfDate.format(new Date());
        String currentTimeStr = sdfTime.format(new Date());
        Date currentDate = null;
        Date currentTime = null;
        try {
            currentDate = sdfDate.parse(currentDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        try {
            currentTime = sdfTime.parse(currentTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String earlistDateStr, earlistTimeStr;
        Date earlistDate = null, earlistTime = null;
        String reminderStartDateStr, reminderEndDateStr, reminderTimeStr;
        Date reminderStartDate, reminderEndDate, reminderTime;
        // 从当前日期开始
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date endDate = calendar.getTime();
        calendar.setTime(currentDate);
        // 循环直到当前日期大于30天后
        while (!calendar.getTime().after(endDate)) {
            Date date = calendar.getTime();
            for (int i = 0; i < reminderList.size(); i++) {
                reminderStartDateStr = reminderList.get(i).getReminderStartDate();
                reminderEndDateStr = reminderList.get(i).getReminderEndDate();
                reminderTimeStr = reminderList.get(i).getReminderTime();
                boolean isReminderActive = reminderList.get(i).getReminderActive();
                try {
                    reminderStartDate = sdfDate.parse(reminderStartDateStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    reminderEndDate = sdfDate.parse(reminderEndDateStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    reminderTime = sdfTime.parse(reminderTimeStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                // 如果开启了reminder
                if (isReminderActive) {
                    // 如果日期在reminder期间
                    if (!date.after(reminderEndDate) && !date.before(reminderStartDate)) {
                        if (earlistDate == null || earlistTime == null) {
                            earlistDate = date;
                            earlistTime = reminderTime;
                        } else if (earlistDate != null && earlistTime.after(reminderTime)) {
                            earlistTime = reminderTime;
                        }
                    }
                }
            }
            // 将当前日期增加一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (earlistDate == null || earlistTime == null) return null;
        earlistDateStr = sdfDate.format(earlistDate);
        earlistTimeStr = sdfTime.format(earlistTime);
        return earlistDateStr + " " + earlistTimeStr;
    }
}