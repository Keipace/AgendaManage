package com.privateproject.agendamanage.module_viewSchedule.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.Course;
import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.db.dao.TaskDao;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewScheduleShowCellActivity extends AppCompatActivity {

    PlanNode planNode;
    private Date date = new Date();//日期
    private DayTimeFragment dayTimeFragment;//时间段
    private List<Task> taskList = new ArrayList<Task>();//这个日期和时间段的所有task
    int totalMinutes = 0;//时间段的时间
    int remainMinutes = 0;//还剩得时间
    private TaskDao taskDao;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewschedule_activity_showcell);

        this.taskDao = new TaskDao(this);
        PlanNodeDao planNodeDao = new PlanNodeDao(this);
        DayTimeFragmentDao dayTimeFragmentDao = new DayTimeFragmentDao(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        planNode = planNodeDao.selectById(bundle.getInt("planNodeId"));
        //根据date获得task判断周几获得课程。
        try {
            date = sdf.parse(bundle.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //根据id获得DayTimeFragent;也就是行
        dayTimeFragment = dayTimeFragmentDao.selectById(bundle.getInt("dayTimeFragmentId"));
        int row = bundle.getInt("row");


        taskList = taskDao.selectDayAndTime(date,dayTimeFragment);
        //获得总共时间
        totalMinutes = ViewScheduleTaskActivity.timeMinutes(dayTimeFragment.getStart(),dayTimeFragment.getEnd());
        remainMinutes = totalMinutes;
        //获得当天周几
        int weekDay = 1;
        try {
            weekDay = EverydayTotalTimeServer.dayForWeek(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获得当天那个时间段的课程
        CourseDao courseDao = new CourseDao(this);
        List<Course> courses = courseDao.selectByPosition(row,weekDay);
        //获得剩余时间
        if(taskList!=null&&taskList.size()!=0){
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                remainMinutes -= task.getMintime();
            }
        }

        TextView planNodeNameTv = findViewById(R.id.viewSchedule_showCell_planNodeName_tv);
        TextView timeNeedTv = findViewById(R.id.viewSchedule_showCell_timeNeed_tv);
        TextView remianTv = findViewById(R.id.viewSchedule_showCell_remianTime_tv);
        planNodeNameTv.setText(planNode.getName());
        timeNeedTv.setText("共需"+planNode.getTimeNeeded()+"分钟");
        remianTv.setText("还需分配"+remianTime(planNode)+"分钟");

        //显示日期
        TextView dateTv = findViewById(R.id.viewSchedule_showCell_date_tv);
        dateTv.setText(bundle.getString("date")+"("+dayTimeFragment.toString()+")");
        //显示总分钟
        TextView totalTimeTv = findViewById(R.id.viewSchedule_showCell_totalMinutes_tv);
        totalTimeTv.setText("共有"+totalMinutes+"分钟");
        //显示剩余时间和课程
        TextView remianTimeTv = findViewById(R.id.viewSchedule_showCell_remianMinutes_tv);
        TextView courseTv = findViewById(R.id.viewSchedule_showCell_course_tv);
        if(courses!=null&&courses.size()!=0){
            remianTimeTv.setText("剩余"+0+"分钟");
            courseTv.setText(showCourses(courses));
        }else {
            remianTimeTv.setText("剩余"+remainMinutes+"分钟");
            courseTv.setText("没有课程");
        }

        RecyclerView recyclerView = findViewById(R.id.viewSchedule_showCell_taskRecyckler_recycleview);
        MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter();
        recyclerView.setAdapter(myRecyclerAdapter);

        FloatingActionButton addFloat = findViewById(R.id.viewSchedule_itemShowTask_add_floatBtn);
        addFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(courses!=null&&courses.size()!=0){
                    ToastUtil.newToast(ViewScheduleShowCellActivity.this,"该段时间已经被课程沾满了哦！！！");
                }else {

                }

            }
        });
    }



    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder>{

        @NonNull
        @Override
        public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(ViewScheduleShowCellActivity.this).inflate(R.layout.viewschedule_item_showtask_showcell,parent,false);
            return new MyRecyclerViewHolder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position) {
            holder.nameTv.setText(taskList.get(position).getName());
            holder.decorationTv.setText(taskList.get(position).getPlan());
            holder.dateTv.setText(sdf.format(taskList.get(position).getDay()));
            holder.dayFragmentTv.setText("("+taskList.get(position).getTimeFragment().toString()+")");
            holder.timeNeed.setText(taskList.get(position).getMintime()+"");

            holder.editImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remainMinutes += taskList.get(position).getMintime();
                    taskDao.deleteTaskById(taskList.get(position));
                    taskList = taskDao.selectDayAndTime(date,dayTimeFragment);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            if(taskList==null||taskList.size()==0){
                return 0;
            }
            return taskList.size();
        }
    }
    class MyRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView nameTv;
        TextView decorationTv;
        TextView dateTv;
        TextView timeNeed;
        TextView dayFragmentTv;
        TextView minutesTv;
        ImageView delImg;
        ImageView editImg;

        public MyRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.viewSchedule_itemShowTask_name_tv);
            decorationTv = itemView.findViewById(R.id.viewSchedule_itemShowTask_decoration_tv);
            dateTv = itemView.findViewById(R.id.viewSchedule_itemShowTask_date_tv);
            timeNeed = itemView.findViewById(R.id.viewSchedule_itemShowTask_timeNeed_tv);
            dayFragmentTv = itemView.findViewById(R.id.viewSchedule_itemShowTask_dayFragment_tv);
            minutesTv = itemView.findViewById(R.id.viewSchedule_itemShowTask_minute_tv);
            delImg = itemView.findViewById(R.id.viewSchedule_itemShowTask_delete_imageView);
            editImg = itemView.findViewById(R.id.viewSchedule_itemShowTask_edit_imageView);
        }
    }


    public String showCourses(List<Course> courseList){
        String courseText = "";
        for (int i = 0; i < courseList.size(); i++) {
            Course course = courseList.get(i);
            courseText += course.getClassname()+"    "+course.getAddress()+"\n";
        }
        return courseText;
    }
    //PlanNode还需分配的时间
    public int remianTime(PlanNode planNode){
        List<Task> taskList = taskDao.selectByPlanNode(planNode);
        int needMinutes = planNode.getTimeNeeded();
        int remainMinutes = needMinutes;
        for (int i = 0; i < taskList.size(); i++) {
            remainMinutes -= taskList.get(i).getMintime();
        }
        return remainMinutes;
    }
}