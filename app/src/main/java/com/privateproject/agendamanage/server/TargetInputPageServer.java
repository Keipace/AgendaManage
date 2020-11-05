package com.privateproject.agendamanage.server;

import android.content.Context;
import android.content.DialogInterface;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.customDialog.TargetDialog;
import com.privateproject.agendamanage.utils.TargetUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import java.text.ParseException;

public class TargetInputPageServer {
    private Context context;
    private TargetDialog targetDialog;
    public TargetInputPageServer(Context context) {
        this.context = context;
        this.targetDialog = new TargetDialog(context, R.style.DayTargetDialog);
    }

    //弹出target添加对话框，设置target添加对话框的监听器、文本格式等

    public void targetView() {


        targetDialog.setDayTargetTitle("制定目标").setCancelBtn("×", new TargetDialog.IOnCancelListener() {
            @Override
            public void OnCancel(TargetDialog dialog) {
                dialog.dismiss();
            }
        }).setConfirmBtn("√", new TargetDialog.IOnConfirmListener() {
            @Override
            public void OnConfirm(TargetDialog dialog) {

                // 将焦点获取到确定按钮上
                targetDialog.confirmBtn.setFocusable(true);
                targetDialog.confirmBtn.setFocusableInTouchMode(true);
                targetDialog.confirmBtn.requestFocus();
                try {
                    // 将target添加页面中用户输入的信息存到数据库
                    boolean success = convertTargetMessage();
                    if (success) {
                        // 用户输入完整的信息存到数据库后刷新页面
                        MainExpandableListDBServer.refresh(0);
                        // 取消target添加弹出框的显示
                        targetDialog.dismiss();
                    } else {
                        // 如果用户输入的信息不完整就提示
                        ToastUtil.newToast(context, "请输入完整信息");
                    }
                } catch (ParseException e) {// 添加数据库的时候，字符串转换data类型时转换错误
                    ToastUtil.newToast(context, "添加失败");
                    targetDialog.dismiss();
                    e.printStackTrace();
                }
            }

        }).setCancelable(false);
        targetDialog.show();
        // target添加弹出框消失时的监听器
        targetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // 记录对话框消失
                MainExpandableListFillDataServer.isAddDialogShow = false;
            }
        });
        TargetUtil.setTargetConstraint(null,targetDialog.targetName,targetDialog.targetDecoration);
       }


    // 将页面中用户输入的数据存到数据库中
    // 如果必要项没有输入则返回false，否则返回true
    private boolean convertTargetMessage() throws ParseException {
        // 判断必要项是否有空的
        if(targetDialog.targetName.getText().toString().equals("")) {
            return false;
        }
        // 将target添加页面中用户输入的信息存到数据库
        MainExpandableListDBServer dbServer = new MainExpandableListDBServer(this.context);
        dbServer.addTarget(targetDialog);
        return true;
    }

}
