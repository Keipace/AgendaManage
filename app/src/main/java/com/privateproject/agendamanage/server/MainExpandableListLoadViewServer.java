package com.privateproject.agendamanage.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.viewHolder.MainExpandableListViewHolder;

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
    public Object[] getTargetChildItem(View convertView) {
        MainExpandableListViewHolder.ContentHolder contentHolder;
        // 第一次渲染时convertView为空
        if (convertView == null || convertView.getTag(R.id.tag_first).equals("dayTarget")) {
            // 加载每个child项的页面
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content_target, null);
            // 将页面中的每个控件赋值到holder类中（把页面包装成holder类）
            contentHolder = new MainExpandableListViewHolder.ContentHolder(convertView);
            // 将holder类存储到convertView中,convertView中存着目标区的child和打卡区的child，因此用 key，value 格式的方法
            convertView.setTag(R.id.tag_first, "target");
            convertView.setTag(R.id.tag_second, contentHolder);
        // 之后渲染时convertView不为空，直接从convertView中取holder类
        } else {
            contentHolder= (MainExpandableListViewHolder.ContentHolder) convertView.getTag(R.id.tag_second);
        }
        Object[] result = new Object[2];
        result[0] = convertView;
        result[1] = contentHolder;
        return result;
    }

    // 加载打卡区的child的页面到类中
    // 方法返回[convertView, contentHolderDayTarget]
    public Object[] getDayTargetChildItem(View convertView) {
        MainExpandableListViewHolder.ContentHolderDayTarget contentHolderDayTarget;
        // 第一次渲染时convertView为空
        if(convertView==null || convertView.getTag(R.id.tag_first).equals("target")) {
            // 加载每个child项的页面
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content_daytarget, null);
            // 将页面中的每个控件赋值到holder类中（把页面包装成holder类）
            contentHolderDayTarget = new MainExpandableListViewHolder.ContentHolderDayTarget(convertView);
            // 将holder类存储到convertView中,convertView中存着目标区的child和打卡区的child，因此用 key，value 格式的方法
            convertView.setTag(R.id.tag_first, "dayTarget");
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
