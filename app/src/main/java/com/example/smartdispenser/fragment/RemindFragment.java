package com.example.smartdispenser.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.RemindCardActivity;
import com.example.smartdispenser.adapter.RemindCardAdapter;
import com.example.smartdispenser.room.DatabaseManager;
import com.example.smartdispenser.room.reminder.Reminder;
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

//        // 设置数据源
//        data = new ArrayList<>(Arrays.asList("Remind1", "Remind2", "Remind3"));
        // 获取Reminder数据库内容
        getReminderList();
        // 设置增添按钮监听器
        MaterialButton remindAddButton = view.findViewById(R.id.remind_add_button);
        remindAddButton.setOnClickListener(remindAddButtonListener);
        // 设置RecyclerView
        remindCardLayout = view.findViewById(R.id.remind_card_layout);
        // 采用线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        remindCardLayout.setLayoutManager(linearLayoutManager);
        // 调用BoxCardAdapter
        remindCardAdapter = new RemindCardAdapter(reminderList, requireContext());
        remindCardLayout.setAdapter(remindCardAdapter);
    }

    // 定义全局变量
    int userId = 1;
    List<Reminder> reminderList;
    RecyclerView remindCardLayout;
    RemindCardAdapter remindCardAdapter;
    int remindCardNum;

    // 设置增添按钮监听事件
    private final View.OnClickListener remindAddButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toRemindCardActivity();
        }
    };

    // 跳转到RemindCardActivity
    private void toRemindCardActivity() {
        int newReminderId = reminderList.size()+1;
        Intent intent = new Intent(requireContext(), RemindCardActivity.class);
        intent.putExtra("UserId", userId);
        intent.putExtra("ReminderId", newReminderId);
        requireContext().startActivity(intent);
    }

    // 在对应的remindCardLayout中加载remindCard布局文件
//    private void addCard() {
//        remindCardId++;
//        // 添加新数据到数据源
//        data.add("Remind" + remindCardId);
//        // 通知Adapter数据已更改
//        remindCardNum = data.size();
//        remindCardAdapter.notifyItemInserted(remindCardNum - 1);
//        // 滚动到最底部
//        remindCardLayout.getLayoutManager().scrollToPosition(remindCardNum - 1);
//    }

    // 获取Reminder数据库内容
    private void getReminderList() {
        DatabaseManager databaseManager = new DatabaseManager(requireContext());
        Future<List<Reminder>> future = databaseManager.getRemindersByUserId(userId);
        try {
            reminderList = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        databaseManager.shutdown();
    }
}