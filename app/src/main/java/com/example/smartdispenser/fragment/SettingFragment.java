package com.example.smartdispenser.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smartdispenser.R;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Reminder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取context
        context = requireContext();

        // 获取switch
        nightSwitch = view.findViewById(R.id.nightSwitch);
        disturbSwitch = view.findViewById(R.id.disturbSwitch);
        startTimeButton = view.findViewById(R.id.startTimeButton);
        endTimeButton = view.findViewById(R.id.endTimeButton);

        // 设置switch
        setNightSwitch();
        setDisturbSwitch();

        // 设置监听事件
        nightSwitch.setOnCheckedChangeListener(nightSwitchListener);
        disturbSwitch.setOnCheckedChangeListener(disturbSwitchListener);
        startTimeButton.setOnClickListener(timeButtonListener);
        endTimeButton.setOnClickListener(timeButtonListener);
    }

    // 定义全局变量
    private Context context;
    // 黑夜主题开关
    private SwitchMaterial nightSwitch;
    private SharedPreferences nightSwitchPreferences;
    private Boolean nightChecked;
    // 免打扰开关
    private SwitchMaterial disturbSwitch;
    private MaterialButton startTimeButton, endTimeButton;
    private SharedPreferences disturbSwitchPreferences;
    private int timeButtonId = 0;

    // 设置nightSwitch状态
    private void setNightSwitch()
    {
        nightSwitchPreferences = getActivity().getSharedPreferences("nightSwitch", MODE_PRIVATE);
        Boolean nightChecked = nightSwitchPreferences.getBoolean("nightSwitch", false);
        nightSwitch.setChecked(nightChecked);
    }

    // 设置nightSwitch开关逻辑
    private CompoundButton.OnCheckedChangeListener nightSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            // 保存switch结果
            SharedPreferences.Editor editor = nightSwitchPreferences.edit();
            editor.putBoolean("nightSwitch", isChecked);
            editor.apply();
            // 主题切换
            if (isChecked) {
                // 设置为夜间模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // 让系统决定模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    };

    // 设置disturbSwitch状态
    private void setDisturbSwitch()
    {
        disturbSwitchPreferences = getActivity().getSharedPreferences("disturbSwitch", MODE_PRIVATE);
        Boolean isChecked = disturbSwitchPreferences.getBoolean("disturbSwitch", false);
        String startTime = disturbSwitchPreferences.getString("startTime","Start");
        String endTime = disturbSwitchPreferences.getString("endTime","End");
        disturbSwitch.setChecked(isChecked);
        startTimeButton.setText(startTime);
        endTimeButton.setText(endTime);
    }

    // 设置disturbSwitch开关逻辑
    private CompoundButton.OnCheckedChangeListener disturbSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            // 保存switch结果
            SharedPreferences.Editor editor = disturbSwitchPreferences.edit();
            editor.putBoolean("disturbSwitch", isChecked);
            editor.apply();
            // 设置时间
            if (isChecked) {
                // 保存时间
                editor.putString("startTime", startTimeButton.getText().toString());
                editor.putString("endTime", endTimeButton.getText().toString());
                editor.apply();
            } else {
                // 清空时间
                editor.putString("startTime", "Start");
                editor.putString("endTime", "End");
                editor.apply();
            }
        }
    };

    // 设置timeButton的监听事件
    private final View.OnClickListener timeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 获取按钮Id
            timeButtonId = view.getId();
            // 获取当前时间
            Calendar cal = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
            cal.setTimeZone(timeZone); // 设置时区
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            // 创建TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, timePickerListener, hour, minute, true);
            // 显示TimePickerDialog
            timePickerDialog.show();
        }
    };

    // 设置TimePickerDialog的监听器
    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 用户选择时间后更新UI
            if(timeButtonId == R.id.startTimeButton) {
                startTimeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
            if(timeButtonId == R.id.endTimeButton) {
                endTimeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }
    };
}