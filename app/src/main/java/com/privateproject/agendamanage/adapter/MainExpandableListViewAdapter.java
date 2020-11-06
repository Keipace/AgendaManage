package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.server.MainExpandableListFillDataServer;
import com.privateproject.agendamanage.server.MainExpandableListLoadViewServer;
import com.privateproject.agendamanage.server.MainExpandableListDBServer;
import com.privateproject.agendamanage.viewHolder.MainExpandableListViewHolder;

/*
* 1.查询数据库
* 2.根据回调方法加载每项的页面
* 3.根据回调方法填充每项的数据*/
public class MainExpandableListViewAdapter extends BaseExpandableListAdapter {
    /*1.数据相关*/
    // strings.xml文件中的group数据
    private final String[] groups;
    // 查询数据库的server
    private MainExpandableListDBServer dbServer;

    /*2.加载页面相关*/
    // 加载页面的server
    private MainExpandableListLoadViewServer loadViewServer;

    /*3.填充页面相关*/
    // 为页面填充数据
    private MainExpandableListFillDataServer fillDataServer;

    public MainExpandableListViewAdapter(Context context, ExpandableListView expandableListView) {
        // 初始化需要的参数
        this.dbServer = new MainExpandableListDBServer(context, expandableListView);
        this.fillDataServer = new MainExpandableListFillDataServer(context);
        this.loadViewServer = new MainExpandableListLoadViewServer(context);
        // 加载数据
        this.groups = context.getResources().getStringArray(R.array.main_group);
    }

    // 回调方法：获取group的数量
    @Override
    public int getGroupCount() {
        return groups.length;
    }

    // 回调方法：根据groupPosition返回children数量
    @Override
    public int getChildrenCount(int groupPosition) {
        // 目标区children数量
        if(groupPosition==0)
            return dbServer.getTargetSize();
        // 打卡区children数量
        else if(groupPosition==1)
            return dbServer.getDayTargetSize();
        else
            throw new IndexOutOfBoundsException("groupPosition异常");
    }

    // 回调方法：根据groupPosition显示group的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // 加载视图
        Object[] temp = loadViewServer.getGroupItem(convertView);
        convertView = (View)temp[0];
        // 根据position，填充对应的数据到视图上
        fillDataServer.setGroupItemContent(groupPosition, groups, (MainExpandableListViewHolder.HeadHolder)temp[1]);
        return convertView;
    }

    // 回调方法：根据groupPosition和childPosition显示child的视图到对应的group中
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // 目标区的child视图
        if(groupPosition == 0) {
            // 加载视图
            Object[] temp = loadViewServer.getTargetChildItem(convertView);
            convertView = (View)temp[0];
            // 根据position，填充对应数据到视图上
            fillDataServer.setTargetChildItemContent(childPosition, dbServer.getTargets(), (MainExpandableListViewHolder.ContentHolder)temp[1]);
        // 打卡区的child视图
        } else if(groupPosition == 1) {
            // 加载视图
            Object[] temp = loadViewServer.getDayTargetChildItem(convertView);
            convertView = (View)temp[0];
            // 根据position，填充对应数据到视图上
            fillDataServer.setDayTargetChildItemContent(childPosition, dbServer.getDayTargets(), (MainExpandableListViewHolder.ContentHolderDayTarget)temp[1]);
        }
        return convertView;
    }


    /*以下方法没有用到*/

    // （没用到）回调方法：根据groupPosition获取group数据
    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    // （没用到）回调方法：根据groupPosition和childPosition，获取children数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition==0)
            return dbServer.getTargets().get(childPosition);
        if(groupPosition==1)
            return dbServer.getDayTargets().get(childPosition);
        else
            throw new IndexOutOfBoundsException("groupPosition异常");
    }

    // （没用到）回调方法：根据groupPosition返回groupId
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // （没用到）回调方法：根据groupPosition和childPosition返回childId
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // （没用到）回调方法
    @Override
    public boolean hasStableIds() {
        return true;
    }

    // （没用到）回调方法
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
