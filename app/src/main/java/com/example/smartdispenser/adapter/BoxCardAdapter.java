package com.example.smartdispenser.adapter;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdispenser.R;
import com.example.smartdispenser.activity.BoxCardActivity;
import com.example.smartdispenser.room.DatabaseManager;
import com.example.smartdispenser.room.medication.Medication;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BoxCardAdapter extends RecyclerView.Adapter<BoxCardAdapter.BoxCardViewHolder> {
    // 定义全局变量
    private Context context;
    private List<Medication> medicationList;
    private int userId = 1;

    public BoxCardAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public BoxCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取item的视图
        View view = LayoutInflater.from(context).inflate(R.layout.box_card, parent, false);
        return new BoxCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxCardViewHolder holder, int position) {
        // 绑定数据
        Medication medication = medicationList.get(position);
        String text = medication.getMedicationId() + "\n" + medication.getMedicationName() + "\n" + medication.medicationQuantity + "\n" + medication.getMedicationNote();
        holder.contentText.setText(text);
        // 绑定tag标识
        ViewGroup parentView = (ViewGroup) holder.boxCardContent.getParent();
        parentView.setTag(position);
        // 数据为空则删除boxCardContent
        if (medication.getMedicationName() == null) {
            deleteContent(parentView);
        }
        // 设置监听事件
        holder.boxDeleteButton.setOnClickListener(boxDeleteButtonListener);
        holder.boxEditButton.setOnClickListener(boxEditButtonListener);
    }

    @Override
    public int getItemCount() {
        // 返回item个数
        return 3;
    }

    public class BoxCardViewHolder extends RecyclerView.ViewHolder {
        ViewGroup boxCardContent;
        TextView contentText;
        MaterialButton boxDeleteButton;
        MaterialButton boxEditButton;

        public BoxCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup boxCard = (ViewGroup) itemView;
            boxCardContent = boxCard.findViewById(R.id.box_card_content);
            contentText = boxCard.findViewById(R.id.content_text);
            boxDeleteButton = boxCard.findViewById(R.id.box_delete_button);
            boxEditButton = boxCard.findViewById(R.id.box_edit_button);
        }
    }

    // 获取长辈组件
    private static ViewGroup getParentView(View child, int level) {
        ViewGroup parent = (ViewGroup) child.getParent();
        for (int i = 0; i < level - 1; i++) {
            parent = (ViewGroup) parent.getParent();
            if (parent == null) return null;
        }
        return parent;
    }

    // 设置删除按钮监听事件
    private final View.OnClickListener boxDeleteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parentView = getParentView(view, 2);
            deleteAlert(parentView);
        }
    };

    // 设置编辑按钮监听事件
    private final View.OnClickListener boxEditButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parentView = getParentView(view, 2);
            editAlert(parentView);
        }
    };

    // 删除对应的boxCard中的boxCardContent
    private void deleteContent(ViewGroup parentView) {
        // 更新数据库内容
        int medicationId = ((int) parentView.getTag()) + 1;
        Medication medication = medicationList.get(medicationId-1);
        medication.setMedication(null, 0, null);
        DatabaseManager databaseManager = new DatabaseManager(context);
        databaseManager.updateMedications(medication);
        databaseManager.shutdown();
        // 删除布局文件
        View boxCardContent = parentView.findViewById(R.id.box_card_content);
        if (boxCardContent != null) parentView.removeView(boxCardContent);
        // 设置删除按钮状态
        MaterialButton boxDeleteButton = parentView.findViewById(R.id.box_delete_button);
        boxDeleteButton.setIcon(getDrawable(context, R.drawable.wrong));
        boxDeleteButton.setText("");
        boxDeleteButton.setEnabled(false);
    }

    // 跳转到BoxCardActivity
    private void toBoxCardActivity(ViewGroup parentView){
        int medicationId = ((int) parentView.getTag()) + 1;
        Intent intent = new Intent(context, BoxCardActivity.class);
        intent.putExtra("UserId", userId);
        intent.putExtra("MedicationId", medicationId);
        context.startActivity(intent);
    }

    // 在对应的boxCard中加载boxCardContent布局文件
//    private void loadContent(ViewGroup view) {
//        // 加载布局文件
//        View boxCardContent = LayoutInflater.from(context).inflate(R.layout.box_card_content, view, false);
//        // 设置id
//        boxCardContent.setId(R.id.box_card_content);
//        // 添加到父布局中的首位
//        view.addView(boxCardContent, 0);
//        // 设置删除按钮状态
//        MaterialButton boxDeleteButton = view.findViewById(R.id.box_delete_button);
//        boxDeleteButton.setIcon(null);
//        boxDeleteButton.setText(context.getString(R.string.delete));
//        boxDeleteButton.setEnabled(true);
//    }

    // 设置删除提示
    private void deleteAlert(ViewGroup parentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tip/提示"); // 设置对话框标题
        builder.setMessage("Confirm to delete this item?\n确定删除此项？"); // 设置对话框消息
        // 设置确定按钮
        builder.setPositiveButton("Yes/确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 删除对应的boxCard中的boxCardContent
                deleteContent(parentView);
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

    // 设置编辑提示
    private void editAlert(ViewGroup parentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tip/提示"); // 设置对话框标题
        builder.setMessage("Confirm to edit this item?\n确定编辑此项？"); // 设置对话框消息
        // 设置确定按钮
        builder.setPositiveButton("Yes/确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 跳转到BoxCardActivity
                toBoxCardActivity(parentView);
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