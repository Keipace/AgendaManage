package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.databinding.ActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.viewHolder.DayTimeSelectViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DayTimeSelectRecycleAdapter extends RecyclerView.Adapter<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder> {
    private Context context;
    private DayTimeFragmentDao dao;
    private List<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder> items;
    private List<DayTimeFragment> dayTimeFragmentList;
    private ActivityDayTimeSelectBinding binding;
    private List<String> temp;
    //扇形图时间差和名称
    private List<Integer> timeLength;
    private List<String> timeName;
    private List<Boolean> timeIdentify;

    public DayTimeSelectRecycleAdapter(Context context,ActivityDayTimeSelectBinding binding) {
        this.context = context;
        this.dao = new DayTimeFragmentDao(context);
        this.dayTimeFragmentList = dao.selectAll();
        this.items = new ArrayList<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder>();
        this.binding = binding;
        this.temp = new ArrayList<String>();
        this.timeName=new ArrayList<String>();
        this.timeLength=new ArrayList<Integer>();
        this.timeIdentify=new ArrayList<Boolean>();
    }



    @NonNull
    @Override
    public DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载ViewType为TextView的ViewHolder
        return new DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder
                (LayoutInflater.from(context).inflate(R.layout.item_daytime_select_textview, parent, false));
    }

    private boolean isRestart = false;
    @Override
    public void onBindViewHolder(@NonNull DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder holder, int position) {
        if (isRestart) {
            this.items.clear();
            this.isRestart = false;
        }
        items.add(position, holder);
        //设置初试时间段
        holder.RecycleTextView.setText(dayTimeFragmentList.get(position).toString());
        //设置文本的点击监听器
        holder.RecycleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳出时间选择器，用户修改时间段
                DayTimeSelectAddServer.TimeSelectAlerDialog(context, dao, DayTimeSelectRecycleAdapter.this, position);
            }
        });
        holder.daytimeSelectContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                temp.clear();
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置不可选，CheckBox可见
                    items.get(i).RecycleTextView.setEnabled(false);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.VISIBLE);
                }
                //显示有关的组件
                isVisible(true);
                return true;
            }
        });
        //复选框监听器
        holder.daytimeSelectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //被选到，把下标放入temp中
                    temp.add(holder.RecycleTextView.getText().toString());
                }else {
                    //取消选择，把存入temp中对应的下标删除
                    temp.remove(holder.RecycleTextView.getText().toString());
                }
            }
        });
        //取消按钮监听器
        binding.daytimeSelectBackBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置可选，CheckBox不可见
                    items.get(i).daytimeSelectCheckBox.setChecked(false);
                    items.get(i).RecycleTextView.setEnabled(true);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.GONE);
                }
                //隐藏有关的组件
                isVisible(false);
                DayTimeSelectRecycleAdapter.this.temp.clear();
            }
        });
        //删除按钮监听器
        binding.daytimeSelectDeleteBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置可选，CheckBox不可见
                    items.get(i).RecycleTextView.setEnabled(true);
                    items.get(i).daytimeSelectCheckBox.setChecked(false);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.GONE);
                }
                isVisible(false);
                //删除dayTimeFragmentList中对应下标的数据
                for (int i = 0; i<temp.size(); i++){
                    for (int j = 0; j<dayTimeFragmentList.size(); j++){
                        if (dayTimeFragmentList.get(j).toString().equals(temp.get(i))){
                            dayTimeFragmentList.remove(j);
                        }
                    }
                }
                DayTimeSelectRecycleAdapter.this.temp.clear();
                //清空数据库
                dao.clearTable();
                //把数据存入数据库
                for (int i = 0; i<dayTimeFragmentList.size(); i++){
                    dao.addDayTimeFragment(dayTimeFragmentList.get(i));
                }
                //刷新
                refresh();
            }
        });

        //计算时间段差
        Time endTime=Time.parseTime(dayTimeFragmentList.get(position).getEnd());
        Time startTime=Time.parseTime(dayTimeFragmentList.get(position).getStart());
        int cha=endTime.subOfMinute(startTime);

        if (dayTimeFragmentList.size()>1){
            if (position==0){//首个时间差
                String topName="00:00-"+dayTimeFragmentList.get(position).getStart();
                int topcha=startTime.subOfMinute(new Time());
                Time nextStartTime=Time.parseTime(dayTimeFragmentList.get(position+1).getStart());
                String nextName=dayTimeFragmentList.get(position).getEnd()+"-"+dayTimeFragmentList.get(position+1).getStart();
                int nextcha=nextStartTime.subOfMinute(endTime);
                timeLength.add(topcha);//添加首个时间差
                timeName.add(topName);//添加首个时间差名称
                timeIdentify.add(false);//添加首个时间差标识
                timeLength.add(cha);//添加当前时间差
                timeName.add(dayTimeFragmentList.get(position).toString());
                timeIdentify.add(true);
                timeLength.add(nextcha);//添加与下一个时间差的时间差
                timeName.add(nextName);
                timeIdentify.add(false);
            }else if(position==dayTimeFragmentList.size()-1){//最后一个时间差
                String lastName=dayTimeFragmentList.get(position).getEnd()+"-24:00";
                Time lastTime=new Time();
                lastTime.setHour(23);
                lastTime.setMinute(59);
                int lastcha=lastTime.subOfMinute(endTime);
                timeLength.add(cha);
                timeIdentify.add(true);
                timeLength.add(lastcha);
                timeIdentify.add(false);
                timeName.add(dayTimeFragmentList.get(position).toString());
                timeName.add(lastName);
            }else {
                Time nextStartTime=Time.parseTime(dayTimeFragmentList.get(position+1).getStart());
                String nextName=dayTimeFragmentList.get(position).getEnd()+"-"+dayTimeFragmentList.get(position+1).getStart();
                int nextcha=nextStartTime.subOfMinute(endTime);
                timeLength.add(cha);
                timeIdentify.add(true);
                timeLength.add(nextcha);
                timeIdentify.add(false);
                timeName.add(dayTimeFragmentList.get(position).toString());
                timeName.add(nextName);
            }
        }else if (dayTimeFragmentList.size()==1){
            String topName="00:00-"+dayTimeFragmentList.get(position).getStart();
            String lastName=dayTimeFragmentList.get(position).getEnd()+"-24:00";
            Time lastTime=new Time();
            lastTime.setHour(23);
            lastTime.setMinute(59);
            int topcha=startTime.subOfMinute(new Time());
            int lastcha=lastTime.subOfMinute(endTime);
            timeLength.add(topcha);//添加首个时间差
            timeIdentify.add(false);
            timeLength.add(cha);//添加当前时间差
            timeIdentify.add(true);
            timeLength.add(lastcha);//添加与末尾时间差
            timeIdentify.add(false);
            timeName.add(topName);
            timeName.add(dayTimeFragmentList.get(position).toString());
            timeName.add(lastName);

        }

        //向扇形图填充数据
        binding.mPieChart.setDatas(timeLength,timeIdentify);
        binding.mPieChart.setTexts(timeName);
        binding.mPieChart.invalidate();


    }
    @Override
    public int getItemCount() {
        //返回Item的数量
        return dayTimeFragmentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void refresh() {
        isRestart = true;
        this.dayTimeFragmentList = dao.selectAll();
        this.timeLength=new ArrayList<Integer>();
        this.timeName=new ArrayList<String>();
        this.timeIdentify=new ArrayList<Boolean>();
        notifyDataSetChanged();
    }
    public void isVisible(boolean isvisible){
        if (isvisible) {
            binding.daytimeSelectAddBotton.setVisibility(View.GONE);
            binding.daytimeSelectBackBotton.setVisibility(View.VISIBLE);
            binding.daytimeSelectDeleteBotton.setVisibility(View.VISIBLE);
        }else {
            binding.daytimeSelectAddBotton.setVisibility(View.VISIBLE);
            binding.daytimeSelectBackBotton.setVisibility(View.GONE);
            binding.daytimeSelectDeleteBotton.setVisibility(View.GONE);
        }

    }

}