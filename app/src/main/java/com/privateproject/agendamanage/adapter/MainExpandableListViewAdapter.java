package com.privateproject.agendamanage.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.server.MainExpandableAdapterServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainExpandableListViewAdapter extends BaseExpandableListAdapter {
    private final String[] groups;
    private MainExpandableAdapterServer server;
    private Context context;

    public MainExpandableListViewAdapter(Context context, ExpandableListView expandableListView) {
        this.context = context;
        this.groups = context.getResources().getStringArray(R.array.main_group);
        server = new MainExpandableAdapterServer(context, expandableListView);
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition==0)
            return server.getTargetsSize();
        else if(groupPosition==1)
            return server.getDayTargetsSize();
        else
            throw new IndexOutOfBoundsException("groupPosition异常");
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition==0)
            return server.getTarget(childPosition);
        if(groupPosition==1)
            return server.getDayTarget(childPosition);
        else
            throw new IndexOutOfBoundsException("groupPosition异常");
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = server.getGroupItem(convertView);
        server.setGroupItemContent(groupPosition, groups);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        try {
            if(groupPosition == 0) {
                convertView = server.getTargetChildItem(convertView);
                server.setTargetChildItemContent(childPosition);
            } else if(groupPosition == 1) {
                convertView = server.getDayTargetChildItem(convertView);
                server.setDayTargetChildItemContent(childPosition);
            }
            return convertView;
        } catch (ClassCastException e) {
            Toast.makeText(context, "类型转换错误", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
