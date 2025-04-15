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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdispenser.NetworkManager;
import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.RemindCardActivity;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.adapter.HomeCardTimeAdapter;
import com.example.smartdispenser.adapter.RemindCardAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Reminder;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RemindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemindFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RemindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemindFragment newInstance(String param1, String param2) {
        RemindFragment fragment = new RemindFragment();
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
        return inflater.inflate(R.layout.fragment_remind, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取context
        context = requireContext();
        databaseManager = DatabaseManager.getInstance(context);

        // 获取userId
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 1);

        // 设置增添按钮监听器
        remindAddButton = view.findViewById(R.id.remind_add_button);
        remindAddButton.setOnClickListener(remindAddButtonListener);

        // 设置RecyclerView
        remindCardLayout = view.findViewById(R.id.remind_card_layout);
        // 采用线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        remindCardLayout.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置handler 获取数据后更新ui
        showData();
        // 获取Reminder数据库内容
        getReminderList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (thread != null) thread.interrupt();
    }

    // 定义全局变量
    int userId = 1;
    private Context context;
    private DatabaseManager databaseManager;
    private MaterialButton remindAddButton;
    private List<Reminder> reminderList;
    private RecyclerView remindCardLayout;
    private RemindCardAdapter remindCardAdapter;
    private Thread thread;
    private Handler handler;

    // 设置增添按钮监听事件
    private final View.OnClickListener remindAddButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toRemindCardActivity();
        }
    };

    // 跳转到RemindCardActivity
    private void toRemindCardActivity() {
        int newReminderId = reminderList.size() + 1;
        Intent intent = new Intent(requireContext(), RemindCardActivity.class);
        intent.putExtra("UserId", userId);
        intent.putExtra("ReminderId", newReminderId);
        requireContext().startActivity(intent);
    }

    // 获取Reminder数据库内容
    private void getReminderList() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                reminderList = databaseManager.getReminder(userId);
                System.out.println("RemindFragment: Reminder获取完成！");
                // 发送消息到Handler
                Message msg = new Message();
                msg.what = 1; // 设置消息类型
                msg.obj = reminderList; // 将数据放入消息
                handler.sendMessage(msg);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!reminderList.isEmpty()) {
//                            // 调用RemindCardAdapter
//                            remindCardAdapter = new RemindCardAdapter(reminderList, context);
//                            remindCardLayout.setAdapter(remindCardAdapter);
//                        }
//                    }
//                });
            }
        });
        thread.start();
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
                    // 调用RemindCardAdapter
                    remindCardAdapter = new RemindCardAdapter(reminderList, context);
                    remindCardLayout.setAdapter(remindCardAdapter);
                }
            }
        };
    }
}