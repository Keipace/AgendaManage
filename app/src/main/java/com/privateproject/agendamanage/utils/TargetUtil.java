package com.privateproject.agendamanage.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.privateproject.agendamanage.activity.TargetInfoActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TargetUtil {
    //设置preDo、deadLine、planOver的时间选择器
    public static void setPreDoPlanDeadLineTimeEditText(Context context, EditText planOverDate, EditText deadLineDate, EditText preDoDate) {
        planOverDate.setOnTouchListener(getOnTouchListener(context, planOverDate, deadLineDate, preDoDate, 1));
        deadLineDate.setOnTouchListener(getOnTouchListener(context, planOverDate, deadLineDate, preDoDate, 2));
        preDoDate.setOnTouchListener(getOnTouchListener(context, planOverDate, deadLineDate, preDoDate, 3));
    }

    private static View.OnTouchListener getOnTouchListener(Context context, EditText planOverDate, EditText deadLineDate, EditText preDoDate, int position) {
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
                            if (temp.compareTo(current) < 0) {
                                ToastUtil.newToast(context, "时间不应早于当前时间");
                                return;
                            }
                            String input = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            // preDoDate早于planOverDate早于deadLineDate，否则选择的日期无效
                            if (compareTime(context, position, planOverDate, deadLineDate, preDoDate, input)) {
                                if (position == 1) {
                                    planOverDate.setText(input);
                                } else if (position == 2) {
                                    deadLineDate.setText(input);
                                } else if (position == 3) {
                                    preDoDate.setText(input);
                                }
                            }
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

    public static View.OnTouchListener getOnTouchListener(Context context, EditText editText) {
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
                            if (temp.compareTo(current) < 0) {
                                ToastUtil.newToast(context, "时间不应早于当前时间");
                                return;
                            }
                            String input = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
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

    private static boolean compareTime(Context context, int position, EditText planOverDate, EditText deadLineDate, EditText preDoDate, String input) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 用户选择的日期转换成string类型
        // 从target添加框页面获取期待完成时间、最晚完成时间、预实施时间
        String deadLineDatestr = deadLineDate.getText().toString();
        String preDoDatestr = preDoDate.getText().toString();
        String planOverDatestr = planOverDate.getText().toString();
        try {
            /*通过传递的position来判断是哪个日期
             * 1：planOverDate
             * 2：deadLineDate
             * 3：preDoDate*/
            // preDoDate早于planOverDate早于deadLineDate
            if (position == 1) { // 当前选择的日期为planOverDate
                if (!deadLineDatestr.equals("") && sdf.parse(input).getTime() > sdf.parse(deadLineDatestr).getTime()) {
                    ToastUtil.newToast(context, "期待完成时间不应晚于最晚完成时间");
                    return false;
                }
                if (!preDoDatestr.equals("") && sdf.parse(input).getTime() < sdf.parse(preDoDatestr).getTime()) {
                    ToastUtil.newToast(context, "期待完成时间不应早于预实施时间");
                    return false;
                }
            } else if (position == 2) { // 当前选择的日期是deadLineDate
                if (!planOverDatestr.equals("") && sdf.parse(input).getTime() < sdf.parse(planOverDatestr).getTime()) {
                    ToastUtil.newToast(context, "最晚完成时间不应早于期待完成时间");
                    return false;
                }
                if (!preDoDatestr.equals("") && sdf.parse(input).getTime() < sdf.parse(preDoDatestr).getTime()) {
                    ToastUtil.newToast(context, "最晚完成时间不应早于预实施时间");
                    return false;
                }
            } else if (position == 3) { // 当前选择的日期是preDoDate
                if (!planOverDatestr.equals("") && sdf.parse(input).getTime() > sdf.parse(planOverDatestr).getTime()) {
                    ToastUtil.newToast(context, "预实施时间不应晚于期待完成时间");
                    return false;
                }
                if (!deadLineDatestr.equals("") && sdf.parse(input).getTime() > sdf.parse(deadLineDatestr).getTime()) {
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


    // 去除开头的0，只允许一位小数
    public static TextWatcher getTextWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 去除开头的几个0（但不处理0或0.)
                String temp = NumberUtils.moveStartZero(s.toString());
                // 只允许最后有一位小数
                temp = NumberUtils.onlyOneDecimal(temp);
                // 当处理后的文本与用户输入的文本不同时，则修改编辑框的文本
                if (!temp.equals(s.toString())) {
                    editText.setText(temp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    // 处理只输入一个0和结尾是小数点的问题
    public static View.OnFocusChangeListener getNotZeroFocusChangeListener(EditText editText) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 如果文本只有一个“0”，则直接替换成空文本
                String temp = editText.getText().toString();
                if (temp.equals("0") || temp.equals("0.0") || temp.equals("0.")) {
                    editText.setText("");
                    return;
                }
                // 如果去除最后一个小数点后文本变化了（即最后一位是小数点），则改变输入的文本
                String result = NumberUtils.deleteEndDecimal(temp);
                if (!result.equals(temp)) {
                    editText.setText(result);
                }
            }
        };
    }


    // target添加弹出框的“描述”不能为空，当焦点发生变化时触发监听器
    public static View.OnFocusChangeListener getDecorationOnFocusChangeListener(EditText decoration) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    decoration.setText(StringUtils.moveSpaceString(decoration.getText().toString()));
                }
            }
        };
    }


    // target添加弹出框的“名称”不能为空，当焦点发生变化时触发监听器
    public static View.OnFocusChangeListener getNameOnFocusChangeListener(EditText targetname) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                targetname.setText(StringUtils.moveSpaceString(targetname.getText().toString()));
            }
        };
    }

    //设置target页面输入框的约束
    public static void setTargetConstraint(EditText timeNeedEditText, EditText nameEditText, EditText decorationEditText) {
        // 设置“所需小时数”的格式，当文本改变时检查开头的0和小数位数
        timeNeedEditText.addTextChangedListener(getTextWatcher(timeNeedEditText));
        // 检测是否为0、最后一位是否是小数点
        timeNeedEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(timeNeedEditText));
        //// target添加弹出框的“名称”不能为空，当焦点发生变化时触发监听器
        nameEditText.setOnFocusChangeListener(getNameOnFocusChangeListener(nameEditText));
        // target添加弹出框的“描述”不能为空，当焦点发生变化时触发监听器
        decorationEditText.setOnFocusChangeListener(getDecorationOnFocusChangeListener(decorationEditText));

    }
}