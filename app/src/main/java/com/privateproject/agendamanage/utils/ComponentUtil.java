package com.privateproject.agendamanage.utils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ComponentUtil {

    // 设置是否可编辑
    public static void EditTextEnable(boolean enable, EditText editText){
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setLongClickable(enable);
    }
    // 设置Button获取焦点
    public static void ButtonRequestFocus(ImageView button) {
        button.setFocusableInTouchMode(true);
        button.setFocusable(true);
        button.requestFocus();
    }

}
