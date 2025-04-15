package com.example.smartdispenser.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartdispenser.R;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.adapter.DrawerExpandableListAdapter;
import com.example.smartdispenser.adapter.FragmentPagerAdapter;
import com.example.smartdispenser.adapter.HomeCardTimeAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.UserInfo;
import com.example.smartdispenser.fragment.BoxFragment;
import com.example.smartdispenser.fragment.HomeFragment;
import com.example.smartdispenser.fragment.RemindFragment;
import com.example.smartdispenser.fragment.SettingFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {
    // 定义userId
    int userId = 1;
    // 定义context
    Context context = this;
    DatabaseManager databaseManager = DatabaseManager.getInstance(this);
    // 定义ViewPager切换页面
    private ViewPager2 viewPager;
    // 定义Fragment页面
    private HomeFragment homeFragment = new HomeFragment();
    private BoxFragment boxFragment = new BoxFragment();
    private RemindFragment remindFragment = new RemindFragment();
    private SettingFragment settingFragment = new SettingFragment();
    // 定义抽屉式侧边栏的内容
    private List<String> groups = new ArrayList<>();
    private List<List<String>> children = new ArrayList<>();
    // 定义侧边栏的userInfo
    UserInfo userInfo;
    // 设置数据handler
    private Handler handler;

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
        intent.putExtra("Page", 0);

        // 获取userId
        SharedPreferences sharedPreferences = this.getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 1);

        // 设置主题
        SharedPreferences nightSwitchPreferences = this.getSharedPreferences("nightSwitch", MODE_PRIVATE);
        Boolean nightChecked = nightSwitchPreferences.getBoolean("nightSwitch", false);
        if (nightChecked) {
            // 设置为夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // 让系统决定模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        // 设置抽屉式侧边栏监听器
        DrawerLayoutListener();

        // 设置bottom_bar监听器
        BottomBarListener();

        // 设置抽屉式侧边栏的内容
        // 设置handler 获取数据后更新ui
        showData();
        setDrawerList();
        // 设置抽屉式侧边栏 修改 退出 按钮监听器
        DrawerButtonListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if (viewPager.getAdapter() == null) viewPager.setAdapter(pagerAdapter);
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

    private void setDrawerList() {
        groups.add("User Phone");
        groups.add("User Email");
        groups.add("User Gender");
        groups.add("User Birthday");
        groups.add("Health Status");
        // 获取数据库userInfo数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                userInfo = databaseManager.getUserInfo(userId);
                System.out.println("MainActivity: UserInfo获取完成！");
                // 发送消息到Handler
                Message msg = new Message();
                msg.what = 1; // 设置消息类型
                msg.obj = userInfo; // 将数据放入消息
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void DrawerButtonListener() {
        MaterialButton userNicknameButton = findViewById(R.id.user_nickname_button);
        MaterialButton exit = findViewById(R.id.exit);
        userNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到InfoCollectionActivity
                Intent intent = new Intent(context, InfoCollectionActivity.class);
                context.startActivity(intent);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //修改SharedPreference为未登录状态
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                // 跳转到LoginActivity
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });
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
                    // 设置userInfo
                    List<String> childrenlist1 = new ArrayList<>();
                    List<String> childrenlist2 = new ArrayList<>();
                    List<String> childrenlist3 = new ArrayList<>();
                    List<String> childrenlist4 = new ArrayList<>();
                    List<String> childrenlist5 = new ArrayList<>();
                    childrenlist1.add(userInfo.getUserPhone() + "");
                    childrenlist2.add(userInfo.getUserEmail() + "");
                    childrenlist3.add(userInfo.getUserGender() + "");
                    childrenlist4.add(userInfo.getUserBirthday() + "");
                    childrenlist5.add(userInfo.getUserHealthStatus() + "");
                    children.add(childrenlist1);
                    children.add(childrenlist2);
                    children.add(childrenlist3);
                    children.add(childrenlist4);
                    children.add(childrenlist5);
                    // 设置用户名
                    TextView userNickname = findViewById(R.id.user_nickname);
                    userNickname.setText(userInfo.getUserNickname());
                    // 获取图像uri
                    ImageView userImage = findViewById(R.id.user_avatar);
                    userImage.setImageURI(Uri.parse(userInfo.getUserImage()));
                    // 设置抽屉式侧边栏的adapter
                    DrawerExpandableListAdapter adapter = new DrawerExpandableListAdapter(groups, children);
                    ExpandableListView drawerList = findViewById(R.id.drawer_list);
                    drawerList.setAdapter(adapter);
                }
            }
        };
    }

}