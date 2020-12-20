package com.privateproject.agendamanage.module_viewSchedule.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
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
import com.privateproject.agendamanage.utils.FlipDialog;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.utils.TimeUtil;
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

    private TextView planNoderemianTimeTv;
    private TextView remianTimeTv;
    private MyRecyclerAdapter myRecyclerAdapter;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewschedule_activity_showcell);

        this.taskDao = new TaskDao(this);
        PlanNodeDao planNodeDao = new PlanNodeDao(this);
        DayTimeFragmentDao dayTimeFragmentDao = new DayTimeFragmentDao(this);
        CourseDao courseDao = new CourseDao(this);

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

        //获得当天周几
        int weekDay = 1;
        try {
            weekDay = EverydayTotalTimeServer.dayForWeek(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获得总共时间
        totalMinutes = ViewScheduleTaskActivity.timeMinutes(dayTimeFragment.getStart(),dayTimeFragment.getEnd());
        remainMinutes = totalMinutes;
        //获得当天那个时间段的课程
        List<Course> courses = courseDao.selectByPosition(row,weekDay);

        TextView planNodeNameTv = findViewById(R.id.viewSchedule_showCell_planNodeName_tv);//planNode的名字
        TextView planNodetimeNeedTv = findViewById(R.id.viewSchedule_showCell_timeNeed_tv);//plannode所需日期
        planNoderemianTimeTv = findViewById(R.id.viewSchedule_showCell_remianTime_tv);
        //显示日期
        TextView dateTv = findViewById(R.id.viewSchedule_showCell_date_tv);
        //显示总分钟
        TextView totalTimeTv = findViewById(R.id.viewSchedule_showCell_totalMinutes_tv);
        //显示剩余时间和课程
        remianTimeTv = findViewById(R.id.viewSchedule_showCell_remianMinutes_tv);
        TextView courseTv = findViewById(R.id.viewSchedule_showCell_course_tv);


        taskList = taskDao.selectDayAndTime(date,dayTimeFragment);
        //获得剩余时间
        if(taskList!=null&&taskList.size()!=0){
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                remainMinutes -= task.getMintime();
            }
        }
        planNodeNameTv.setText(planNode.getName());
        planNodetimeNeedTv.setText("共需"+planNode.getTimeNeeded()+"分钟");
        planNoderemianTimeTv.setText("还需分配"+remianTime(planNode)+"分钟");
        dateTv.setText(bundle.getString("date")+"("+dayTimeFragment.toString()+")");
        totalTimeTv.setText("共有"+totalMinutes+"分钟");
        if(courses!=null&&courses.size()!=0){
            remianTimeTv.setText("剩余"+0+"分钟");
            courseTv.setText(showCourses(courses));
        }else {
            remianTimeTv.setText("剩余"+remainMinutes+"分钟");
            courseTv.setText("没有课程");
        }

        RecyclerView recyclerView = findViewById(R.id.viewSchedule_showCell_taskRecyckler_recycleview);
        myRecyclerAdapter = new MyRecyclerAdapter();
        recyclerView.setAdapter(myRecyclerAdapter);

        FloatingActionButton addFloat = findViewById(R.id.viewSchedule_itemShowTask_add_floatBtn);

        addFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(courses!=null&&courses.size()!=0){
                    ToastUtil.newToast(ViewScheduleShowCellActivity.this,"该段时间已经被课程沾满了哦！！！");
                }else {
                    FlipDialog dialog=new FlipDialog(ViewScheduleShowCellActivity.this);
                    dialog.setCancelBtn(new FlipDialog.IOnCancelListener() {
                        @Override
                        public void OnCancel(FlipDialog dialog) {
                            dialog.dismiss();
                        }
                    }).setConfirmBtn(new FlipDialog.IOnConfirmListener() {
                        @Override
                        public void OnConfirm(FlipDialog dialog, String taskName, String taskDesc, Time startTime, Time endTime) {
                            int minute = endTime.subOfMinute(startTime);
                            if (taskName == null || taskDesc == null) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "名称或描述未添加！");
                            } else if (startTime.after(endTime)) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "开始时间需早于结束时间");
                            } else if (startTime.before(stringToTime(dayTimeFragment.getStart()))) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "开始时间需晚于于该时间段开始时间");
                            } else if (endTime.after(stringToTime(dayTimeFragment.getEnd()))) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "结束时间需早于于该时间段结束时间");
                            } else if(minute>remainMinutes||minute>remianTime(planNode)){
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this,"您分配的时间已经超过了剩余时间了！！！");
                            }else {
                                Task task=new Task(taskName,taskDesc,bundle.getString("date"),dayTimeFragment,minute,planNode);
                                taskDao.addTask(task);

                                refreshShowCell();
                                myRecyclerAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    }).show();

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
                    Task task = taskList.get(position);
                    FlipDialog dialog=new FlipDialog(ViewScheduleShowCellActivity.this,task.getName(),task.getPlan());
                    dialog.setCancelBtn(new FlipDialog.IOnCancelListener() {
                        @Override
                        public void OnCancel(FlipDialog dialog) {
                            dialog.dismiss();
                        }
                    }).setConfirmBtn(new FlipDialog.IOnConfirmListener() {
                        @Override
                        public void OnConfirm(FlipDialog dialog, String taskName, String taskDesc, Time startTime, Time endTime) {
                            int minute = endTime.subOfMinute(startTime);
                            if (taskName == null || taskDesc == null) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "名称或描述未添加！");
                            } else if (startTime.after(endTime)) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "开始时间需早于结束时间");
                            } else if (startTime.before(stringToTime(dayTimeFragment.getStart()))) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "开始时间需晚于于该时间段开始时间");
                            } else if (endTime.after(stringToTime(dayTimeFragment.getEnd()))) {
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this, "结束时间需早于于该时间段结束时间");
                            } else if(minute>totalMinutes||minute>planNode.getTimeNeeded()){
                                ToastUtil.newToast(ViewScheduleShowCellActivity.this,"您分配的时间已经超过了剩余时间了！！！");
                            }else {
                                task.setName(taskName);
                                task.setPlan(taskDesc);
                                task.setMintime(endTime.subOfMinute(startTime));
                                taskDao.updateTask(task);
                                refreshShowCell();
                                myRecyclerAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    }).show();


                }
            });

            holder.delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remainMinutes += taskList.get(position).getMintime();
                    taskDao.deleteTaskById(taskList.get(position));
                    refreshShowCell();
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
    public Time stringToTime(String string){
        int hour = Integer.parseInt(string.split(":")[0]);
        int minute = Integer.parseInt(string.split(":")[1]);
        Time time = new Time(hour,minute);
        return time;
    }

    public void refreshShowCell(){
        remainMinutes = totalMinutes;
        planNoderemianTimeTv.setText("还需分配"+remianTime(planNode)+"分钟");
        taskList = taskDao.selectDayAndTime(date,dayTimeFragment);
        if(taskList!=null&&taskList.size()!=0){
            for (int i = 0; i < taskList.size(); i++) {
                Task task1 = taskList.get(i);
                remainMinutes -= task1.getMintime();
            }
        }
        remianTimeTv.setText("剩余"+remainMinutes+"分钟");
    }
}