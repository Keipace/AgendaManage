package com.privateproject.agendamanage.utils;

import android.view.View;
import android.widget.EditText;

public class ComponentUtil {

    // 设置是否可编辑
    public static void EditTextEnable(boolean enable, EditText editText){
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setLongClickable(enable);
    }
    // 设置Button获取焦点
    public static void requestFocus(View view) {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
    }

}
