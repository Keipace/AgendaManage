package com.privateproject.agendamanage.utils;

import android.text.InputType;
import android.widget.EditText;

public class ComponentUtil {

    // 设置是否可编辑
    public static void EditTextEnable(boolean enable, EditText editText){
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setLongClickable(enable);
        editText.setInputType(enable? InputType.TYPE_CLASS_TEXT:InputType.TYPE_NULL);
    }

}
