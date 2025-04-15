package com.example.smartdispenser.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartdispenser.HandleDateTime;
import com.example.smartdispenser.R;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.example.smartdispenser.database.Reminder;
import com.example.smartdispenser.database.ReminderDateTime;
import com.example.smartdispenser.database.UserInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RemindCardActivity extends AppCompatActivity {
    // 定义context
    private Context context = this;
    // 定义databaseManager
    private DatabaseManager databaseManager = DatabaseManager.getInstance(context);
    // 定义userId和reminderId
    private int userId = 1;
    private int reminderId = 1;
    int medicationId = 1;
    // 定义reminderList和reminder
    private List<Reminder> reminderList;
    private List<Medication> medicationList;
    private Reminder reminder;
    // 定义日期选择按钮id
    private int dateButtonId = 0;
    // 定义提醒名称 药物服用数
    private TextInputLayout reminderName;
    private TextInputLayout medicationTakingNum;
    private TextInputEditText nameInput;
    private TextInputEditText takingNumInput;
    // 定义确定按钮
    private MaterialButton remindConfirmButton;
    // 定义药物选择按钮
    private MaterialButton remindMedicationButton;
    // 定义日期选择按钮
    private MaterialButton remindStartDateButton;
    private MaterialButton remindEndDateButton;
    // 定义时间选择按钮
    private MaterialButton remindTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_card);

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

        // 设置titleButton的样式和点击事件
        setTitleBar();

        // 设置remindConfirmButton的监听器
        remindConfirmButton = findViewById(R.id.remind_confirm_button);
        remindConfirmButton.setOnClickListener(remindConfirmButtonListener);

        // 设置remindMedicationButton的监听器
        remindMedicationButton = findViewById(R.id.remind_medication_button);
        remindMedicationButton.setOnClickListener(remindMedicationButtonListener);

        // 设置remindStartDateButton，remindEndDateButton的监听器
        remindStartDateButton = findViewById(R.id.remind_start_date_button);
        remindEndDateButton = findViewById(R.id.remind_end_date_button);
        remindStartDateButton.setOnClickListener(remindDateButtonListener);
        remindEndDateButton.setOnClickListener(remindDateButtonListener);

        // 设置remindTimeButton的监听器
        remindTimeButton = findViewById(R.id.remind_time_button);
        remindTimeButton.setOnClickListener(remindTimeButtonListener);

        // 获取reminderList
        getReminderList();
    }

    // 设置titleBar的样式和titleButton的点击事件
    private void setTitleBar() {
        // 设置titleText
        TextView titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.remind));
        // 设置titleButton的点击事件
        MaterialButton titleButton = findViewById(R.id.title_button);
        titleButton.setIcon(getDrawable(R.drawable.back));
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到MainActivity
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("Page", 2); // 传递页面序号
                context.startActivity(intent);
                // 销毁当前Activity
                finish();
            }
        });
    }

    private void getReminderList() {
        Intent intent = getIntent();
//        userId = intent.getIntExtra("UserId", 1);
        reminderId = intent.getIntExtra("ReminderId", 1);
        // 查询数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                medicationList = databaseManager.getMedication(userId);
                reminderList = databaseManager.getReminder(userId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 设置textInput的样式和内容
                        setTextInput();
                    }
                });
            }
        }).start();
    }

    // 设置remindConfirmButton的监听事件
    private final View.OnClickListener remindConfirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveAlert();
        }
    };

    // 设置textInput的样式和内容
    private void setTextInput() {
        reminderName = findViewById(R.id.reminder_name);
        medicationTakingNum = findViewById(R.id.medication_taking_num);
        nameInput = reminderName.findViewById(R.id.text_input);
        takingNumInput = medicationTakingNum.findViewById(R.id.text_input);
        // 设置hint
        reminderName.setHint("Reminder Name");
        medicationTakingNum.setHint("Number of Medications Taken");
        // 设置takingNumInput格式
        setTakingNumInput();
        // 设置内容
        // 如果数量小于reminderId，说明是插入
        if (reminderList.size() < reminderId) {
            nameInput.setText("Reminder" + reminderId);
            reminder = new Reminder(0, reminderId, userId, "Reminder" + reminderId, 0, 0, null, null, null, true);
        }
        // 如果数量大于等于reminderId，说明是修改
        else {
            reminder = reminderList.get(reminderId - 1);
            nameInput.setText(reminder.getReminderName());
            medicationId = reminder.getMedicationId();
            String medicationName = medicationList.get(medicationId - 1).getMedicationName();
            remindMedicationButton.setText(medicationName);
            takingNumInput.setText(reminder.getMedicationTakingNum() + "");
            remindStartDateButton.setText(reminder.getReminderStartDate());
            remindEndDateButton.setText(reminder.getReminderEndDate());
            remindTimeButton.setText(reminder.getReminderTime());
        }
    }

    // 设置takingNumInput格式
    private void setTakingNumInput() {
        // 设置quantityInput输入为整数数字
        takingNumInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        // 设置输入数字上下限
        takingNumInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // TextInputEditText失去了焦点，没有被选中
                    String text = String.valueOf(takingNumInput.getText());
                    if (!text.isEmpty()) {
                        int input = Integer.valueOf(text);
                        if (input < 0) {
                            takingNumInput.setText("0");
                        } else if (input > 11) {
                            takingNumInput.setText("11");
                        }
                    }
                }
            }
        });
    }

    // 设置remindMedicationButton的监听事件
    private final View.OnClickListener remindMedicationButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showMedicationAlert();
        }
    };

    // 设置药物选择提示
    private void showMedicationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Medication Name/选择药物名称"); // 设置对话框标题
        // 获取medication
        String medication1 = String.valueOf(medicationList.get(0).getMedicationName());
        String medication2 = String.valueOf(medicationList.get(1).getMedicationName());
        String medication3 = String.valueOf(medicationList.get(2).getMedicationName());
        // 设置列表项
        String[] items = {medication1, medication2, medication3};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户选择的列表项的处理逻辑
                medicationId = which + 1;
                String selectedItem = items[which];
                // 根据用户选择更新UI
                remindMedicationButton.setText(selectedItem);
                dialog.dismiss(); // 关闭对话框
            }
        });
        // 创建并显示AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 设置DatePickerDialog的监听器
    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // 用户选择日期后更新UI
            if (dateButtonId == R.id.remind_start_date_button) {
                remindStartDateButton.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth));
            } else if (dateButtonId == R.id.remind_end_date_button) {
                remindEndDateButton.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth));
            }
        }
    };

    // 设置TimePickerDialog的监听器
    TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 用户选择时间后更新UI
            remindTimeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
        }
    };

    // 设置remindDateButton的监听事件
    private final View.OnClickListener remindDateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 获取按钮id
            dateButtonId = view.getId();
            // 获取当前日期
            Calendar cal = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
            cal.setTimeZone(timeZone); // 设置时区
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            // 创建DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, datePickerListener, year, month, day);
            // 显示DatePickerDialog
            datePickerDialog.show();
        }
    };

    // 设置remindTimeButton的监听事件
    private final View.OnClickListener remindTimeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 获取当前时间
            Calendar cal = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
            cal.setTimeZone(timeZone); // 设置时区
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            // 创建TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, timePickerListener, hour, minute, true);
            // 显示TimePickerDialog
            timePickerDialog.show();
        }
    };

    // 更新Reminder数据库
    private void saveReminder() {
        // 获取textInput的内容
        String reminderName = String.valueOf(nameInput.getText());
        int medicationTakingNum = Integer.valueOf(String.valueOf(takingNumInput.getText()));
        String reminderStartDate = String.valueOf(remindStartDateButton.getText());
        String reminderEndDate = String.valueOf(remindEndDateButton.getText());
        String reminderTime = String.valueOf(remindTimeButton.getText());
        //比较StartDate和EndDate大小




        // 更新Reminder数据库内容
        // 保存旧的reminder
        Reminder oldReminder = new Reminder(reminder.getId(), reminder.getReminderId(), reminder.getUserId(), reminder.getReminderName(), reminder.getMedicationId(),
                reminder.getMedicationTakingNum(), reminder.getReminderStartDate(), reminder.getReminderEndDate(), reminder.getReminderTime(), reminder.getReminderActive());
        // 获取新的reminder
        reminder.setReminder(reminderName, medicationId, medicationTakingNum, reminderStartDate, reminderEndDate, reminderTime);
        reminder.setReminderActive(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                List<ReminderDateTime> reminderDateTimeList = new ArrayList<>();
                // 如果数量小于reminderId，说明是插入
                if (reminderList.size() < reminderId) {
                    System.out.println("插入reminder");
                    // 插入新的reminderDateTime
                    HandleDateTime.splitDateTime(reminder, reminderDateTimeList);
                    databaseManager.insertReminderDateTime(reminderDateTimeList);
                    // 插入reminder
                    databaseManager.insertReminder(reminder);
                    System.out.println("Reminder插入完成！");
                }
                // 如果数量大于等于reminderId，说明是修改
                else {
                    System.out.println("修改reminder");
                    // 删除旧的reminderDateTime
                    HandleDateTime.splitDateTime(oldReminder, reminderDateTimeList);
                    databaseManager.deleteReminderDateTime(reminderDateTimeList);
                    System.out.println(oldReminder.getReminderTime());
                    // 插入新的reminderDateTime
                    HandleDateTime.splitDateTime(reminder, reminderDateTimeList);
                    databaseManager.insertReminderDateTime(reminderDateTimeList);
                    // 更新reminder
                    databaseManager.updateReminder(reminder);
                    System.out.println("Reminder更新完成！");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 跳转到MainActivity
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("Page", 2); // 传递页面序号
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
        builder.setMessage("Confirm to save this item?\n确定保存此项？"); // 设置对话框消息
        // 设置确定按钮
        builder.setPositiveButton("Yes/确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 更新Reminder数据库，跳转到MainActivity
                saveReminder();
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
}