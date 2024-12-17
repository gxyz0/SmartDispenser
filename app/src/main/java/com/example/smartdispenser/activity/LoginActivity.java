package com.example.smartdispenser.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartdispenser.R;
import com.example.smartdispenser.fragment.LoginFragment;
import com.example.smartdispenser.fragment.RegisterFragment;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class LoginActivity extends AppCompatActivity {
    Fragment loginFragment = new LoginFragment();
    Fragment registerFragment = new RegisterFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始为loginFragment
        replaceFragment(loginFragment);

        //bottomBar事件监听器
        BottomBarListener();
    }

    // fragment替换
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_layout, fragment);
        transaction.commit();
    }

    //bottomBar事件监听器
    private void BottomBarListener() {
        AnimatedBottomBar bottom_bar = findViewById(R.id.login_register_bar);
        bottom_bar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, AnimatedBottomBar.Tab lastTab, int newIndex, AnimatedBottomBar.Tab newTab) {
                // 处理选项卡选择事件
                if(newIndex == 0) replaceFragment(loginFragment);
                if(newIndex == 1) replaceFragment(registerFragment);
            }

            @Override
            public void onTabReselected(int Index, AnimatedBottomBar.Tab tab) {
                // 处理选项卡重新选择事件
                if(Index == 0) replaceFragment(loginFragment);
                if(Index == 1) replaceFragment(registerFragment);

            }
        });
    }
}