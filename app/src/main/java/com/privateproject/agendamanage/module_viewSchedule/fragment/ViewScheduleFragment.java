package com.privateproject.agendamanage.module_viewSchedule.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.necer.calendar.BaseCalendar;
import com.necer.calendar.Miui10Calendar;
import com.necer.enumeration.CalendarState;
import com.necer.enumeration.CheckModel;
import com.necer.enumeration.DateChangeBehavior;
import com.necer.listener.OnCalendarChangedListener;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.db.dao.TaskDao;
import com.privateproject.agendamanage.module_viewSchedule.activity.ViewSchedulePlanNodeActivity;
import com.privateproject.agendamanage.module_viewSchedule.activity.ViewScheduleTaskActivity;
import com.privateproject.agendamanage.module_viewSchedule.adapter.ViewScheduleAdapter;
import com.privateproject.agendamanage.module_viewSchedule.adapter.ViewSchedulePlanNodeAdapter;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewScheduleFragment extends Fragment {
    private ViewScheduleAdapter adapter;
    private RecyclerView recyclerView;
    private String dateStr;
    private TextView messageNumber;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewschedule_fragment_schedule,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = sdf.format(date);

        Miui10Calendar miui10Calendar=view.findViewById(R.id.viewScheduleFragment_Calendar);
        ImageButton foldBtn=view.findViewById(R.id.viewScheduleFragment_foldBtn);
        Button jumpBtn=view.findViewById(R.id.jumpBtn);
        Button todayBtn=view.findViewById(R.id.jumpTodayBtn);
        TextView tv_result=view.findViewById(R.id.tv_result);


        messageNumber = view.findViewById(R.id.viewSchedule_messageNumber_tv);
        messageNumber.setText(initPlanNode()+"");

        adapter=new ViewScheduleAdapter(getContext(),dateStr);
        recyclerView=view.findViewById(R.id.calendar_recycleView);
        recyclerView.setAdapter(adapter);

        miui10Calendar.setCheckMode(CheckModel.SINGLE_DEFAULT_CHECKED);


        //跳转日期
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            private int Year;
            private int Month;
            private int Day;
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Year = year;
                        Month = month+1;
                        Day = dayOfMonth;
                        miui10Calendar.jumpDate(Year,Month,Day);
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }

        });

        //返回今日
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateStr = sdf.format(date);
                miui10Calendar.jumpDate(dateStr);
            }
        });

        //日历点击日期监听器
        miui10Calendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {

            @Override
            public void onCalendarChange(BaseCalendar baseCalendar, int year, int month, LocalDate localDate, DateChangeBehavior dateChangeBehavior) {

                tv_result.setText(localDate+"");

                if (localDate != null) {
                    dateStr = localDate.toString("yyyy-MM-dd");
                } else {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateStr = sdf.format(date);
                }
                adapter.setDateStr(dateStr);
                adapter.setCourseAndTaskList(adapter.init());
                adapter.notifyDataSetChanged();
            }

        });

        foldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miui10Calendar.getCalendarState() == CalendarState.WEEK) {
                    miui10Calendar.toMonth();
                    foldBtn.setImageResource(R.drawable.down);
                } else {
                    miui10Calendar.toWeek();
                    foldBtn.setImageResource(R.drawable.up);
                }
            }
        });

        messageNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewSchedulePlanNodeActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setDateStr(dateStr);
        adapter.setCourseAndTaskList(adapter.init());
        adapter.notifyDataSetChanged();
        initPlanNode();
    }


    public int initPlanNode(){
        TargetDao targetDao = new TargetDao(getContext());
        List<PlanNode> monthPlanNodes = new ArrayList<PlanNode>();
        //所有为true的target
        List<Target> trueTargets = targetDao.selectAllTtue();
        if(trueTargets!=null && trueTargets.size()!=0){
            for (int i = 0; i < trueTargets.size(); i++) {
                //该target下的所有叶子结点
                List<PlanNode> targetPlanNodes = targetDao.selectLastPlanNode(trueTargets.get(i),getContext());
                if(targetPlanNodes!=null && targetPlanNodes.size()!=0){
                    for (int j = 0; j < targetPlanNodes.size(); j++) {
                        if (ViewSchedulePlanNodeAdapter.isNumWitToday(targetPlanNodes.get(j).getStartTime(),30)){
                            monthPlanNodes.add(targetPlanNodes.get(j));
                        }
                    }
                }
            }
        }
        return monthPlanNodes.size();
    }
    //PlanNode还需分配的时间
    public int remianTime(PlanNode planNode){
        TaskDao taskDao = new TaskDao(getContext());
        List<Task> taskList = taskDao.selectByPlanNode(planNode);
        int needMinutes = planNode.getTimeNeeded();
        int remainMinutes = needMinutes;
        for (int i = 0; i < taskList.size(); i++) {
            remainMinutes -= taskList.get(i).getMintime();
        }
        return remainMinutes;
    }
}

