package com.example.smartdispenser.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.RemindCardActivity;
import com.example.smartdispenser.room.DatabaseManager;
import com.example.smartdispenser.room.reminder.Reminder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class RemindCardAdapter extends RecyclerView.Adapter<RemindCardAdapter.RemindCardViewHolder> {
    // 定义全局变量
    private Context context;
    private List<Reminder> reminderList;
    private int userId = 1;

    public RemindCardAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
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
        holder.remindName.setText(reminderList.get(position).getReminderName());
        holder.remindTime.setText(startDate + " ~ " + endDate + "  " + time);
        // 设置标识
        holder.remindDeleteButton.setTag(position);
        holder.remindCheckButton.setTag(position);
        // 设置监听事件
        holder.remindDeleteButton.setOnClickListener(remindDeleteButtonListener);
        holder.remindCheckButton.setOnClickListener(remindCheckButtonListener);
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

    // 删除对应的remindCard
    private void deleteCard(Object tag) {
        int position = (int) tag;
        // 从reminderList中移除当前item
        Reminder deleteReminder = reminderList.get(position);
        reminderList.remove(position);
        // 数据库删除数据
        DatabaseManager databaseManager = new DatabaseManager(context);
        databaseManager.deleteReminders(deleteReminder);
        // 更新剩余item的reminderId
        for (int i = position; i < reminderList.size(); i++) {
            Reminder reminder = reminderList.get(i);
            reminder.setReminderId(reminder.getReminderId() - 1);
            // 更新数据库
            databaseManager.updateReminders(reminder);
        }
        // 关闭数据库
        databaseManager.shutdown();
        // 在RecyclerView中移除对应item
        notifyItemRemoved(position);
        // 更新RecyclerView的显示
        notifyItemRangeChanged(position, reminderList.size() - position);
    }

    // 跳转到RemindCardActivity
    private void toRemindCardActivity(Object tag) {
        int reminderId = ((int) tag) + 1;
        Intent intent = new Intent(context, RemindCardActivity.class);
        intent.putExtra("UserId", userId);
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
}
