package com.privateproject.agendamanage.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class DayTargetUtil {
    // 返回OnTouchListener，触摸时弹出时间选择框，选择一天的某个时间点
    public static View.OnTouchListener getOnTimeTouchListener(Context context, EditText startEditText, EditText endEditText, boolean isStart) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 获取当前时间
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // 当前点击的是“开始时间”
                            if(isStart) {
                                // 如果“结束时间”已经选择过了，则“开始时间”不能晚于“结束时间”
                                if (!endEditText.getText().toString().equals("")) {
                                    // 获取“结束时间”，并用“：”切割，第一部分是小时，第二部分是分钟
                                    String[] temp = endEditText.getText().toString().split(":");
                                    int endHour = Integer.parseInt(temp[0]);
                                    int endMinute = Integer.parseInt(temp[1]);
                                    /*
                                     * 1.开始时间的小时数不能大于结束时间的小时数
                                     * 2.开始时间的小时数等于结束时间的小时数时，开始时间的分钟数不能大于结束时间的分钟数*/
                                    if(hourOfDay>endHour || (hourOfDay==endHour&&minute>=endMinute) ) {
                                        ToastUtil.newToast(context, "开始时间必须早于结束时间");
                                        return;
                                    }
                                }
                                startEditText.setText(formatTimeText(hourOfDay, minute));
                            } else {
                                // 当前点击的是“结束时间”
                                if(!startEditText.getText().toString().equals("")) {
                                    String[] temp = startEditText.getText().toString().split(":");
                                    int startHour = Integer.parseInt(temp[0]);
                                    int startMinute = Integer.parseInt(temp[1]);
                                    if(hourOfDay<startHour || (hourOfDay==startHour&&minute<=startMinute)) {
                                        ToastUtil.newToast(context, "结束时间必须晚于开始时间");
                                        return;
                                    }
                                }
                                // 将int类型的小时数和分钟数转换为 hh:mm 格式
                                endEditText.setText(formatTimeText(hourOfDay, minute));
                            }
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    // 显示时间选择器
                    timePickerDialog.show();
                    return true;
                }
                return false;
            }
        };
    }

    // 将int类型的小时数和分钟数转换为 hh:mm 格式
    private static String formatTimeText(int hourOfDay, int minute) {
        String temp = "";
        // 如果小时数为0，则小时数为“00”
        if(hourOfDay==0) {
            temp += "00";
            // 如果小时数为0~9，则前面添加一个0
        } else if(hourOfDay<=9) {
            temp += "0"+hourOfDay;
        } else {
            temp += hourOfDay;
        }
        temp += ":";
        // 如果分钟数为0，则分钟数为“00”
        if(minute==0) {
            temp += "00";
            // 如果分钟数为0~9，则前面添加一个0
        } else if(minute<=9) {
            temp += "0"+minute;
        } else {
            temp += minute;
        }
        return temp;
    }
    // 处理只输入一个0和结尾是小数点的问题
    public static View.OnFocusChangeListener getNotZeroFocusChangeListener(EditText editText) {
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

    // 去除开头的0，只允许一位小数
    public static TextWatcher getTextWatcher(EditText editText) {
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


}
