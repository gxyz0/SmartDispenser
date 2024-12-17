package com.example.smartdispenser.activity;

import static com.example.smartdispenser.util.ImageUtils.getBitmapFromImageView;
import static com.example.smartdispenser.util.ImageUtils.saveImageToInternalStorage;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdispenser.R;
import com.example.smartdispenser.database.DatabaseManager;
import com.example.smartdispenser.database.User;
import com.example.smartdispenser.database.UserInfo;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class InfoCollectionActivity extends AppCompatActivity {

    private Context context = this;
    private int userId = 1;
    private DatabaseManager databaseManager = DatabaseManager.getInstance(this);

    private EditText editTextName, editTextBirthday, editTextEmail, editTextHealthStatus;
    private ImageView profileImage;
    private RadioGroup radioGroupGender;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_collection);

        editTextName = findViewById(R.id.edit_text_name);
        radioGroupGender = findViewById(R.id.radio_group_gender);
        editTextBirthday = findViewById(R.id.edit_text_birthday);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextHealthStatus = findViewById(R.id.edit_text_health_status);
        profileImage = findViewById(R.id.profile_image);

        // 从数据库中获取内容
        setInfoFromDatabase();

        //选择照片按钮监听
        Button buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });

        editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        //finish按钮监听
        Button finish_btn = findViewById(R.id.button_finish);
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注册信息保存到数据库
                saveToDatabase();
            }
        });

    }

    //选择图片来源
    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择图片来源");
        builder.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispatchTakePictureIntent();
            }
        });
        builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });
        builder.setNeutralButton("取消", null);
        builder.show();
    }

    //跳转至拍照activity
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //处理图片选择返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                //将拍照的图片显示到profileImage
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileImage.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                try {

                    //得到选择的相册图片，显示到profileImage
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //使用calender选择时间  年月日
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        //将选择的日期显示到生日框中
                        updateBirthdayEditText(year, month, day);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    //更新生日文本框
    private void updateBirthdayEditText(int year, int month, int day) {
        String monthString = String.valueOf(month + 1);
        String dayString = String.valueOf(day);

        // 确保月和日都是两位的
        monthString = monthString.length() == 1 ? "0" + monthString : monthString;
        dayString = dayString.length() == 1 ? "0" + dayString : dayString;

        String formattedDate = year + "-" + monthString + "-" + dayString;
        editTextBirthday.setText(formattedDate);
    }

    // 数据库中获取数据
    private void setInfoFromDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                UserInfo userInfo = databaseManager.getUserInfo(userId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 将信息填写
                        editTextName.setText(userInfo.getUserNickname());
                        editTextEmail.setText(userInfo.getUserEmail());
                        editTextBirthday.setText(userInfo.getUserBirthday());
                        editTextHealthStatus.setText(userInfo.getUserHealthStatus());
                    }
                });
            }
        }).start();
    }

    //注册表信息保存到数据库
    private void saveToDatabase() {
//        String path =
//                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
//        Drawable drawable  = profileImage.getDrawable();
//        String pic_path = path  + "user_avatar.jpg";

        String userNickname = editTextName.getText().toString().trim();
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        String userGender = selectedId == -1 ? "" : ((RadioButton) findViewById(selectedId)).getText().toString();
        String userBirthday = editTextBirthday.getText().toString().trim();
        String userEmail = editTextEmail.getText().toString().trim();
        String userHealthStatus = editTextHealthStatus.getText().toString().trim();

        // 得到照片的路径，保存头像图片至内部存储（应用所在data目录下）
        String imagePath = saveImageToInternalStorage(this, getBitmapFromImageView(profileImage), "user_avatar.png");
        if (imagePath == null) {
            Toast.makeText(this, "头像保存失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println(imagePath);

        // 根据userId更新数据库用户数据
        SharedPreferences sharedPreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseManager.startConnection();
                // 获得user
                User user = databaseManager.getUser(userId);
                String userPhone = user.getUserName();
                UserInfo userInfo = new UserInfo(userId, userNickname, userPhone, userEmail, userGender, userBirthday, userHealthStatus, imagePath);
                databaseManager.updateUserInfo(userInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
                        //填写完注册表之后跳转到主界面
                        jumpToMainPage();
                    }
                });
            }
        }).start();
    }

    //跳转到主界面
    private void jumpToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 结束当前Activity，避免用户按返回键回到MainActivity
    }

}