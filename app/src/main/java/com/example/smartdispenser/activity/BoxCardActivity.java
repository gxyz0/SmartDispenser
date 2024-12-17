package com.example.smartdispenser.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartdispenser.R;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BoxCardActivity extends AppCompatActivity {
    // 定义context
    private Context context = this;
    // 定义databaseManager
    private DatabaseManager databaseManager = DatabaseManager.getInstance(context);
    // 定义userId和medicationId
    private int userId = 1;
    private int medicationId = 1;
    // 定义medicationList和medication
    private List<Medication> medicationList;
    private Medication medication;
    // 定义boxCardContent
    private TextInputLayout medicationName;
    private TextInputLayout medicationQuantity;
    private TextInputLayout medicationNote;
    private TextInputEditText nameInput;
    private TextInputEditText quantityInput;
    private TextInputEditText noteInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_card);

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

        // 设置boxConfirmButton的监听器
        MaterialButton boxConfirmButton = findViewById(R.id.box_confirm_button);
        boxConfirmButton.setOnClickListener(boxConfirmButtonListener);

        // 获取medicationList
        getMedicationList();
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
            }
        });
    }

    private void getMedicationList() {
        Intent intent = getIntent();
//        userId = intent.getIntExtra("UserId", 1);
        medicationId = intent.getIntExtra("MedicationId", 1);
        // 查询数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                medicationList = databaseManager.getMedication(userId);
                System.out.println("Medication获取完成！");
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

    // 设置boxConfirmButton的监听事件
    private final View.OnClickListener boxConfirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveAlert();
        }
    };

    // 设置textInput的样式和内容
    private void setTextInput() {
        medicationName = findViewById(R.id.medication_name);
        medicationQuantity = findViewById(R.id.medication_quantity);
        medicationNote = findViewById(R.id.medication_note);
        nameInput = medicationName.findViewById(R.id.text_input);
        quantityInput = medicationQuantity.findViewById(R.id.text_input);
        noteInput = medicationNote.findViewById(R.id.text_input);
        // 设置hint
        medicationName.setHint("Medication Name");
        medicationQuantity.setHint("Medication Quantity");
        medicationNote.setHint("Medication Note");
        // 设置quantityInput格式
        setQuantityInput();
        // 设置内容
        medication = medicationList.get(medicationId - 1);
        nameInput.setText(medication.getMedicationName());
        quantityInput.setText(medication.getMedicationQuantity() + "");
        noteInput.setText(medication.getMedicationNote());
    }

    // 设置quantityInput格式
    private void setQuantityInput() {
        // 设置quantityInput输入为整数数字
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        // 设置输入数字上下限
        quantityInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // TextInputEditText失去了焦点，没有被选中
                    String text = String.valueOf(quantityInput.getText());
                    if (!text.isEmpty()) {
                        int input = Integer.valueOf(text);
                        if (input < 0) {
                            quantityInput.setText("0");
                        } else if (input > 11) {
                            quantityInput.setText("11");
                        }
                    }
                }
            }
        });
    }

    // 更新Medication数据库
    private void saveMedication() {
        // 获取textInput的内容
        String medicationName = String.valueOf(nameInput.getText());
        int medicationQuantity = Integer.valueOf(String.valueOf(quantityInput.getText()));
        String medicationNote = String.valueOf(noteInput.getText());
        // 更新Medication数据库内容
        medication.setMedication(medicationName, medicationQuantity, medicationNote, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                databaseManager.updateMedication(medication);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 跳转到MainActivity
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("Page", 1); // 传递页面序号
                        context.startActivity(intent);
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
                // 更新Medication数据库，跳转到MainActivity
                saveMedication();
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