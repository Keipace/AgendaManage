package com.privateproject.agendamanage.module_viewSchedule.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.db.dao.TaskDao;
import com.privateproject.agendamanage.module_viewSchedule.activity.ViewScheduleTaskActivity;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewSchedulePlanNodeAdapter extends RecyclerView.Adapter<ViewSchedulePlanNodeAdapter.SchedulePlanNodeHolder> {
    private Context context;
    private int sectionNumber;
    private int remianTime;
    //所有父target为true的叶子PlanNode
    List<PlanNode> allPlanNodes = new ArrayList<PlanNode>();
    List<PlanNode> dayPlanNodes = new ArrayList<PlanNode>();
    List<PlanNode> weekPlanNodes = new ArrayList<PlanNode>();
    List<PlanNode> monthPlanNodes = new ArrayList<PlanNode>();
    List<PlanNode> nowPlanNodes = new ArrayList<PlanNode>();

    public ViewSchedulePlanNodeAdapter(Context context,int sectionNumber) {
        this.context = context;
        this.sectionNumber = sectionNumber;
        init();
    }

    @NonNull
    @Override
    public SchedulePlanNodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewschedule_item_fragment_plannode,parent,false);
        return new SchedulePlanNodeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulePlanNodeHolder holder, int position) {
        holder.nameTv.setText(nowPlanNodes.get(position).getName());
        holder.decorationTv.setText(nowPlanNodes.get(position).getDecoration());
        holder.dateTimeTv.setText(twoDateStr(nowPlanNodes.get(position).getStartTime(),nowPlanNodes.get(position).getEndTime()));
        holder.timeNeedTv.setText(nowPlanNodes.get(position).getTimeNeeded()+"");
        holder.remainTv.setText("还需分配"+remianTime(nowPlanNodes.get(position))+"分钟");

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewScheduleTaskActivity.class);
                intent.putExtra("PlanNodeId",nowPlanNodes.get(position).getId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(nowPlanNodes==null||nowPlanNodes.size()==0){
            return 0;
        }
        return nowPlanNodes.size();
    }


    class SchedulePlanNodeHolder extends RecyclerView.ViewHolder{

        TextView nameTv;
        TextView decorationTv;
        TextView dateTimeTv;
        TextView timeNeedTv;
        TextView minuteTv;
        TextView remainTv;
        ConstraintLayout layout;

        public SchedulePlanNodeHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.viewSchedule_itemPlanNode_name_tv);
            decorationTv = itemView.findViewById(R.id.viewSchedule_itemPlanNode_decoration_tv);
            dateTimeTv = itemView.findViewById(R.id.viewSchedule_itemPlanNode_daytime_tv);
            timeNeedTv = itemView.findViewById(R.id.viewSchedule_itemPlanNode_timeNeed_tv);
            minuteTv = itemView.findViewById(R.id.viewSchedule_itemPlanNode_minute_tv);
            remainTv = itemView.findViewById(R.id.viewSchedule_itemPlanNode_remainTime_tv);
            layout = itemView.findViewById(R.id.viewSchedule_itemPlanNode_layout);
        }
    }

    public void init(){
        TargetDao targetDao = new TargetDao(context);
        //所有为true的target
        List<Target> trueTargets = targetDao.selectAllTtue();
        if(trueTargets!=null && trueTargets.size()!=0){
            for (int i = 0; i < trueTargets.size(); i++) {
                //该target下的所有叶子结点
                List<PlanNode> targetPlanNodes = targetDao.selectLastPlanNode(trueTargets.get(i),context);
                if(targetPlanNodes!=null && targetPlanNodes.size()!=0){
                    for (int j = 0; j < targetPlanNodes.size(); j++) {
                        if(isNumWitToday(targetPlanNodes.get(j).getStartTime(),1)){
                            dayPlanNodes.add(targetPlanNodes.get(j));
                        }
                        if (isNumWitToday(targetPlanNodes.get(j).getStartTime(),7)){
                            weekPlanNodes.add(targetPlanNodes.get(j));
                        }
                        if (isNumWitToday(targetPlanNodes.get(j).getStartTime(),30)){
                            monthPlanNodes.add(targetPlanNodes.get(j));
                        }
                        allPlanNodes.add(targetPlanNodes.get(j));
                    }
                }
            }
        }
        if(sectionNumber==1){
            nowPlanNodes = dayPlanNodes;
        }else if(sectionNumber==2){
            nowPlanNodes = weekPlanNodes;
        }else {
            nowPlanNodes = monthPlanNodes;
        }
    }
    public static boolean isNumWitToday(Date date,int num){
        Date today = new Date();
        int dayNum =  EverydayTotalTimeServer.differentDays(today,date)-1;
        if(dayNum>0&&dayNum<=num){
            return true;
        }
        return false;
    }

    public String twoDateStr(Date date1,Date date2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date1Str = sdf.format(date1);
        String date2Str = sdf.format(date2);
        return "("+date1Str+"~"+date2Str+")";
    }

    public int remianTime(PlanNode planNode){
        TaskDao taskDao = new TaskDao(context);
        List<Task> taskList = taskDao.selectByPlanNode(planNode);
        int needMinutes = planNode.getTimeNeeded();
        int remainMinutes = needMinutes;
        for (int i = 0; i < taskList.size(); i++) {
            remainMinutes -= taskList.get(i).getMintime();
        }
        return remainMinutes;
    }

}
