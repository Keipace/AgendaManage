package com.privateproject.agendamanage.module_weekTime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.databinding.WeektimeActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.db.bean.Course;
import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.dao.TaskDao;
import com.privateproject.agendamanage.module_weekTime.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.PieChartView;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.module_weekTime.viewHolder.DayTimeSelectViewHolder;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DayTimeSelectRecycleAdapter extends RecyclerView.Adapter<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder> {
    private Context context;
    private DayTimeFragmentDao dao;
    private List<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder> items;
    private List<DayTimeFragment> dayTimeFragmentList;
    private List<String> temp;
    //扇形图时间差和名称
    private List<Integer> timeLength;
    private List<String> timeName;
    private List<Boolean> timeIdentify;
    private List<RaiflatButton> buttons;
    private PieChartView mPieChart;

    public DayTimeSelectRecycleAdapter(Context context, List<RaiflatButton> buttons, PieChartView mPieChart) {
        this.context = context;
        this.dao = new DayTimeFragmentDao(context);
        this.dayTimeFragmentList = dao.selectAll();
        this.items = new ArrayList<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder>();
        this.temp = new ArrayList<String>();
        this.timeName=new ArrayList<String>();
        this.timeLength=new ArrayList<Integer>();
        this.timeIdentify=new ArrayList<Boolean>();
        this.buttons = buttons;
        this.mPieChart = mPieChart;
    }



    @NonNull
    @Override
    public DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载ViewType为TextView的ViewHolder
        return new DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder
                (LayoutInflater.from(context).inflate(R.layout.weektime_item_daytime_select_textview, parent, false));
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
        buttons.get(1).setOnClickListener(new View.OnClickListener() {
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
                temp.clear();
            }
        });

        //删除按钮监听器
        buttons.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible(false);

                //删除dayTimeFragmentList中对应下标的数据
                for (int i = 0; i<temp.size(); i++){
                    for (int j = 0; j<dayTimeFragmentList.size(); j++){
                        if (dayTimeFragmentList.get(j).toString().equals(temp.get(i))){
                            if(!isDayFragmentUsed(dayTimeFragmentList.get(j))){
                                dayTimeFragmentList.remove(j);
                            }
                        }
                    }
                }
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置可选，CheckBox不可见
                    items.get(i).RecycleTextView.setEnabled(true);
                    items.get(i).daytimeSelectCheckBox.setChecked(false);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.GONE);
                }
                temp.clear();
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
        mPieChart.setDatas(timeLength,timeIdentify);
        mPieChart.setTexts(timeName);
        mPieChart.invalidate();


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
            buttons.get(0).setVisibility(View.GONE);
            buttons.get(1).setVisibility(View.VISIBLE);
            buttons.get(2).setVisibility(View.VISIBLE);
        }else {
            buttons.get(0).setVisibility(View.VISIBLE);
            buttons.get(1).setVisibility(View.GONE);
            buttons.get(2).setVisibility(View.GONE);
        }
    }

    public boolean isDayFragmentUsed(DayTimeFragment dayTimeFragment){
        TaskDao taskDao = new TaskDao(context);
        CourseDao courseDao = new CourseDao(context);
        List<Task> taskList = taskDao.selectAll();
        List<Course> courseList = courseDao.selectAll();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            if(task.getTimeFragment().equals(dayTimeFragment)){
                ToastUtil.newToast(context,"此段时间有任务哦");
                return true;
            }
        }
        for (int i = 0; i < courseList.size(); i++) {
            Course course = courseList.get(i);
            if(dayTimeFragmentList.get(course.getRow()-1)!=null){
                ToastUtil.newToast(context,"此段时间有课程哦");
                return true;
            }
        }
        return false;
    }

}