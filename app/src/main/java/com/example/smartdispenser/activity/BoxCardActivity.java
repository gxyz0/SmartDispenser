package com.example.smartdispenser.activity;

import static com.example.smartdispenser.util.ImageUtils.createImageFile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.smartdispenser.R;
import com.example.smartdispenser.adapter.BoxCardAdapter;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.Medication;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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

    //定义图像操作请求码
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    public static final int REQUEST_PERMISSIONS_CODE = 3;
    private static final int CROP_IMAGE_REQUEST = 69;

    //定义默认药品图片
    private ImageView medicationImage;
    private Uri defaultImageUri = Uri.parse("android.resource://com.example.smartdispenser" + "/" + R.drawable.add_medication); // 类级别的变量来存储图片的 Uri
    private Uri imageUri = defaultImageUri;

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

        // 放置药品的图像
        setImageInput();
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
        medicationImage = findViewById(R.id.medication_image);
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
        imageUri = Uri.parse(medication.getMedicationImage());
        Glide.with(context) // 传入 context 或 activity/fragment
                .load(imageUri) // 传入图片的 Uri
                .into(medicationImage); // 传入要显示图片的 ImageView
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

    //上传药品图像，用户可裁剪350*200dp，放置在cardview中
    private void setImageInput() {
        medicationImage = findViewById(R.id.medication_image);
        medicationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示图片来源选项
                showImagePickerOptions();
            }

        });
    }

    // 选择图片来源
    private void showImagePickerOptions() {
        CharSequence[] options = new CharSequence[]{"从相册选择", "拍照"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择图片");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    openGallery();
                } else if (which == 1) {
                    if (ContextCompat.checkSelfPermission(BoxCardActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(BoxCardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(BoxCardActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                    } else {
                        openCamera();
                    }
                }
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 创建文件来保存图片
            File photoFile = createImageFile(this);
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.MapScanner.MapScanner", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予权限，调用 openCamera 方法
                openCamera();
            } else {
                // 用户拒绝权限，处理这种情况
                Toast.makeText(this, "权限被拒绝，无法使用相机", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            startCrop(uri);
        } else if(requestCode == CAMERA_REQUEST){
            if(resultCode == RESULT_OK){
                startCrop(imageUri);
            }
        }else if (requestCode == CROP_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            imageUri = resultUri;
            if (resultUri != null) {
                Glide.with(this)
                        .load(resultUri)
                        .into(medicationImage);

            }
        }
    }

    private void startCrop(Uri uri) {
        // 动态创建裁剪后的图片文件
        File croppedImageFile = createImageFile(this);
        UCrop.of(uri, Uri.fromFile(croppedImageFile))
                .withAspectRatio(7, 4) // 设置裁剪比例
                .withMaxResultSize(350, 200) // 设置最大结果大小
                .start(this);
    }

    // 更新Medication数据库
    private void saveMedication() {
        // 获取textInput的内容
        String medicationName = String.valueOf(nameInput.getText());
        int medicationQuantity = Integer.valueOf(String.valueOf(quantityInput.getText()));
        String medicationNote = String.valueOf(noteInput.getText());

        //单独保存药品图片的uri信息
        Uri uri = imageUri;

        // 更新Medication数据库内容
        medication.setMedication(medicationName, medicationQuantity, medicationNote, imageUri.toString());
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