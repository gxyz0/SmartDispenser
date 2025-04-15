package com.example.smartdispenser.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartdispenser.R;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.example.smartdispenser.database.ReminderDateTime;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HomeCardTimeAdapter extends RecyclerView.Adapter<HomeCardTimeAdapter.HomeCardTimeViewHolder> {
    // 定义全局变量
    private Context context;
    private DatabaseManager databaseManager;
    private List<ReminderDateTime> reminderDateTimeList;
    private int userId = 1;

    public HomeCardTimeAdapter(Context context, List<ReminderDateTime> reminderDateTimeList) {
        this.context = context;
        this.databaseManager = DatabaseManager.getInstance(context);
        this.reminderDateTimeList = reminderDateTimeList;
    }

    @NonNull
    @Override
    public HomeCardTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取item的视图
        View view = LayoutInflater.from(context).inflate(R.layout.home_card_time, parent, false);
        return new HomeCardTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCardTimeViewHolder holder, int position) {
        // 绑定数据
        ReminderDateTime reminderDateTime = reminderDateTimeList.get(position);
        holder.homeDateTimeText.setText(reminderDateTime.getReminderDateTime());
        holder.homeDateTimeButton.setTag(reminderDateTime);
        if (reminderDateTime.isTaking()) {
            holder.homeDateTimeButton.setIconResource(R.drawable.right);
        } else {
            holder.homeDateTimeButton.setIconResource(R.drawable.wrong);
        }
        // 设置监听事件
        holder.homeDateTimeButton.setOnClickListener(remindDateTimeButtonListener);
    }

    @Override
    public int getItemCount() {
        // 返回item个数
        return reminderDateTimeList.size();
    }

    public class HomeCardTimeViewHolder extends RecyclerView.ViewHolder {
        MaterialButton homeDateTimeButton;
        TextView homeDateTimeText;

        public HomeCardTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup homeDateTimeCard = (ViewGroup) itemView;
            homeDateTimeButton = homeDateTimeCard.findViewById(R.id.home_card_time_button);
            homeDateTimeText = homeDateTimeCard.findViewById(R.id.home_card_time_text);
        }
    }

    // 设置datetime按钮监听事件
    private final View.OnClickListener remindDateTimeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MaterialButton bt = (MaterialButton) view;
            ReminderDateTime reminderDateTime = (ReminderDateTime) bt.getTag();
            if (reminderDateTime.isTaking()) {
                bt.setIconResource(R.drawable.wrong);
                reminderDateTime.setIsTaking(false);
                bt.setTag(reminderDateTime);
            } else {
                bt.setIconResource(R.drawable.right);
                reminderDateTime.setIsTaking(true);
                bt.setTag(reminderDateTime);
            }
            // 修改isTaking数据库内容
            new Thread(new Runnable() {
                @Override
                public void run() {
                    databaseManager.startConnection();
                    databaseManager.updateReminderDateTimeIsTaking(reminderDateTime);
                    System.out.println("HomeFragment: ReminderDateTime修改完成！");
                }
            }).start();
        }
    };
}