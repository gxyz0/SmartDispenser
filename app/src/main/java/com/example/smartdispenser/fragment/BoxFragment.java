package com.example.smartdispenser.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dd.CircularProgressButton;
import com.example.smartdispenser.PostRequest;
import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.MainActivity;
import com.example.smartdispenser.activity.SmartActivity;
import com.example.smartdispenser.adapter.BoxCardAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.example.smartdispenser.NetworkManager;
import com.example.smartdispenser.adapter.DrawerExpandableListAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.example.smartdispenser.database.User;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoxFragment extends Fragment {

    public BoxFragment() {
        // Required empty public constructor
    }

    public static BoxFragment newInstance() {
        BoxFragment fragment = new BoxFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_box, container, false);
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

        // 获取netWorkManger
        netWorkManger = NetworkManager.getInstance(context);

        // 设置connectButton按钮监听器
        connectButton = view.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonListener);
        connectButton.post(new Runnable() {
            @Override
            public void run() {
                connectButton.performClick();
            }
        });

        // 设置toSmartButton跳转事件
        toSmartButton = view.findViewById(R.id.to_smart_button);
        toSmartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到MainActivity
                Intent intent = new Intent(context, SmartActivity.class);
                context.startActivity(intent);
            }
        });

        // 设置RecyclerView
        boxCardLayout = view.findViewById(R.id.box_card_layout);
        // 采用线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        boxCardLayout.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取Medication数据库内容
        getMedicationList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isTimerRunning) {
            // 关闭设备查询服务
            if (connectButton.getProgress() == 50) netWorkManger.stopDiscovery();
            countDownTimer.cancel();
        }
        if (thread != null) thread.interrupt();
    }

    // 定义全局变量
    int userId = 1;
    private Context context;
    private DatabaseManager databaseManager;
    private NetworkManager netWorkManger;
//    private PostRequest postRequest = new PostRequest();
    private CircularProgressButton connectButton;
    private MaterialButton toSmartButton;

    private List<Medication> medicationList;
    private RecyclerView boxCardLayout;
    private BoxCardAdapter boxCardAdapter;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning;
    private Thread thread;

    // 设置connectButton监听事件
    private final View.OnClickListener connectButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 按钮初始状态 允许点击
            if (connectButton.getProgress() == 0) {
                // 设置connectButton状态
                setConnectButton();
            }
        }
    };

    // 设置connectButton样式 设置设备查询服务
    private void setConnectButton() {
        // 启动设备查询服务
        netWorkManger.URL = null;
        netWorkManger.startDiscovery();
        // 设置connectButton样式
        connectButton.setIndeterminateProgressMode(true);
        connectButton.setProgress(50);
        isTimerRunning = true;
        countDownTimer = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long l) {
                if (netWorkManger.URL != null) {
                    // 关闭设备查询服务
                    netWorkManger.stopDiscovery();
                    // 发送Post请求
                    PostRequest.postUserId(new User(userId, null, null), netWorkManger.URL + "/userId");
//                    postRequest.postMedicationJson(postRequest.getUploadMedicationList(context, userId), netWorkManger.URL + "/medication");
//                    postRequest.postReminderJson(postRequest.getUploadReminderList(context, userId), netWorkManger.URL + "/reminder");
                    // 设置connectButton正确样式
                    connectButton.setProgress(100);
                    // 关闭计时器
                    this.cancel();
                }
            }

            public void onFinish() {
                isTimerRunning = false;
                // 关闭设备查询服务
                netWorkManger.stopDiscovery();
                // 设置connectButton错误样式
                connectButton.setProgress(-1);
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {
                    }

                    public void onFinish() {
                        connectButton.setProgress(0);
                    }
                }.start();
            }
        }.start();
    }

    // 获取Medication数据库内容
    private void getMedicationList() {
        // 查询数据库
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                medicationList = databaseManager.getMedication(userId);
                System.out.println("BoxFragment: Medication获取完成！");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!medicationList.isEmpty()) {
                            // 调用BoxCardAdapter
                            BoxCardAdapter boxCardAdapter = new BoxCardAdapter(requireContext(), medicationList);
                            boxCardLayout.setAdapter(boxCardAdapter);
                        }
                    }
                });
            }
        });
        thread.start();
    }

}