package com.example.smartdispenser.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdispenser.HandleDateTime;
import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.MainActivity;
import com.example.smartdispenser.activity.RemindCardActivity;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.ReminderDateTime;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class RemindCardAdapter extends RecyclerView.Adapter<RemindCardAdapter.RemindCardViewHolder> {
    // 定义全局变量
    private Context context;
    private DatabaseManager databaseManager;
    private List<Reminder> reminderList;
    private int userId = 1;

    public RemindCardAdapter(List<Reminder> reminderList, Context context) {
        this.context = context;
        this.databaseManager = DatabaseManager.getInstance(context);
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public RemindCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.remind_card, parent, false);
        return new RemindCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemindCardViewHolder holder, int position) {
        // 绑定数据
        String startDate = reminderList.get(position).getReminderStartDate();
        String endDate = reminderList.get(position).getReminderEndDate();
        String time = reminderList.get(position).getReminderTime();
        boolean isReminderActive = reminderList.get(position).getReminderActive();
        holder.remindName.setText(reminderList.get(position).getReminderName());
        holder.remindTime.setText(startDate + " ~ " + endDate + "  " + time);
        holder.remindSwitch.setChecked(isReminderActive);
        // 设置标识
        holder.remindDeleteButton.setTag(position);
        holder.remindCheckButton.setTag(position);
        holder.remindSwitch.setTag(position);
        // 设置监听事件
        holder.remindDeleteButton.setOnClickListener(remindDeleteButtonListener);
        holder.remindCheckButton.setOnClickListener(remindCheckButtonListener);
        holder.remindSwitch.setOnCheckedChangeListener(remindSwitchListener);
        // 检查是否因为免打扰禁用
        if(preventReminder(time,position)) {
            System.out.println("禁用时间："+time);
            holder.remindSwitch.setChecked(false);
            holder.remindSwitch.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class RemindCardViewHolder extends RecyclerView.ViewHolder {
        TextView remindName, remindTime;
        SwitchMaterial remindSwitch;
        MaterialButton remindDeleteButton;
        MaterialButton remindCheckButton;

        public RemindCardViewHolder(@NonNull View itemView) {
            super(itemView);

            ViewGroup remindCard = (ViewGroup) itemView;
            remindName = remindCard.findViewById(R.id.remind_name);
            remindTime = remindCard.findViewById(R.id.remind_time);
            remindSwitch = remindCard.findViewById(R.id.remind_switch);
            remindDeleteButton = remindCard.findViewById(R.id.remind_delete_button);
            remindCheckButton = remindCard.findViewById(R.id.remind_check_button);
        }
    }

    // 设置删除按钮监听事件
    private final View.OnClickListener remindDeleteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            deleteAlert(view.getTag());
        }
    };

    // 设置查看按钮监听事件
    private View.OnClickListener remindCheckButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkAlert(view.getTag());
        }
    };

    // 设置Switch按钮逻辑
    private CompoundButton.OnCheckedChangeListener remindSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            System.out.println("switch更新");
            // 获取reminder
            Reminder reminder = reminderList.get((int) view.getTag());
            reminder.setReminderActive(isChecked);
            // 获取reminderDateTimeList
            List<ReminderDateTime> reminderDateTimeList = new ArrayList<>();
            HandleDateTime.splitDateTime(reminder,reminderDateTimeList);
            // 打开数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    databaseManager.startConnection();
                    // 更新reminder
                    databaseManager.updateReminder(reminder);
                    // 插入或删除reminderDateTime
                    if(isChecked) databaseManager.insertReminderDateTime(reminderDateTimeList);
                    else databaseManager.deleteReminderDateTime(reminderDateTimeList);
                }
            }).start();
        }
    };

    // 删除对应的remindCard
    private void deleteCard(Object tag) {
        int position = (int) tag;
        // 从reminderList中移除当前item
        Reminder deleteReminder = reminderList.get(position);
        reminderList.remove(position);
        // 获取reminderDateTime
        List<ReminderDateTime> reminderDateTimeList = new ArrayList<>();
        HandleDateTime.splitDateTime(deleteReminder,reminderDateTimeList);
        // 打开数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                databaseManager.deleteReminder(deleteReminder);
                System.out.println("Reminder删除成功！");
                databaseManager.deleteReminderDateTime(reminderDateTimeList);
                System.out.println("ReminderDateTime删除成功！");
                // 更新剩余reminder的reminderId
                for (int i = position; i < reminderList.size(); i++) {
                    Reminder reminder = reminderList.get(i);
                    reminder.setReminderId(reminder.getReminderId() - 1);
                    // 更新数据库
                    databaseManager.updateReminder(reminder);
                }
            }
        }).start();
        // 在RecyclerView中移除对应item
        notifyItemRemoved(position);
        // 更新RecyclerView的显示
        notifyItemRangeChanged(position, reminderList.size() - position);
    }

    // 跳转到RemindCardActivity
    private void toRemindCardActivity(Object tag) {
        int reminderId = ((int) tag) + 1;
        Intent intent = new Intent(context, RemindCardActivity.class);
//        intent.putExtra("UserId", userId);
        intent.putExtra("ReminderId", reminderId);
        context.startActivity(intent);
    }

    // 设置删除提示
    private void deleteAlert(Object tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tip/提示"); // 设置对话框标题
        builder.setMessage("Confirm to delete this item?\n确定删除此项？"); // 设置对话框消息
        // 设置确定按钮
        builder.setPositiveButton("Yes/确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCard(tag);
            }
        });
        // 设置取消按钮
        builder.setNegativeButton("No/取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // 创建并显示AlertDialog
        builder.create();
        builder.show();
    }

    // 设置查看提示
    private void checkAlert(Object tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tip/提示"); // 设置对话框标题
        builder.setMessage("Confirm to check this item?\n确定查看此项？"); // 设置对话框消息
        // 设置确定按钮
        builder.setPositiveButton("Yes/确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                toRemindCardActivity(tag);
            }
        });
        // 设置取消按钮
        builder.setNegativeButton("No/取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // 创建并显示AlertDialog
        builder.create();
        builder.show();
    }

    // 根据免打扰时间，判断禁用哪些reminder
    private Boolean preventReminder(String timeStr, int position) {
        SharedPreferences disturbSwitchPreferences = context.getSharedPreferences("disturbSwitch", MODE_PRIVATE);
        Boolean isChecked = disturbSwitchPreferences.getBoolean("disturbSwitch", false);
        String startTimeStr = disturbSwitchPreferences.getString("startTime","Start");
        String endTimeStr = disturbSwitchPreferences.getString("endTime","End");
        if(startTimeStr.equals("Start") || endTimeStr.equals("End")) return false;
        // 获取时间的Date格式
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdfDateTime.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        Date time, startTime, endTime;
        try {
            time = sdfDateTime.parse(timeStr);
            startTime = sdfDateTime.parse(startTimeStr);
            endTime = sdfDateTime.parse(endTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 比较时间大小
        boolean isBetween = false;
        if(startTime.before(endTime)) {
            isBetween = time.after(startTime) && time.before(endTime);
        } else if (startTime.after(endTime)) {
            isBetween = time.after(startTime) || time.before(endTime);
        }
//        if(isBetween) {
//            // 获取reminder
//            Reminder reminder = reminderList.get(position);
//            reminder.setReminderActive(false);
//            // 打开数据库
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    databaseManager.startConnection();
//                    // 更新reminder
//                    databaseManager.updateReminder(reminder);
//                }
//            }).start();
//        }
        return isBetween;
    }
}
