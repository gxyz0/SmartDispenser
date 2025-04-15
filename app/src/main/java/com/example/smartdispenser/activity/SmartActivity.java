package com.example.smartdispenser.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.smartdispenser.APIClient;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.UserInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartdispenser.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SmartActivity extends AppCompatActivity {
    // 定义context
    private Context context = this;
    // 定义databaseManager
    private DatabaseManager databaseManager = DatabaseManager.getInstance(context);
    // 定义userId
    private int userId = 1;
    // 定义按钮
    private MaterialButton generateButton;
    private MaterialButton confirmButton;
    // 定义输入框
    private TextInputLayout healthStatusInput;
    private TextInputLayout suggestionInput;
    private TextInputEditText healthStatusText;
    private TextInputEditText suggestionText;
    // 定义userInfo
    private UserInfo userInfo;
    // 定义response
    private String response;
    // 药物默认图片
    private Uri defaultImageUri = Uri.parse("android.resource://com.example.smartdispenser" + "/" + R.drawable.add_medication);
    // 定义reminderList、medicationList
    private List<Reminder> reminderList = null;
    private List<Medication> medicationList = null;

    // 定义client
    APIClient client = new APIClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart);

        // 边缘到边缘布局
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取userId
        SharedPreferences sharedPreferences = this.getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 1);

        // 设置titleBar的样式和titleButton的点击事件
        setTitleBar();

        // 获取reminderList、medicationList
        getMedicationReminderList();

        // 设置generateButton事件监听器
        generateButton = findViewById(R.id.smart_generate_button);
        generateButton.setOnClickListener(generateButtonListener);

        // 设置confirmButton事件监听器
        confirmButton = findViewById(R.id.smart_confirm_button);
        confirmButton.setOnClickListener(confirmButtonListener);

        // 设置healthStatusInput输入框
        healthStatusInput = findViewById(R.id.health_status_input);
        healthStatusText = healthStatusInput.findViewById(R.id.text_input);
        setHealthStatusInput();

        // 获取suggestionInput输入框
        suggestionInput = findViewById(R.id.suggestion_input);
        suggestionText = suggestionInput.findViewById(R.id.text_input);
    }

    // 设置titleBar的样式和titleButton的点击事件
    private void setTitleBar() {
        // 设置titleText
        TextView titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.box));
        // 设置titleButton的点击事件
        MaterialButton titleButton = findViewById(R.id.title_button);
        titleButton.setIcon(getDrawable(R.drawable.back));
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到MainActivity
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("Page", 1); // 传递页面序号
                context.startActivity(intent);
                // 销毁当前Activity
                finish();
            }
        });
    }

    private void getMedicationReminderList() {
        // 查询数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                reminderList = databaseManager.getReminder(userId);
                medicationList = databaseManager.getMedication(userId);
            }
        }).start();
    }

    // 设置healthStatusInput输入框内容
    private void setHealthStatusInput() {
        // 设置hint
        healthStatusInput.setHint("Please enter the health status of the elderly");
        // 查询数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                userInfo = databaseManager.getUserInfo(userId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!(userInfo.getUserHealthStatus().isEmpty())) {
                            healthStatusText.setText(userInfo.getUserHealthStatus());
                        }
                    }
                });
            }
        }).start();
    }

    // 设置generateButton监听事件
    private final View.OnClickListener generateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 生成建议信息
            String query = healthStatusText.getText().toString() + "。请根据我上面的健康状况信息给予建议，格式为(药片名称,药片数量,服药时间,持续服用的天数)，例如(asplrin,1,20:00,10)，请严格按照格式回复我3种药物，药物名称用英文，只回复括号内的内容，用换行符号隔开";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    response = client.getResponse(query);
                    System.out.println("Response: " + response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 设置suggestionInput输入框内容
                            suggestionText.setText(response);
                        }
                    });
                }
            }).start();
        }
    };

    // 设置confirmButton监听事件
    private final View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 保存提示
            saveAlert();
        }
    };

    // 确认应用并返回首页
    private void toMainActivity() {
        // 按换行符分割
        response = suggestionText.getText().toString();
        String[] contents = response.split("\n");
        Medication[] medication = new Medication[3];
        Reminder[] reminder = new Reminder[3];
        // 获取行数
        int size = Math.min(contents.length, 3);
        for (int i = 0; i < size; i++) {
            // 去除括号和空格
            contents[i] = contents[i].replaceAll("[()]", "").replaceAll("[ ]", "");
            // 按逗号分割
            String[] parts = contents[i].split(",");
            // 获取medication、reminder
            medication[i] = new Medication(medicationList.get(i).getId(), i+1, userId, parts[0], 11, "一次" + parts[1] + "片", defaultImageUri.toString());
            reminder[i] = new Reminder(0, reminderList.size()+i+1, userId, "Suggestion"+(i+1), i+1, Integer.parseInt(parts[1]), getDateStr(0), getDateStr(Integer.parseInt(parts[3])), parts[2], false);
        }
        // 修改数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                for (int i = 0; i < size; i++) {
                    databaseManager.updateMedication(medication[i]);
                    databaseManager.insertReminder(reminder[i]);
                }
                System.out.println("Suggestion应用成功！");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 跳转到MainActivity
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("Page", 1); // 传递页面序号
                        context.startActivity(intent);
                        // 销毁当前Activity
                        finish();
                    }
                });
            }
        }).start();
    }

    // 设置保存提示
    private void saveAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tip/提示"); // 设置对话框标题
        builder.setMessage("Confirm to apply this suggestion?\n确定应用此建议？"); // 设置对话框消息
        // 设置确定按钮
        builder.setPositiveButton("Yes/确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 更新Medication、Reminder数据库，跳转到MainActivity
                toMainActivity();
            }
        });
        // 设置取消按钮
        builder.setNegativeButton("No/取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // 创建并显示AlertDialog
        builder.create();
        builder.show();
    }

    private String getDateStr(int days) {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        // 加上指定天数
        calendar.add(Calendar.DAY_OF_YEAR, days);
        // 定义日期格式
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdfDate.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区
        // 格式化日期
        return sdfDate.format(calendar.getTime());
    }

}