package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.DayTargetInfoActivity;
import com.privateproject.agendamanage.activity.TargetInfoActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.utils.NumberUtils;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.privateproject.agendamanage.viewHolder.MainExpandableListViewHolder;

import java.text.ParseException;
import java.util.Calendar;

public class MainExpandableListLoadViewServer {
    /*参数相关*/
    private Context context;

    public MainExpandableListLoadViewServer(Context context) {
        // 初始化需要的参数
        this.context = context;
    }

    // 加载group的页面到类中
    // 方法返回[convertView, headHolder]
    public Object[] getGroupItem(View convertView) {
        MainExpandableListViewHolder.HeadHolder headHolder;
        // 第一次渲染时convertView为空
        if (convertView == null) {
            // 加载每个group项的页面
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_header, null);
            // 将页面中的每个控件赋值到holder类中（把页面包装成holder类）
            headHolder = new MainExpandableListViewHolder.HeadHolder(convertView);
            // 将holder类存储到convertView中
            convertView.setTag(headHolder);
        // 之后渲染时convertView不为空，直接从convertView中取holder类
        } else {
            headHolder= (MainExpandableListViewHolder.HeadHolder) convertView.getTag();
        }
        Object[] result = new Object[2];
        result[0] = convertView;
        result[1] = headHolder;
        return result;
    }

    // 加载目标区的child的页面到类中
    // 方法返回[convertView, headHolder]
    private boolean isTargetFirst = true; //解决第一次展开时判断convertView==null失误
    public Object[] getTargetChildItem(View convertView) {
        MainExpandableListViewHolder.ContentHolder contentHolder;
        // 第一次渲染时convertView为空
        if (convertView == null || isTargetFirst) {
            isTargetFirst = false;
            // 加载每个child项的页面
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content_target, null);
            // 将页面中的每个控件赋值到holder类中（把页面包装成holder类）
            contentHolder = new MainExpandableListViewHolder.ContentHolder(convertView);
            // 将holder类存储到convertView中,convertView中存着目标区的child和打卡区的child，因此用 key，value 格式的方法
            convertView.setTag(R.id.tag_first, contentHolder);
        // 之后渲染时convertView不为空，直接从convertView中取holder类
        } else {
            contentHolder= (MainExpandableListViewHolder.ContentHolder) convertView.getTag(R.id.tag_first);
        }
        Object[] result = new Object[2];
        result[0] = convertView;
        result[1] = contentHolder;
        return result;
    }

    // 加载打卡区的child的页面到类中
    // 方法返回[convertView, contentHolderDayTarget]
    private boolean isDayTargetFirst = true; //解决第一次展开时判断convertView==null失误
    public Object[] getDayTargetChildItem(View convertView) {
        MainExpandableListViewHolder.ContentHolderDayTarget contentHolderDayTarget;
        // 第一次渲染时convertView为空
        if(convertView==null || isDayTargetFirst) {
            isDayTargetFirst = false;
            // 加载每个child项的页面
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content_daytarget, null);
            // 将页面中的每个控件赋值到holder类中（把页面包装成holder类）
            contentHolderDayTarget = new MainExpandableListViewHolder.ContentHolderDayTarget(convertView);
            // 将holder类存储到convertView中,convertView中存着目标区的child和打卡区的child，因此用 key，value 格式的方法
            convertView.setTag(R.id.tag_second, contentHolderDayTarget);
        // 之后渲染时convertView不为空，直接从convertView中取holder类
        } else {
            contentHolderDayTarget = (MainExpandableListViewHolder.ContentHolderDayTarget)convertView.getTag(R.id.tag_second);
        }
        Object[] result = new Object[2];
        result[0] = convertView;
        result[1] = contentHolderDayTarget;
        return result;
    }

}
