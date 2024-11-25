package com.example.smartdispenser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartdispenser.R;
import com.example.smartdispenser.adapter.DrawerExpandableListAdapter;
import com.example.smartdispenser.adapter.FragmentPagerAdapter;
import com.example.smartdispenser.fragment.BoxFragment;
import com.example.smartdispenser.fragment.HomeFragment;
import com.example.smartdispenser.fragment.RemindFragment;
import com.example.smartdispenser.fragment.SettingFragment;
import com.example.smartdispenser.room.DatabaseManager;
import com.example.smartdispenser.room.medication.Medication;
import com.example.smartdispenser.room.reminder.Reminder;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {
    // 定义userId
    int userId = 1;
    // 定义ViewPager切换页面
    ViewPager2 viewPager;
    // 定义Fragment页面
    HomeFragment homeFragment = new HomeFragment();
    BoxFragment boxFragment = new BoxFragment();
    RemindFragment remindFragment = new RemindFragment();
    SettingFragment settingFragment = new SettingFragment();
    // 定义抽屉式侧边栏的内容
    List<String> groups = new ArrayList<>();
    List<List<String>> children = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 边缘到边缘布局
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置初始页面
        initPager();
        Intent intent = getIntent();
        int pageIndex = intent.getIntExtra("Page", 0);
        switchFragment(pageIndex);

        // 数据库初始化
        setDatabase();

        // 设置抽屉式侧边栏监听器
        DrawerLayoutListener();

        // 设置bottom_bar监听器
        BottomBarListener();

        // 设置抽屉式侧边栏的内容
        setDrawerList();
    }

    // 设置抽屉式侧边栏监听器
    private void DrawerLayoutListener() {
        MaterialButton titleButton = findViewById(R.id.title_button);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDrawerOpen = drawerLayout.isDrawerOpen(GravityCompat.START);
                if (!isDrawerOpen) drawerLayout.openDrawer(GravityCompat.START); // 打开抽屉
                else drawerLayout.closeDrawer(GravityCompat.START); // 关闭抽屉
            }
        });
    }

    // 设置bottom_bar监听器
    private void BottomBarListener() {
        AnimatedBottomBar bottom_bar = findViewById(R.id.bottom_bar);
        bottom_bar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, AnimatedBottomBar.Tab lastTab, int newIndex, AnimatedBottomBar.Tab newTab) {
                // 处理选项卡选择事件
                switchFragment(newIndex); //选择ViewPager的fragment
                setTitleText(newIndex);   //更新titlebar的titletext
            }

            @Override
            public void onTabReselected(int Index, AnimatedBottomBar.Tab tab) {
                // 处理选项卡重新选择事件
                switchFragment(Index); //选择ViewPager的fragment
                setTitleText(Index);   //更新titlebar的titletext
            }
        });
    }

    private void switchFragment(int index) {
        viewPager.setCurrentItem(index, true);
    }

    // fragment替换
//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.main_page, fragment);
//        transaction.commit();
//    }

    // 设置ViewPager2页面切换、监听器
    private void initPager() {
        viewPager = findViewById(R.id.main_view);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(boxFragment);
        fragments.add(remindFragment);
        fragments.add(settingFragment);
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                swicthTab(position);    //选择bottomBar的tab
                setTitleText(position); //更新titleBar的titleText
            }
        });
    }

    private void swicthTab(int position) {
        AnimatedBottomBar bottom_bar = findViewById(R.id.bottom_bar);
        bottom_bar.selectTabAt(position, true);
    }

    private void setTitleText(int index) {
        TextView textView = findViewById(R.id.title_text);
        switch (index) {
            case 0:
                textView.setText(getString(R.string.home));
                break;
            case 1:
                textView.setText(getString(R.string.box));
                break;
            case 2:
                textView.setText(getString(R.string.remind));
                break;
            case 3:
                textView.setText(getString(R.string.setting));
                break;
        }
    }

    private void setDatabase() {
        DatabaseManager databaseManager = new DatabaseManager(this);
        // 初始化medicationList
        Future<List<Medication>> medicationFuture = databaseManager.getMedicationsByUserId(userId);
        try {
            List<Medication> medicationList = medicationFuture.get();
            if (medicationList.isEmpty()) {
                Medication medication1 = new Medication(1, userId, "阿司匹林", 11, "早晚，1次2片");
                Medication medication2 = new Medication(2, userId, "布洛芬", 11, "早中晚，1次1片");
                Medication medication3 = new Medication(3, userId, "维他命", 11, "早，1次3片");
                databaseManager.insertMedications(medication1, medication2, medication3);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 初始化reminderList
        Future<List<Reminder>> reminderFuture = databaseManager.getRemindersByUserId(userId);
        try {
            List<Reminder> reminderList = reminderFuture.get();
            if (reminderList.isEmpty()) {
                Reminder reminder1 = new Reminder(1, userId, "Reminder1", "阿司匹林", 1, "20:00", "2024-10-10", "2024-10-11", 1);
                databaseManager.insertReminders(reminder1);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        databaseManager.shutdown();
    }

    private void setDrawerList() {
        groups.add("User Name");
        groups.add("User Age/Birth");
        groups.add("Health Condition");
        groups.add("Medicine Taking");
        List<String> childrenlist1 = new ArrayList<>();
        childrenlist1.add("AAA·AAA");
        List<String> childrenlist2 = new ArrayList<>();
        childrenlist2.add("60/1980.12.31");
        List<String> childrenlist3 = new ArrayList<>();
        childrenlist3.add("11111111");
        childrenlist3.add("22222222");
        childrenlist3.add("33333333");
        List<String> childrenlist4 = new ArrayList<>();
        childrenlist4.add("11111111");
        childrenlist4.add("22222222");
        childrenlist4.add("33333333");
        children.add(childrenlist1);
        children.add(childrenlist2);
        children.add(childrenlist3);
        children.add(childrenlist4);
        // 设置抽屉式侧边栏的adapter
        DrawerExpandableListAdapter adapter = new DrawerExpandableListAdapter(groups, children);
        ExpandableListView drawerList = findViewById(R.id.drawer_list);
        drawerList.setAdapter(adapter);
    }


}