package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.databinding.ItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.NumberUtils;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.util.Calendar;

public class DayTargetInputPageServer {
    private Context context;

    public DayTargetInputPageServer(Context context) {
        this.context = context;
    }

    //弹出dayTarget添加对话框，设置dayTarget添加对话框的监听器、文本格式等
    private ItemMainAdddaytargetBinding dayTargetPage; // 使用binding来获取页面控件
    public void daytargetView() {
        dayTargetPage = ItemMainAdddaytargetBinding.inflate(LayoutInflater.from(context));

        // 设置对话框
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                // 设置对话框的标题
                .setTitle("打卡")
                // 设置对话框显示的View对象
                .setView(dayTargetPage.daytargetParentConstraintLayout)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", null)
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", (dialog, which) -> {
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
                tempBtn.setFocusableInTouchMode(true);
                tempBtn.setFocusable(true);
                tempBtn.requestFocus();
                try {
                    // 将target添加页面中用户输入的信息存到数据库
                    boolean success = convertDayTargetMessage();
                    if(success) {
                        // 用户输入完整的信息存到数据库后刷新页面
                        MainExpandableListDBServer.refresh(1);
                        // 取消dayTarget添加弹出框的显示
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
                MainExpandableListFillDataServer.isAddDialogShow = false;
            }
        });

        // target添加弹出框的“名称”不能为空，当焦点发生变化时触发监听器
        dayTargetPage.daytargetDayNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                dayTargetPage.daytargetDayNameEditText.setText(StringUtils.moveSpaceString(dayTargetPage.daytargetDayNameEditText.getText().toString()));
            }
        });
        // target添加弹出框的“描述”不能为空，当焦点发生变化时触发监听器
        dayTargetPage.daytargetDayDecorationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    dayTargetPage.daytargetDayDecorationEditText.setText(StringUtils.moveSpaceString(dayTargetPage.daytargetDayDecorationEditText.getText().toString()));
                }
            }
        });
        
        // 计划完成次数，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
        dayTargetPage.daytargetPlanCountsEditText.addTextChangedListener(DayTargetUtil.getTextWatcher(dayTargetPage.daytargetPlanCountsEditText));
        dayTargetPage.daytargetPlanCountsEditText.setOnFocusChangeListener(DayTargetUtil.getNotZeroFocusChangeListener(dayTargetPage.daytargetPlanCountsEditText));

        // 频率，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
        dayTargetPage.daytargetFrequcecyEditText.addTextChangedListener(DayTargetUtil.getTextWatcher(dayTargetPage.daytargetFrequcecyEditText));
        dayTargetPage.daytargetFrequcecyEditText.setOnFocusChangeListener(DayTargetUtil.getNotZeroFocusChangeListener(dayTargetPage.daytargetFrequcecyEditText));

        // 开始时间和结束时间，点击时弹出选择时间的提示框
        dayTargetPage.daytargetTimeFragmentStartEditText.setOnTouchListener(DayTargetUtil.getOnTimeTouchListener(
                context,dayTargetPage.daytargetTimeFragmentStartEditText,dayTargetPage.daytargetTimeFragmentEndEditText, true));
        dayTargetPage.daytargetTimeFragmentEndEditText.setOnTouchListener(DayTargetUtil.getOnTimeTouchListener(
                context,dayTargetPage.daytargetTimeFragmentStartEditText,dayTargetPage.daytargetTimeFragmentEndEditText, false));

    }
    // 将页面中用户输入的数据存到数据库中
    // 如果必要项没有输入则返回false，否则返回true
    private boolean convertDayTargetMessage() throws ParseException{
        // 判断必要项是否有空的
        if(dayTargetPage.daytargetDayNameEditText.getText().toString().equals("")
                || dayTargetPage.daytargetFrequcecyEditText.getText().toString().equals("")
                || dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().equals("")
                || dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().equals("")
                || dayTargetPage.daytargetPlanCountsEditText.getText().toString().equals(""))
            return false;

        // 将target添加页面中用户输入的信息存到数据库
        MainExpandableListDBServer dbServer = new MainExpandableListDBServer(this.context);
        dbServer.addDayTarget(dayTargetPage);
        return true;

    }

}
