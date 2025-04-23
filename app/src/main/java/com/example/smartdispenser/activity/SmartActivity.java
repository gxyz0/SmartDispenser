package com.example.smartdispenser.activity;

import static com.example.smartdispenser.util.ImageUtils.createImageFile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.smartdispenser.APIClient;
import com.example.smartdispenser.ImageAPIClient;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.UserInfo;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartdispenser.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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
    private MaterialButton identifyButton;
    private MaterialButton confirmButton;
    // 定义输入框
    private TextInputLayout healthStatusInput;
    private TextInputLayout suggestionInput;
    private TextInputEditText healthStatusText;
    private TextInputEditText suggestionText;
    // 定义图片框
    private ImageView prescriptionImage;
    private TextInputLayout prescriptionInput;
    private TextInputEditText prescriptionText;
    // 定义操作码和图片Uri
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CROP_IMAGE_REQUEST = 69;
    private Uri defaultImageUri = Uri.parse("android.resource://com.example.smartdispenser" + "/" + R.drawable.add_prescription); // 类级别的变量来存储图片的 Uri
    private Uri imageUri = defaultImageUri;
    // 定义userInfo
    private UserInfo userInfo;
    // 定义response
    private String response;
    // 定义reminderList、medicationList
    private List<Reminder> reminderList = null;
    private List<Medication> medicationList = null;

    // 定义client
    APIClient APIClient = new APIClient();
    ImageAPIClient imageAPIClient = new ImageAPIClient(context);

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

        // 设置identifyButton事件监听器
        identifyButton = findViewById(R.id.smart_identify_button);
        identifyButton.setOnClickListener(identifyButtonListener);

        // 设置confirmButton事件监听器
        confirmButton = findViewById(R.id.smart_confirm_button);
        confirmButton.setOnClickListener(confirmButtonListener);

        // 设置处方图片框监听事件
        prescriptionImage = findViewById(R.id.prescription_image);
        prescriptionImage.setOnClickListener(prescriptionImageListener);

        // 传入默认图片
        Glide.with(context) // 传入context
                .load(imageUri) // 传入图片的Uri
                .into(prescriptionImage);

        // 设置healthStatusInput输入框
        healthStatusInput = findViewById(R.id.health_status_input);
        healthStatusText = healthStatusInput.findViewById(R.id.text_input);
        setHealthStatusInput();

        // 获取suggestionInput输入框
        suggestionInput = findViewById(R.id.suggestion_input);
        suggestionText = suggestionInput.findViewById(R.id.text_input);
        suggestionInput.setHint("(Name, Quantity, Time, Days)");

        // 设置prescriptionInput输入框
        prescriptionInput = findViewById(R.id.prescription_input);
        prescriptionText = prescriptionInput.findViewById(R.id.text_input);
        prescriptionInput.setHint("Recognition Result");
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
        healthStatusInput.setHint("Please enter the health status");
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
            String query = healthStatusText.getText().toString() + "。请根据我上面的健康状况信息给予建议，格式为(药片名称,药片数量,服药时间,持续服用的天数)，例如(asplrin,1,20:00,10)，请严格按照格式回复我1到3种药物，药物名称用英文，只回复括号内的内容，用换行符号隔开";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    response = APIClient.getResponse(query);
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

    // 设置identifyButton监听事件
    private final View.OnClickListener identifyButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // String question = "请根据我给予你的处方图片生成建议信息，格式为(药片名称,药片数量,服药时间,持续服用的天数)，例如(asplrin,1,20:00,10)，请根据处方内容严格按照格式回复我1到3种药物，药物名称用英文，只回复括号内的内容，用换行符号隔开";
            String question = "请读取图片中的文字并告诉我";
            if(imageUri != defaultImageUri) {
                imageAPIClient.initWebSocket();
                imageAPIClient.sendRequest(imageUri,question);
                // 设置回调
                imageAPIClient.setCallback(new ImageAPIClient.WebSocketCallback() {
                    @Override
                    public void onMessageReceived(String message) {
                        message = message.replaceAll("[\n]", " ");
                        prescriptionText.setText(message);
                        Log.d("WebSocket", "收到内容：" + message);
                        String query = message + "。请根据上面的处方信息给予用药建议，格式为(药片名称,药片数量,服药时间,持续服用的天数)，例如(asplrin,1,20:00,10)，请严格按照格式回复我1到3种药物，药物名称用英文，只回复括号内的内容，用换行符号隔开";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                response = APIClient.getResponse(query);
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
                });
            }
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

    // 设置prescriptionImage监听事件
    private final View.OnClickListener prescriptionImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 打开图库
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    };

    // 获取图片Uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // 动态创建裁剪后的图片文件
            File croppedImageFile = createImageFile(context);
            UCrop.of(imageUri, Uri.fromFile(croppedImageFile))
                    .withAspectRatio(7, 4) // 设置裁剪比例
                    .withMaxResultSize(350, 200) // 设置最大结果大小
                    .start(this);
            // 传入图片
//            Glide.with(context) // 传入context
//                    .load(imageUri) // 传入图片的Uri
//                    .into(prescriptionImage);
        }
        if (requestCode == CROP_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = UCrop.getOutput(data);
            if (imageUri != null) {
                Glide.with(context) // 传入context
                        .load(imageUri) // 传入图片的Uri
                        .into(prescriptionImage);

            }
        }
    }

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