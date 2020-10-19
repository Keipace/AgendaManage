package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.utils.NumberUtils;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
                tempBtn.setFocusable(true);
                tempBtn.setFocusableInTouchMode(true);
                tempBtn.requestFocus();
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
        targetPage.targetTimePlanOverEditText.setOnTouchListener(getOnTouchListener(targetPage.targetTimePlanOverEditText, 1));
        targetPage.targetTimeDeadLineEditText.setOnTouchListener(getOnTouchListener(targetPage.targetTimeDeadLineEditText, 2));
        targetPage.targetTimePreDoEditText.setOnTouchListener(getOnTouchListener(targetPage.targetTimePreDoEditText, 3));
        // target添加弹出框的“名称”不能为空，当焦点发生变化时触发监听器
        targetPage.targetNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                targetPage.targetNameEditText.setText(StringUtils.moveSpaceString(targetPage.targetNameEditText.getText().toString()));
            }
        });
        // target添加弹出框的“描述”不能为空，当焦点发生变化时触发监听器
        targetPage.targetDecorationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    targetPage.targetDecorationEditText.setText(StringUtils.moveSpaceString(targetPage.targetDecorationEditText.getText().toString()));
                }
            }
        });
        // 设置“所需小时数”的格式，当文本改变时检查开头的0和小数位数
        targetPage.targetTimeNeedEditText.addTextChangedListener(getTextWatcher(targetPage.targetTimeNeedEditText));
        // 检测是否为0、最后一位是否是小数点
        targetPage.targetTimeNeedEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(targetPage.targetTimeNeedEditText));

    }

    // 去除开头的0，只允许一位小数
    private TextWatcher getTextWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 去除开头的几个0（但不处理0或0.)
                String temp = NumberUtils.moveStartZero(s.toString());
                // 只允许最后有一位小数
                temp = NumberUtils.onlyOneDecimal(temp);
                // 当处理后的文本与用户输入的文本不同时，则修改编辑框的文本
                if(!temp.equals(s.toString())) {
                    editText.setText(temp);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    // 处理只输入一个0和结尾是小数点的问题
    public View.OnFocusChangeListener getNotZeroFocusChangeListener(EditText editText) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 如果文本只有一个“0”，则直接替换成空文本
                String temp = editText.getText().toString();
                if(temp.equals("0")) {
                    editText.setText("");
                    return;
                }
                // 如果去除最后一个小数点后文本变化了（即最后一位是小数点），则改变输入的文本
                String result = NumberUtils.deleteEndDecimal(temp);
                if(!result.equals(temp)) {
                    editText.setText(result);
                }
            }
        };
    }

    private View.OnTouchListener getOnTouchListener(EditText editText, int position){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //获取当前日期
                    Calendar current = Calendar.getInstance();
                    //日期选择器弹出框
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // 将选择的日期转换成Calendar类型
                            Calendar temp = Calendar.getInstance();
                            temp.set(year, monthOfYear, dayOfMonth);
                            // 三个日期的选择都不能早于当前日期
                            /*temp早于current，返回负数；temp等于current，返回0；temp晚于current，返回正数*/
                            if(temp.compareTo(current)<0) {
                                ToastUtil.newToast(context, "时间不应早于当前时间");
                                return;
                            }
                            String input = year + "-" + (monthOfYear+1 )+ "-" + dayOfMonth;
                            // preDoDate早于planOverDate早于deadLineDate，否则选择的日期无效
                            if (compareTime(position, year, monthOfYear, dayOfMonth, input))
                                editText.setText(input);
                        }
                    }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
                    // 显示出来日历选择器弹出框
                    datePickerDialog.show();
                    return true;
                }
                return false;
            }
        };
    }

    public boolean compareTime(int position, int year, int monthOfYear, int dayOfMonth, String input) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 用户选择的日期转换成string类型
        // 从target添加框页面获取期待完成时间、最晚完成时间、预实施时间
        String deadLineDate = targetPage.targetTimeDeadLineEditText.getText().toString();
        String preDoDate = targetPage.targetTimePreDoEditText.getText().toString();
        String planOverDate = targetPage.targetTimePlanOverEditText.getText().toString();
        try {
            /*通过传递的position来判断是哪个日期
             * 1：planOverDate
             * 2：deadLineDate
             * 3：preDoDate*/
            // preDoDate早于planOverDate早于deadLineDate
            if(position == 1) { // 当前选择的日期为planOverDate
                if(!deadLineDate.equals("") && sdf.parse(input).getTime()>sdf.parse(deadLineDate).getTime()) {
                    ToastUtil.newToast(context, "期待完成时间不应晚于最晚完成时间");
                    return false;
                }
                if(!preDoDate.equals("") && sdf.parse(input).getTime()<sdf.parse(preDoDate).getTime()) {
                    ToastUtil.newToast(context, "期待完成时间不应早于预实施时间");
                    return false;
                }
            } else if(position == 2) { // 当前选择的日期是deadLineDate
                if(!planOverDate.equals("") && sdf.parse(input).getTime()<sdf.parse(planOverDate).getTime()) {
                    ToastUtil.newToast(context, "最晚完成时间不应早于期待完成时间");
                    return false;
                }
                if(!preDoDate.equals("") && sdf.parse(input).getTime()<sdf.parse(preDoDate).getTime()) {
                    ToastUtil.newToast(context, "最晚完成时间不应早于预实施时间");
                    return false;
                }
            } else if(position == 3) { // 当前选择的日期是preDoDate
                if(!planOverDate.equals("") && sdf.parse(input).getTime()>sdf.parse(planOverDate).getTime()) {
                    ToastUtil.newToast(context, "预实施时间不应晚于期待完成时间");
                    return false;
                }
                if(!deadLineDate.equals("") && sdf.parse(input).getTime()>sdf.parse(deadLineDate).getTime()) {
                    ToastUtil.newToast(context, "预实施时间不应晚于最晚完成时间");
                    return false;
                }
            }
        } catch (ParseException e) { //string转换为Date类型时的异常
            ToastUtil.newToast(context, "日期输入错误");
            return false;
        }
        return true;
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
