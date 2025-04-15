package com.example.smartdispenser.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.MainActivity;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.User;
import com.example.smartdispenser.database.UserInfo;
import com.example.smartdispenser.util.ViewUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnFocusChangeListener {

    private int userId = 0;
    private Context context;
    private DatabaseManager databaseManager;

    private EditText editTextPassword;
    private EditText editTextUsername;
    private ImageButton eye;
    public boolean  isVisible = false ;
    private TextView forgetPsw;
    private Button btnLogin;
    private SharedPreferences sharedPreferences;
    private CheckBox isRemember;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseManager = DatabaseManager.getInstance(requireContext());
        context = requireContext();

        //获得密码用户名（username是电话号）
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        btnLogin = view.findViewById(R.id.buttonLogin);

        //是否记住密码
        isRemember = view.findViewById(R.id.checkRemember);

        //监听焦点变更，及时收起键盘
        editTextUsername.setOnFocusChangeListener(this);
        editTextUsername.addTextChangedListener(new HideTextWatcher(editTextUsername, 11));
        editTextPassword.addTextChangedListener(new HideTextWatcher(editTextPassword, 6));

        //获取密码是否可见图标
        eye = view.findViewById(R.id.ibPasswordToggle_login);
        eye.setOnClickListener(view1 -> togglePasswordVisibility());


//        //开始直接设置密码可见（保持形式一致，没有字体间隔差异）
//        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        //通过SharedPreference检查是否记住密码配置
        sharedPreferences = requireActivity().getSharedPreferences("config", MODE_PRIVATE);
        boolean isRememberMeChecked = sharedPreferences.getBoolean("rememberMe", false);

        //如果记住直接赋值
        if (isRememberMeChecked) {
            editTextUsername.setText(sharedPreferences.getString("username", ""));
            editTextPassword.setText(sharedPreferences.getString("password", ""));
            isRemember.setChecked(true);
        }
        //密码是否可见初始化
        eyeInitialize(isVisible);

        //忘记密码监听，忘记密码跳到新界面找回密码，暂时用MainActivity代替
        forgetPsw = view.findViewById(R.id.loginForgetPassword);
        forgetPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        //检查是否登陆成功
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });
    }

    //eye图标初始化
    private void eyeInitialize(boolean isVisible) {
        if (!isVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eye.setImageResource(R.drawable.eye_off); // 不可见图标
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eye.setImageResource(R.drawable.eye_on); // 可见图标
        }
    }

    //控制密码是否显示
    private void togglePasswordVisibility() {
        if (isVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eye.setImageResource(R.drawable.eye_off); // 不可见图标
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eye.setImageResource(R.drawable.eye_on); // 可见图标
        }
        isVisible = !isVisible;
        // 移动光标到文本末尾
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    //username输入框焦点变更时，检查长度是否为11位
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            String phone = editTextUsername.getText().toString();
            //如果号码不足11位
            if (TextUtils.isEmpty(phone) || phone.length() < 11) {
                editTextUsername.requestFocus();
                Toast.makeText(getActivity(), "Please input your 11 phone number!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //根据输入情况隐藏键盘
    //当上一行输入达到最大长度时，收起键盘
    private class HideTextWatcher implements TextWatcher {
        private View password;
        private int max_length;

        public HideTextWatcher(View password, int max_length) {
            this.password = password;
            this.max_length = max_length;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (s.length() == max_length) {
                ViewUtil.hideOneInputManager(LoginFragment.this, editTextUsername);
            }
        }
    }

    //登录成功后逻辑
    public void loginSuccess() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        boolean isRememberMeChecked = isRemember.isChecked();

        // 这里添加登录验证逻辑
        // 登录成功，保存用户名和密码至SharedPreference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        if (isRememberMeChecked) {
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putBoolean("rememberMe", true);
            editor.apply();
        } else {
            editor.clear();
            editor.apply();
        }
        // 跳转到另一个Activity并finish当前activity
        Activity activity = getActivity();
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        activity.finish();
    }

    //数据库中检查登录是否成功
    private void tryLogin() {
        String userName = editTextUsername.getText().toString();
        String userPassword = editTextPassword.getText().toString();
        // 打开数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                userId = databaseManager.getUserIdByNamePassword(userName, userPassword);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (userId != 0) {
                            // 保存userId
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userId", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("userId", userId);
                            editor.apply();
                            // 登录成功
                            Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
                            loginSuccess();
                        } else {
                            // 登录失败，提示用户
                            Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

}