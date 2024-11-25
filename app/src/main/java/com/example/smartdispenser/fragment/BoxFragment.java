package com.example.smartdispenser.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dd.CircularProgressButton;
import com.example.smartdispenser.R;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.room.DatabaseManager;
import com.example.smartdispenser.room.medication.Medication;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

        // 设置connectButton按钮监听器
        connectButton = view.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonListener);
        // 获取Medication数据库内容
        getMedicationList();
        // 设置RecyclerView
        RecyclerView boxCardLayout = view.findViewById(R.id.box_card_layout);
        // 采用线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        boxCardLayout.setLayoutManager(linearLayoutManager);
        // 调用BoxCardAdapter
        BoxCardAdapter boxCardAdapter = new BoxCardAdapter(requireContext(), medicationList);
        boxCardLayout.setAdapter(boxCardAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // 定义全局变量
    int userId = 1;
    CircularProgressButton connectButton;
    List<Medication> medicationList;

    // 设置connectButton监听事件
    private final View.OnClickListener connectButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setConnectButton();
        }
    };

    // 设置connectButton状态
    private void setConnectButton() {
        connectButton.setIndeterminateProgressMode(true);
        connectButton.setProgress(50);
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            public void onFinish() {
                //connectButton.setProgress(100);
                connectButton.setProgress(-1);
            }
        }.start();
        new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long l) {
            }

            public void onFinish() {
                connectButton.setProgress(0);
            }
        }.start();
    }

    // 获取Medication数据库内容
    private void getMedicationList() {
        DatabaseManager databaseManager = new DatabaseManager(requireContext());
        Future<List<Medication>> future = databaseManager.getMedicationsByUserId(userId);
        try {
            medicationList = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        databaseManager.shutdown();
    }

}