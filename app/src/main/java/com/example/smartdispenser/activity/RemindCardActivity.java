package com.example.smartdispenser.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.smartdispenser.R;
import com.example.smartdispenser.room.DatabaseManager;
import com.example.smartdispenser.room.medication.Medication;
import com.example.smartdispenser.room.reminder.Reminder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RemindCardActivity extends AppCompatActivity {
    // 定义context
    Context context = this;
    // 定义userId和reminderId
    int userId = 1;
    int reminderId = 1;
    // 定义reminderList和reminder
    List<Reminder> reminderList;
    Reminder reminder;
    // 定义日期选择按钮id
    int dateButtonId = 0;
    // 定义提醒名称 药物服用数
    TextInputLayout reminderName;
    TextInputLayout medicationTakingNum;
    TextInputEditText nameInput;
    TextInputEditText takingNumInput;
    // 定义药物选择按钮
    MaterialButton remindMedicationButton;
    // 定义日期选择按钮
    MaterialButton remindStartDateButton;
    MaterialButton remindEndDateButton;
    // 定义时间选择按钮
    MaterialButton remindTimeButton;

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

        // 设置titleButton的样式和点击事件
        setTitleBar();

        // 设置remindConfirmButton的监听器
        MaterialButton remindConfirmButton = findViewById(R.id.remind_confirm_button);
        remindConfirmButton.setOnClickListener(remindConfirmButtonListener);

        // 设置textInput的样式和内容
        setTextInput();

        // 设置remindMedicationButton的监听器
        remindMedicationButton.setOnClickListener(remindMedicationButtonListener);

        // 设置remindStartDateButton，remindEndDateButton的监听器
        remindStartDateButton.setOnClickListener(remindDateButtonListener);
        remindEndDateButton.setOnClickListener(remindDateButtonListener);

        // 设置remindTimeButton的监听器
        remindTimeButton.setOnClickListener(remindTimeButtonListener);
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
            }
        });
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
        remindMedicationButton = findViewById(R.id.remind_medication_button);
        remindStartDateButton = findViewById(R.id.remind_start_date_button);
        remindEndDateButton = findViewById(R.id.remind_end_date_button);
        remindTimeButton = findViewById(R.id.remind_time_button);
        // 设置hint
        reminderName.setHint("Reminder Name");
        medicationTakingNum.setHint("Number of Medications Taken");
        // 设置takingNumInput格式
        setTakingNumInput();
        // 获取ReminderList
        Intent intent = getIntent();
        userId = intent.getIntExtra("UserId", 1);
        reminderId = intent.getIntExtra("ReminderId", 1);
        DatabaseManager databaseManager = new DatabaseManager(context);
        Future<List<Reminder>> future = databaseManager.getRemindersByUserId(userId);
        try {
            reminderList = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        databaseManager.shutdown();
        // 设置内容
        // 如果数量小于reminderId，说明是插入
        if (reminderList.size() < reminderId) {
            nameInput.setText("Reminder" + reminderId);
            reminder = new Reminder(reminderId, userId, "Reminder" + reminderId, null, 0, null, null, null, 1);
        }
        // 如果数量大于reminderId，说明是修改
        else {
            reminder = reminderList.get(reminderId - 1);
            nameInput.setText(reminder.getReminderName());
            remindMedicationButton.setText(reminder.getMedicationName());
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

    // 设置药物选择提示
    private void showMedicationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Medication Name/选择药物名称"); // 设置对话框标题
        // 读取数据库
        List<Medication> medicationList;
        DatabaseManager databaseManager = new DatabaseManager(context);
        Future<List<Medication>> future = databaseManager.getMedicationsByUserId(userId);
        try {
            medicationList = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        databaseManager.shutdown();
        String medication1 = String.valueOf(medicationList.get(0).getMedicationName());
        String medication2 = String.valueOf(medicationList.get(1).getMedicationName());
        String medication3 = String.valueOf(medicationList.get(2).getMedicationName());
        // 设置列表项
        String[] items = {medication1, medication2, medication3};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户选择的列表项的处理逻辑
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

    // 更新Reminder数据库
    private void saveReminder() {
        // 获取textInput的内容
        String reminderName = String.valueOf(nameInput.getText());
        String medicationName = String.valueOf(remindMedicationButton.getText());
        int medicationTakingNum = Integer.valueOf(String.valueOf(takingNumInput.getText()));
        String reminderStartDate = String.valueOf(remindStartDateButton.getText());
        String reminderEndDate = String.valueOf(remindEndDateButton.getText());
        String reminderTime = String.valueOf(remindTimeButton.getText());
        // 更新Reminder数据库内容
        DatabaseManager databaseManager = new DatabaseManager(context);
        reminder.setReminder(reminderName, medicationName, medicationTakingNum, reminderTime, reminderStartDate, reminderEndDate);
        // 如果数量小于reminderId，说明是插入
        if (reminderList.size() < reminderId) {
            databaseManager.insertReminders(reminder);
        }
        // 如果数量大于reminderId，说明是修改
        else {
            databaseManager.updateReminders(reminder);
        }
        databaseManager.shutdown();
        // 跳转到MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("Page", 2); // 传递页面序号
        context.startActivity(intent);
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