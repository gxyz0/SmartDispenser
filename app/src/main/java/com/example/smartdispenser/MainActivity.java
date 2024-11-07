package com.example.smartdispenser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

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

        // 初始页面为Home
        replaceFragment(new HomeFragment());

        // 设置bottom_bar监听器
        AnimatedBottomBar bottom_bar = findViewById(R.id.bottom_bar);
        bottom_bar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, AnimatedBottomBar.Tab lastTab, int newIndex, AnimatedBottomBar.Tab newTab) {
                // 处理选项卡选择事件
                switchFragment(newIndex);
            }

            @Override
            public void onTabReselected(int Index, AnimatedBottomBar.Tab tab) {
                // 处理选项卡重新选择事件
                switchFragment(Index);
            }
        });

        // 设置抽屉式侧边栏响应
        Button closeButton = findViewById(R.id.title_button);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDrawerOpen = drawerLayout.isDrawerOpen(GravityCompat.START);
                if (!isDrawerOpen) drawerLayout.openDrawer(GravityCompat.START); // 打开抽屉
                else drawerLayout.closeDrawer(GravityCompat.START); // 关闭抽屉
            }
        });
    }

    private void switchFragment(int index) {
        switch (index) {
            case 0:
                replaceFragment(new HomeFragment());
                break;
            case 1:
                replaceFragment(new BoxFragment());
                break;
            case 2:
                replaceFragment(new RemindFragment());
                break;
            case 3:
                replaceFragment(new SettingFragment());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}