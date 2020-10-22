package com.privateproject.agendamanage.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import com.privateproject.agendamanage.activity.TargetInfoActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ComponentUtil {

    // 设置是否可编辑
    public static void EditTextEnable(boolean enable, EditText editText){
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setLongClickable(enable);
    }

    public static void ButtonRequestFocus(Button button) {
        button.setFocusableInTouchMode(true);
        button.setFocusable(true);
        button.requestFocus();
    }



}
