package com.privateproject.agendamanage.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
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

    public static void setOnDateTouchListener(Context context, EditText editText, boolean isShowCurrent) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 点击时弹出日期选择框
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calendar = Calendar.getInstance();
                    // 弹出框选择日期时从已经设定的时间开始
                    if (isShowCurrent) {
                        if (!editText.getText().toString().equals("")) {
                            String[] tmp = editText.getText().toString().split("-");
                            calendar.set(Integer.parseInt(tmp[0]),
                                    Integer.parseInt(tmp[1])-1,
                                    Integer.parseInt(tmp[2]));
                        }
                    }
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String dateStr = year+"-"+(month+1)+"-"+dayOfMonth;
                            editText.setText(dateStr);
                        }
                    }, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });
    }

    public static void setDateStartToEnd(Context context, EditText startDateEditText, EditText endDateEditText, boolean isShowCurrent) {
        ComponentUtil.EditTextEnable(false, startDateEditText);
        ComponentUtil.EditTextEnable(false, endDateEditText);
        startDateEditText.setOnTouchListener(getOnDateTouchListener(context, startDateEditText, endDateEditText, true, isShowCurrent));
        endDateEditText.setOnTouchListener(getOnDateTouchListener(context, startDateEditText, endDateEditText, false, isShowCurrent));
    }
    // 返回OnTouchListener，触摸时弹出时间选择框，选择一天的某个时间点
    private static View.OnTouchListener getOnDateTouchListener(Context context, EditText startEditText, EditText endEditText, boolean isStart, boolean isShowCurrent) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 点击时弹出日期选择框
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calendar = Calendar.getInstance();
                    // 弹出框选择日期时从已经设定的时间开始
                    if (isShowCurrent) {
                        if (isStart && !startEditText.getText().toString().equals("")) {
                            String[] tmp = startEditText.getText().toString().split("-");
                            calendar.set(Integer.parseInt(tmp[0]),
                                    Integer.parseInt(tmp[1])-1,
                                    Integer.parseInt(tmp[2]));
                        } else if (!isStart && !endEditText.getText().toString().equals("")){
                            String[] tmp = endEditText.getText().toString().split("-");
                            calendar.set(Integer.parseInt(tmp[0]),
                                    Integer.parseInt(tmp[1])-1,
                                    Integer.parseInt(tmp[2]));
                        }
                    }
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String dateStr = year+"-"+(month+1)+"-"+dayOfMonth;
                            try {
                                Date date = sdf.parse(dateStr);
                                if (isStart) {
                                // 点击开始日期时
                                    // 如果已经选择了结束日期
                                    if (!endEditText.getText().toString().equals("")) {
                                        Date endDate = sdf.parse(endEditText.getText().toString());
                                        // 当开始日期晚于结束日期时提示
                                        if (date.after(endDate)) {
                                            ToastUtil.newToast(context, "开始日期不能晚于结束日期");
                                            return;
                                        }
                                    }
                                    startEditText.setText(dateStr);
                                } else {
                                // 点击结束日期时
                                    // 如果已经选择了开始日期
                                    if (!startEditText.getText().toString().equals("")) {
                                        Date startDate = sdf.parse(startEditText.getText().toString());
                                        // 当结束日期早于开始日期时提示
                                        if (date.before(startDate)) {
                                            ToastUtil.newToast(context, "结束日期不能早于开始日期");
                                            return;
                                        }
                                    }
                                    endEditText.setText(dateStr);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
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

    // 计算end对应的日期距离start对应的日期，如1号到3号有2天
    public static int subDate(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return (int)(diff/(1000*60*60*24));
    }

    public static String getDate(Date date) {
        return sdf.format(date);
    }

    // 在当前日期的基础上偏移n天（正数表示未来的几天，负数表示过去的几天）
    public static Date getOffCurrentDate(int n) {
        Date today = new Date();//获取今天的日期
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, n);
        Date result= c.getTime();//这是明天
        return result;
    }

    // 在baseDate的基础上偏移n天（正数表示未来的几天，负数表示过去的几天）
    public static Date getOffDate(Date baseDate, int off) {
        Calendar c = Calendar.getInstance();
        c.setTime(baseDate);
        c.add(Calendar.DAY_OF_MONTH, off);
        return c.getTime();
    }
}
