package com.privateproject.agendamanage.utils;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static void setTimeStartToEnd(Context context, EditText startTimeEditText, EditText endTimeEditText, boolean isShowCurrent) {
        startTimeEditText.setOnTouchListener(getOnTimeTouchListener(context, startTimeEditText, endTimeEditText, true, isShowCurrent));
        endTimeEditText.setOnTouchListener(getOnTimeTouchListener(context, startTimeEditText, endTimeEditText, false, isShowCurrent));
    }
    // 返回OnTouchListener，触摸时弹出时间选择框，选择一天的某个时间点
    private static View.OnTouchListener getOnTimeTouchListener(Context context, EditText startEditText, EditText endEditText, boolean isStart, boolean isShowCurrent) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 获取当前时间
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Time time = new Time(hourOfDay,minute);
                            // 当前点击的是“开始时间”
                            if(isStart) {
                                // 如果“结束时间”已经选择过了，则“开始时间”不能晚于“结束时间”
                                if (!endEditText.getText().toString().equals("")) {
                                    Time endTime = Time.parseTime(endEditText.getText().toString());
                                    //开始时间不能大于结束时间
                                    if(!time.before(endTime)) {
                                        ToastUtil.newToast(context, "开始时间必须早于结束时间");
                                        return;
                                    }
                                }
                                startEditText.setText(time.toString());
                            } else {
                                // 当前点击的是“结束时间”
                                if(!startEditText.getText().toString().equals("")) {
                                    Time startTime = Time.parseTime(startEditText.getText().toString());
                                    if(!time.after(startTime)) {
                                        ToastUtil.newToast(context, "结束时间必须晚于开始时间");
                                        return;
                                    }
                                }
                                // 将int类型的小时数和分钟数转换为 hh:mm 格式
                                endEditText.setText(time.toString());
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

    /*
    * 比较两个时间的大小
    * 参数字符串为yyyy-MM-dd格式
    * 当first<second时返回-1；当first=second时返回0；当first>second时返回1；*/
    public static int compareDate(String first, String second) {
        Date firstDate = getDate(first);
        Date secondDate = getDate(second);
        return compareDate(firstDate, secondDate);
    }

    /*
     * 比较两个时间的大小
     * 当first<second时返回-1；当first=second时返回0；当first>second时返回1；*/
    public static int compareDate(Date first, Date second) {
        if (first.getTime() < second.getTime()) {
            return -1;
        } else if (first.getTime() > second.getTime()) {
            return 1;
        } else {
            return 0;
        }
    }

    // 将 yyyy-MM-dd 格式的字符串转换成Date类型的数据
    public static Date getDate(String date) {
        Date result;
        try {
             result = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    // 计算end对应的日期距离start对应的日期
    public static int subDate(Date start, Date end) {
        long diff = start.getTime() - end.getTime();
        return (int)(diff/(1000*60*60*24));
    }
}
