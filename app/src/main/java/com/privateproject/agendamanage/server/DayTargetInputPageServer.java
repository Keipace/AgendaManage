package com.privateproject.agendamanage.server;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.customDialog.DayTargetDialog;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;

public class DayTargetInputPageServer {
    private Context context;
    private DayTargetDialog dayTargetDialog;

    public DayTargetInputPageServer(Context context) {
        this.context = context;
        this.dayTargetDialog = new DayTargetDialog(context, R.style.DayTargetDialog);
    }


    //弹出dayTarget添加对话框，设置dayTarget添加对话框的监听器、文本格式等
    //private ItemMainAdddaytargetBinding dayTargetPage; // 使用binding来获取页面控件

    public void daytargetView() {
        //dayTargetPage = ItemMainAdddaytargetBinding.inflate(LayoutInflater.from(context));

        dayTargetDialog.setDayTargetTitle("打卡").setCancelBtn("×", new DayTargetDialog.IOnCancelListener() {
            @Override
            public void OnCancel(DayTargetDialog dialog) {
                dialog.dismiss();

            }
        }).setConfirmBtn("√", new DayTargetDialog.IOnConfirmListener() {
            @Override
            public void OnConfirm(DayTargetDialog dialog) {

                // 将焦点获取到确定按钮上
                dayTargetDialog.confirmBtn.setFocusableInTouchMode(true);
                dayTargetDialog.confirmBtn.setFocusable(true);
                dayTargetDialog.confirmBtn.requestFocus();
                try {
                    // 将target添加页面中用户输入的信息存到数据库
                    boolean success =convertDayTargetMessage();
                    if (success) {
                        // 用户输入完整的信息存到数据库后刷新页面
                        MainExpandableListDBServer.refresh(1);
                        // 取消dayTarget添加弹出框的显示
                        dayTargetDialog.dismiss();
                    } else {
                        // 如果用户输入的信息不完整就提示
                        ToastUtil.newToast(context, "请输入完成信息");
                    }
                } catch (ParseException e) {// 添加数据库的时候，字符串转换data类型时转换错误
                    ToastUtil.newToast(context, "添加失败");
                    dayTargetDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }).setCancelable(false);
        // target添加弹出框消失时的监听器
        dayTargetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MainExpandableListFillDataServer.isAddDialogShow = false;
            }
        });
        dayTargetDialog.show();
        // target添加弹出框消失时的监听器
        dayTargetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MainExpandableListFillDataServer.isAddDialogShow = false;
            }
        });
        DayTargetUtil.setDayTargetConstraint(dayTargetDialog.dayTargetDayName, dayTargetDialog.dayTargetDayDecoration,null,null);
        // 开始时间和结束时间，点击时弹出选择时间的提示框
    }
    // 将页面中用户输入的数据存到数据库中
    // 如果必要项没有输入则返回false，否则返回true
    private boolean convertDayTargetMessage() throws ParseException {
        // 判断必要项是否有空的
        if(dayTargetDialog.dayTargetDayName.getText().toString().equals(""))
            return false;

        // 将target添加页面中用户输入的信息存到数据库
        MainExpandableListDBServer dbServer = new MainExpandableListDBServer(this.context);
        dbServer.addDayTarget(dayTargetDialog);
        return true;

    }

}
