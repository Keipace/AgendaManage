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
                        ToastUtil.newToast(context, "请输入完成信息");
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
        dayTargetPage.daytargetPlanCountsEditText.addTextChangedListener(getTextWatcher(dayTargetPage.daytargetPlanCountsEditText));
        dayTargetPage.daytargetPlanCountsEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(dayTargetPage.daytargetPlanCountsEditText));

        // 频率，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
        dayTargetPage.daytargetFrequcecyEditText.addTextChangedListener(getTextWatcher(dayTargetPage.daytargetFrequcecyEditText));
        dayTargetPage.daytargetFrequcecyEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(dayTargetPage.daytargetFrequcecyEditText));

        // 开始时间和结束时间，点击时弹出选择时间的提示框
        dayTargetPage.daytargetTimeFragmentStartEditText.setOnTouchListener(getOnTimeTouchListener(dayTargetPage.daytargetTimeFragmentStartEditText, true));
        dayTargetPage.daytargetTimeFragmentEndEditText.setOnTouchListener(getOnTimeTouchListener(dayTargetPage.daytargetTimeFragmentEndEditText, false));

    }

    // 返回OnTouchListener，触摸时弹出时间选择框，选择一天的某个时间点
    private View.OnTouchListener getOnTimeTouchListener(EditText editText, boolean isStart) {
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
                                if (!dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().equals("")) {
                                    // 获取“结束时间”，并用“：”切割，第一部分是小时，第二部分是分钟
                                    String[] temp = dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().split(":");
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
                                editText.setText(formatTimeText(hourOfDay, minute));
                            } else {
                            // 当前点击的是“结束时间”
                                if(!dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().equals("")) {
                                    String[] temp = dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().split(":");
                                    int startHour = Integer.parseInt(temp[0]);
                                    int startMinute = Integer.parseInt(temp[1]);
                                    if(hourOfDay<startHour || (hourOfDay==startHour&&minute<=startMinute)) {
                                        ToastUtil.newToast(context, "结束时间必须晚于开始时间");
                                        return;
                                    }
                                }
                                // 将int类型的小时数和分钟数转换为 hh:mm 格式
                                editText.setText(formatTimeText(hourOfDay, minute));
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
    public String formatTimeText(int hourOfDay, int minute) {
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
