package com.example.smartdispenser.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.smartdispenser.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    TextView timeText;
    TextView dateText;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timeText = view.findViewById(R.id.time_text);
        dateText = view.findViewById(R.id.date_text);
        handler.post(updateTimeRunnable); // 启动时间更新
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimeRunnable); // 停止时间更新
    }

    // 设置定时器
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            handler.postDelayed(this, 1000); // 每秒更新一次
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

}