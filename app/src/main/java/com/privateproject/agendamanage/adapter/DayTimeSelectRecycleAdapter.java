package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.DayTimeSelectActivity;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.databinding.ItemDaytimeAddLayoutBinding;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.viewHolder.DayTimeSelectViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DayTimeSelectRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ODD = 0;
    private static final int TYPE_EVEN = 1;

    //数据是否改变
    public static boolean isChange = false;

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    private Context context;
    private DayTimeFragmentDao dao;
    private List<String> datas;
    private DayTimeSelectAddServer selectAddServer;
    private DayTimeSelectRecycleAdapter adapter;
    public void setAdapter(DayTimeSelectRecycleAdapter adapter) {
        this.adapter = adapter;
    }

    public DayTimeSelectRecycleAdapter(Context context) {
        this.context = context;
        this.dao = new DayTimeFragmentDao(context);
        List<DayTimeFragment> timeList = dao.selectAll();
        selectAddServer = new DayTimeSelectAddServer(context);
        this.datas = new ArrayList<String>();
        for (int i = 0; i < timeList.size(); i++) {
            this.datas.add(timeList.get(i).getTimePoint());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //如果为偶数，则加载ViewType为TextView的ViewHolder
        if (viewType == TYPE_ODD)
            return new DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.item_daytime_select_textview, parent, false));
            //如果为奇数，则加载ViewType为ConstrainLayout的ViewHolder
        else
            return new DayTimeSelectViewHolder.DayTimeSelectLayoutRecycleViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.item_daytime_select_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //偶数个显示时间点
        if (holder instanceof DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder) {
            ((DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder) holder).RecycleTextView.setText(datas.get(position / 2));
            //奇数个显示时间段，并设置页面监听器
        } else {
            //获取开始时间
            Time beginTime = Time.parseTime(datas.get(position / 2));
            //获取结束时间
            Time endTime = Time.parseTime(datas.get((position + 1) / 2));
            //viewHolder初始化
            DayTimeSelectViewHolder.DayTimeSelectLayoutRecycleViewHolder viewHolder = (DayTimeSelectViewHolder.DayTimeSelectLayoutRecycleViewHolder) holder;
            //结束时间减去开始时间，在RecycleLayoutTextView中显示
            viewHolder.RecycleLayoutTextView.setText((beginTime.sub(endTime)).getString());
            //点击时，弹出设置时间段的弹窗
            viewHolder.RecycleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加载编辑时间段的弹窗
                    ItemDaytimeAddLayoutBinding addTimePageXml = ItemDaytimeAddLayoutBinding.inflate(LayoutInflater.from(context));
                    //为EditText设置初始值
                    addTimePageXml.daytimeAddBegintimeEdit.setText(datas.get(position / 2));
                    addTimePageXml.daytimeAddEndtimeEdit.setText(datas.get((position + 1) / 2));
                    //设置选择时间段界面的组件监听器(时间选择器)
                    TimeUtil.setTimeStartToEnd(context, addTimePageXml.daytimeAddBegintimeEdit, addTimePageXml.daytimeAddEndtimeEdit, false);
                    ComponentUtil.EditTextEnable(false, addTimePageXml.daytimeAddBegintimeEdit);
                    ComponentUtil.EditTextEnable(false, addTimePageXml.daytimeAddEndtimeEdit);
                    //设置时间段设置的选择框
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("设置时间段")
                            .setView(addTimePageXml.daytimeAddContainer)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] times = {addTimePageXml.daytimeAddBegintimeEdit.getText().toString(), addTimePageXml.daytimeAddEndtimeEdit.getText().toString()};
                                    //判断选择的时间段与已有的时间段是否冲突
                                    if (selectAddServer.AppearConflict(times,datas)) {
                                        //冲突出现时弹出的提示框
                                        selectAddServer.ConflictAlerDialog(times,datas,dao,adapter);
                                    } else {
                                        selectAddServer.updateTimeList(times,datas,dao);
                                        //数据改变，刷新页面
                                        refresh();
                                    }
                                }
                            })
                            .create().show();
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        //返回Item的数量
        return datas.size() * 2 - 1;
    }

    @Override
    public int getItemViewType(int position) {
        //根据奇偶区分ItemView类型
        if (position % 2 == 0)
            return TYPE_ODD;
        else return TYPE_EVEN;
    }

    public void refresh(){
        dao.clearTable();
        for (int i = 0; i < this.datas.size(); i++) {
            dao.addDayTimeFragment(new DayTimeFragment(i, this.datas.get(i)));
        }
        isChange = true;
        adapter.notifyDataSetChanged();
    }

}