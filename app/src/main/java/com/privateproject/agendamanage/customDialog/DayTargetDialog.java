package com.privateproject.agendamanage.customDialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.databinding.ItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.server.MainExpandableListDBServer;
import com.privateproject.agendamanage.utils.NumberUtils;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.util.Calendar;

public class DayTargetDialog extends Dialog implements View.OnClickListener {

    public TextView dayTargetTitle;
    public RaiflatButton cancelBtn, confirmBtn;
    public String title, cancel, confirm;
    public EditText dayTargetDayName;
    public EditText dayTargetDayDecoration;
  /*  public EditText dayTargetPlanCounts;
    public EditText dayTargetFrequcecy;
    public TextView dayTargetTimeFragmentStart;
    public TextView dayTargetTimeFragmentEnd;*/
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public DayTargetDialog(Context context) {
        super(context);
    }

    public DayTargetDialog(Context context, View layout, int style) {
        super(context, style);
    }

    public DayTargetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    public DayTargetDialog setDayTargetTitle(String title) {
        this.title = title;
        return this;
    }

    public DayTargetDialog setCancelBtn(String cancel, IOnCancelListener listener) {
        this.cancel = cancel;
        this.cancelListener = listener;
        return this;
    }

    public DayTargetDialog setConfirmBtn(String confirm, IOnConfirmListener listener) {
        this.confirm = confirm;
        this.confirmListener = listener;
        return this;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.item_main_adddaytarget);
        //设置宽度
        WindowManager manager = getWindow().getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point size = new Point();
        display.getSize(size);
        params.width = (int) (size.x * 0.8);//设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(params);


        dayTargetTitle=findViewById(R.id.daytarget_title_textView);
        cancelBtn=findViewById(R.id.daytarget_cancel_textView);
        confirmBtn=findViewById(R.id.daytarget_confirm_textView);
        dayTargetDayName=findViewById(R.id.daytarget_day_name_editText);
        dayTargetDayDecoration=findViewById(R.id.daytarget_day_decoration_editText);
      /*  dayTargetPlanCounts=findViewById(R.id.daytarget_planCounts_editText);
        dayTargetFrequcecy=findViewById(R.id.daytarget_frequcecy_editText);
        dayTargetTimeFragmentStart=findViewById(R.id.daytarget_timeFragmentStart_editText);
        dayTargetTimeFragmentEnd=findViewById(R.id.daytarget_timeFragmentEnd_editText);*/

        //给控件填内容
        if (!TextUtils.isEmpty(title)) {
            dayTargetTitle.setText(title);
        }
        if (!TextUtils.isEmpty(cancel)) {
            cancelBtn.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            confirmBtn.setText(confirm);
        }
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

       /* // target添加弹出框的“名称”不能为空，当焦点发生变化时触发监听器
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
        });*/

      /*  // 计划完成次数，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
        dayTargetPage.daytargetPlanCountsEditText.addTextChangedListener(getTextWatcher(dayTargetPage.daytargetPlanCountsEditText));
        dayTargetPage.daytargetPlanCountsEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(dayTargetPage.daytargetPlanCountsEditText));

        // 频率，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
        dayTargetPage.daytargetFrequcecyEditText.addTextChangedListener(getTextWatcher(dayTargetPage.daytargetFrequcecyEditText));
        dayTargetPage.daytargetFrequcecyEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(dayTargetPage.daytargetFrequcecyEditText));

        // 开始时间和结束时间，点击时弹出选择时间的提示框
        dayTargetPage.daytargetTimeFragmentStartEditText.setOnTouchListener(getOnTimeTouchListener(dayTargetPage.daytargetTimeFragmentStartEditText, true));
        dayTargetPage.daytargetTimeFragmentEndEditText.setOnTouchListener(getOnTimeTouchListener(dayTargetPage.daytargetTimeFragmentEndEditText, false));
*/
    }

    //执行监听器事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.daytarget_cancel_textView:
                if (cancelListener != null)
                    cancelListener.OnCancel(this);
                break;
            case R.id.daytarget_confirm_textView:
                if (confirmListener != null)
                    confirmListener.OnConfirm(this);
                break;
        }
    }

    //监听器接口
    public interface IOnCancelListener {
        void OnCancel(DayTargetDialog dialog);
    }

    public interface IOnConfirmListener {
        void OnConfirm(DayTargetDialog dialog);
    }


    // 返回OnTouchListener，触摸时弹出时间选择框，选择一天的某个时间点
   /* private View.OnTouchListener getOnTimeTouchListener(EditText editText, boolean isStart) {
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
                            if (isStart) {
                                // 如果“结束时间”已经选择过了，则“开始时间”不能晚于“结束时间”
                                if (!dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().equals("")) {
                                    // 获取“结束时间”，并用“：”切割，第一部分是小时，第二部分是分钟
                                    String[] temp = dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().split(":");
                                    int endHour = Integer.parseInt(temp[0]);
                                    int endMinute = Integer.parseInt(temp[1]);

                                    // 1.开始时间的小时数不能大于结束时间的小时数
                                    // 2.开始时间的小时数等于结束时间的小时数时，开始时间的分钟数不能大于结束时间的分钟数
                                    if (hourOfDay > endHour || (hourOfDay == endHour && minute >= endMinute)) {
                                        ToastUtil.newToast(context, "开始时间必须早于结束时间");
                                        return;
                                    }
                                }
                                editText.setText(formatTimeText(hourOfDay, minute));
                            } else {
                                // 当前点击的是“结束时间”
                                if (!dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().equals("")) {
                                    String[] temp = dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().split(":");
                                    int startHour = Integer.parseInt(temp[0]);
                                    int startMinute = Integer.parseInt(temp[1]);
                                    if (hourOfDay < startHour || (hourOfDay == startHour && minute <= startMinute)) {
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
        if (hourOfDay == 0) {
            temp += "00";
            // 如果小时数为0~9，则前面添加一个0
        } else if (hourOfDay <= 9) {
            temp += "0" + hourOfDay;
        } else {
            temp += hourOfDay;
        }
        temp += ":";
        // 如果分钟数为0，则分钟数为“00”
        if (minute == 0) {
            temp += "00";
            // 如果分钟数为0~9，则前面添加一个0
        } else if (minute <= 9) {
            temp += "0" + minute;
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
                if (temp.equals("0")) {
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

    // 去除开头的0，只允许一位小数
    private TextWatcher getTextWatcher(EditText editText) {
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

    // 将页面中用户输入的数据存到数据库中
    // 如果必要项没有输入则返回false，否则返回true
    public boolean convertDayTargetMessage() throws ParseException {
        // 判断必要项是否有空的
        if (dayTargetPage.daytargetDayNameEditText.getText().toString().equals("")
                || dayTargetPage.daytargetFrequcecyEditText.getText().toString().equals("")
                || dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().equals("")
                || dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().equals("")
                || dayTargetPage.daytargetPlanCountsEditText.getText().toString().equals(""))
            return false;

        // 将target添加页面中用户输入的信息存到数据库
        MainExpandableListDBServer dbServer = new MainExpandableListDBServer(this.context);
        dbServer.addDayTarget(dayTargetPage);
        return true;

    }*/

}
