package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.TargetUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import java.text.ParseException;

public class TargetInputPageServer {
    private Context context;

    public TargetInputPageServer(Context context) {
        this.context = context;
    }

    //弹出target添加对话框，设置target添加对话框的监听器、文本格式等
    private ItemMainAddtargetBinding targetPage; // 使用binding来获取页面控件
    public void targetView() {
        // 初始化binding类
        targetPage = ItemMainAddtargetBinding.inflate(LayoutInflater.from(context));

        // 设置对话框
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                // 设置对话框的标题
                .setTitle("制定目标")
                // 设置对话框显示的View对象
                .setView(targetPage.targetParentConstraintLayout)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", null)
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", (dialog, which) -> {
                    // 取消登录，不做任何事情
                })
                // 创建并显示对话框
                .create();
        // 显示对话框
        alertDialog.show();
        // 设置对话框的“确定”按钮监听器
        Button tempBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        tempBtn.setOnClickListener(new View.OnClickListener() {
            // 点击确定按钮时执行的动作
            @Override
            public void onClick(View v) {
                // 将焦点获取到确定按钮上
                ComponentUtil.ButtonRequestFocus(tempBtn);
                try {
                    // 将target添加页面中用户输入的信息存到数据库
                    boolean success = convertTargetMessage();
                    if(success) {
                        // 用户输入完整的信息存到数据库后刷新页面
                        MainExpandableListDBServer.refresh(0);
                        // 取消target添加弹出框的显示
                        alertDialog.dismiss();
                    } else {
                        // 如果用户输入的信息不完整就提示
                        ToastUtil.newToast(context, "请输入完整信息");
                    }
                } catch (ParseException e) {// 添加数据库的时候，字符串转换data类型时转换错误
                    ToastUtil.newToast(context, "添加失败");
                    alertDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
        // target添加弹出框消失时的监听器
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // 记录对话框消失
                MainExpandableListFillDataServer.isAddDialogShow = false;
            }
        });

        //给target添加弹出框的期待完成时间、最晚完成时间、预实施时间编辑框设置日历选择器
        TargetUtil.setPreDoPlanDeadLineTimeEditText(context,targetPage.targetTimePlanOverEditText, targetPage.targetTimeDeadLineEditText, targetPage.targetTimePreDoEditText);
       //给target添加弹出框的期待完成时间、最晚完成时间、预实施时间编辑框设置约束
        TargetUtil.setTargetConstraint(targetPage.targetTimeNeedEditText, targetPage.targetNameEditText, targetPage.targetDecorationEditText);
    }


    // 将页面中用户输入的数据存到数据库中
    // 如果必要项没有输入则返回false，否则返回true
    private boolean convertTargetMessage() throws ParseException {
        // 判断必要项是否有空的
        if(targetPage.targetNameEditText.getText().toString().equals("")
                || targetPage.targetTimeNeedEditText.getText().toString().equals("")
                || targetPage.targetTimePlanOverEditText.getText().toString().equals("")
                || targetPage.targetTimeDeadLineEditText.getText().toString().equals("")
                || targetPage.targetTimePreDoEditText.getText().toString().equals("")) {
            return false;
        }
        // 将target添加页面中用户输入的信息存到数据库
        MainExpandableListDBServer dbServer = new MainExpandableListDBServer(this.context);
        dbServer.addTarget(targetPage);
        return true;
    }

}
