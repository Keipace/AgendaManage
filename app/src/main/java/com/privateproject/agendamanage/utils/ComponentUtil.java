package com.privateproject.agendamanage.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class ComponentUtil {

    // 设置是否可编辑
    public static void EditTextEnable(boolean enable, EditText editText){
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setLongClickable(enable);
    }


}
