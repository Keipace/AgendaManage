package com.privateproject.agendamanage.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class ToastUtil {
    // 使用newToast方法来代替Toast.makeText，处理Toast显示的延迟
    private static ArrayList<Toast> toastList = new ArrayList<Toast>();
    public static void newToast(Context context, String content) {
        // 取消所有待显示的Toast
        cancelAll();
        // 添加要显示的Toast并显示出来
        Toast toast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        toastList.add(toast);
        toast.show();
    }
    // 循环取消待显示的Toast
    public static void cancelAll() {
        if (!toastList.isEmpty()){
            for (Toast t : toastList) {
                t.cancel();
            }
            toastList.clear();
        }
    }
}