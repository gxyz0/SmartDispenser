package com.example.smartdispenser.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.InfoCollectionActivity;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.User;
import com.example.smartdispenser.database.UserInfo;
import com.example.smartdispenser.util.ViewUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnFocusChangeListener {

    private int userId = 0;
    private Context context;
    private DatabaseManager databaseManager;

    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextUsername;
    private ImageButton eye;
    private Button buttonRegister;
    private boolean isVisible = false;

    private Uri imageUri = Uri.parse("android.resource://com.example.smartdispenser" + "/" + R.drawable.add_avatar);
    private Uri defaultImageUri = Uri.parse("android.resource://com.example.smartdispenser" + "/" + R.drawable.add_medication); // 类级别的变量来存储图片的 Uri

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = requireContext();
        databaseManager = DatabaseManager.getInstance(context);

        //获取密码，确认密码，用户名
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        buttonRegister = view.findViewById(R.id.buttonRegisterConfirm);
        editTextUsername.setOnFocusChangeListener(this);
        editTextUsername.addTextChangedListener(new HideTextWatcher(editTextUsername, 11));
        editTextPassword.addTextChangedListener(new HideTextWatcher(editTextPassword, 6));
        editTextConfirmPassword.addTextChangedListener(new HideTextWatcher(editTextConfirmPassword, 6));
        eye = view.findViewById(R.id.ibPasswordToggle);
        eyeInitialize(isVisible);
        eye.setOnClickListener(view1 -> togglePasswordVisibility());

        buttonRegister.setOnClickListener(view12 -> registerUser());

//        //开始直接设置密码可见（保持形式一致，没有字体间隔差异）
//        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    private void eyeInitialize(boolean isVisible) {
        if (!isVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eye.setImageResource(R.drawable.eye_off); // 不可见图标
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eye.setImageResource(R.drawable.eye_on); // 可见图标
        }
    }

    //控制密码是否显示
    private void togglePasswordVisibility() {
        //和LoginFragment共享isVisible
        if (isVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eye.setImageResource(R.drawable.eye_off); // 不可见图标
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eye.setImageResource(R.drawable.eye_on); // 可见图标
        }
        isVisible = !isVisible;
        // 移动光标到文本末尾
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    //焦点改变时，检查username是否为11位
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            String phone = editTextUsername.getText().toString();
            //如果号码不足11位
            if (TextUtils.isEmpty(phone) || phone.length() < 11) {
                editTextUsername.requestFocus();
                Toast.makeText(this.getActivity(), "Please input your 11 phone number!", Toast.LENGTH_SHORT).show();
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
                ViewUtil.hideOneInputManager(RegisterFragment.this, editTextUsername);
            }
        }
    }

    //点击注册按钮时，判断两次输入的密码是否一致
    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // 检查密码是否匹配
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this.getActivity(), "Your two passwords are not same! ", Toast.LENGTH_SHORT).show();
            return;
        }

        // 在这里处理注册逻辑，比如发送网络请求等
        // ...
        // 这里没有直接写注册逻辑，而是把用户输入的密码和账号先用变量保存起来，和下一页的InfoCollection一起发送至数据库

        // 将用户数据保存至数据库(用户数据初始化)
        // 包含注册成功动作
        tryRegister();
    }

    //注册成功后逻辑
    public void registerSuccess() {
        //获取密码、用户名、是否记住密码
        CheckBox isRemember = getView().findViewById(R.id.checkRemember);
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        boolean isRememberMeChecked = isRemember.isChecked();

        //保存用户名和密码至sharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config", MODE_PRIVATE);
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
        //注册后默认让用户在InfoCollection中初始化个人信息
        Activity activity = getActivity();
        Intent intent = new Intent(activity, InfoCollectionActivity.class);
        startActivity(intent);
        activity.finish();
    }


    // 数据库 用户数据初始化
    private void tryRegister() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        // 查询数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 执行数据库连接操作
                databaseManager.startConnection();
                userId = databaseManager.getUserIdByName(username);
                // 若查找不到用户，则可以注册
                if (userId == 0) {
                    setDatabase();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 注册成功
                            Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show();
                            registerSuccess();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 注册失败
                            Toast.makeText(context, "注册失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void setDatabase() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        User user = new User(0, username, password);
        databaseManager.insertUser(user); // 插入后自动获取userId
        userId = user.getUserId();
        // 保存userId
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
        // 用户数据初始化
        if (databaseManager.getUserInfo(userId) == null) {
            UserInfo userInfo = new UserInfo(userId, "user" + userId, username, null, null, null, null, imageUri.toString());
            databaseManager.insertUserInfo(userInfo);
        }
        if (databaseManager.getMedication(userId).isEmpty()) {
            Medication medication1 = new Medication(0, 1, userId, "aspirin", 11, "早晚，1次2片", defaultImageUri.toString());
            Medication medication2 = new Medication(0, 2, userId, "ibuprofen", 11, "早中晚，1次1片", defaultImageUri.toString());
            Medication medication3 = new Medication(0, 3, userId, "vitamin", 11, "早，1次1片", defaultImageUri.toString());
            databaseManager.insertMedication(medication1);
            databaseManager.insertMedication(medication2);
            databaseManager.insertMedication(medication3);
        }
        if (databaseManager.getReminder(userId).isEmpty()) {
            Reminder reminder1 = new Reminder(0, 1, userId, "Reminder1", 1, 1, "2024-10-01", "2024-10-02", "12:00", true);
            databaseManager.insertReminder(reminder1);
        }
    }
}