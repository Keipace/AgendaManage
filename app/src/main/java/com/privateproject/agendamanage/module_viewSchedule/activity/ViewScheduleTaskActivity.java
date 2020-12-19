package com.privateproject.agendamanage.module_viewSchedule.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewScheduleTaskActivity extends AppCompatActivity {

    public static final int HEAD = 0;
    public static final int LEFT = 1;
    public static final int CELL = 2;

    private PlanNode planNode;
    private PlanNodeDao planNodeDao;
    private DayTimeFragmentDao dayTimeFragmentDao;
    private List<DayTimeFragment> dayTimeList;//时间段
    private List<Date> dateList;//planNode的日期列表
    private int rowNum=0;
    private int colNum=0;
    private Object[][] datas;
    RecyclerView recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewschedule_activity_task);

        Intent intent = getIntent();
        int planNodeId = intent.getIntExtra("PlanNodeId",0);
        planNodeDao = new PlanNodeDao(this);
        planNode = planNodeDao.selectById(planNodeId);

        TextView planNodeNameTv = findViewById(R.id.viewSchedule_task_planNodeName_tv);
        TextView timeNeedTv = findViewById(R.id.viewSchedule_task_timeNeed_tv);
        TextView remianTv = findViewById(R.id.viewSchedule_task_remianTime_tv);
        planNodeNameTv.setText(planNode.getName());
        timeNeedTv.setText("共需"+planNode.getTimeNeeded()+"分钟");
        remianTv.setText("还需分配"+remianTime(planNode)+"分钟");

        dayTimeFragmentDao = new DayTimeFragmentDao(this);
        dayTimeList = dayTimeFragmentDao.selectAll();
        if (planNode!=null||planNode.getDuringDay()!=0){
            colNum = planNode.getDuringDay()+1;
        }
        if(dayTimeList!=null||dayTimeList.size()!=0){
            rowNum = dayTimeList.size()+1;
        }
        datas = new Object[rowNum][colNum];
        datas[0][0] = new String("");
        //初始化时间段
        for (int i = 0; i < rowNum-1; i++) {
            datas[i+1][0] = dayTimeList.get(i);
        }
        //初始化日期
        dateList = startToEndDates(planNode.getStartTime(),planNode.getEndTime());
        List<String> dateStrs = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        for (int i = 0; i < dateList.size(); i++) {
            dateStrs.add(sdf.format(dateList.get(i)));
        }
        for (int i = 0; i < colNum-1; i++) {
            datas[0][i+1] = dateStrs.get(i);
        }
        //初始化Cell
        for (int i = 1; i < rowNum; i++) {
            for (int j = 1; j < colNum; j++) {
                datas[i][j] = timeMinutes(dayTimeList.get(i-1).getStart(),dayTimeList.get(i-1).getEnd());
            }
        }
        initCourseAndTask();
        recycler = findViewById(R.id.viewSchedule_task_list_recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,colNum);
        recycler.setLayoutManager(layoutManager);
        MyTaskAdapter myTaskAdapter = new MyTaskAdapter();
        recycler.setAdapter(myTaskAdapter);
    }

    class MyTaskAdapter extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ViewScheduleTaskActivity.this);
            View root;
            if (viewType == HEAD) {
                //第一行的viewHolder
                root = layoutInflater.inflate(R.layout.weektime_item_week_time_head, parent, false);
                return new TasksHeaderHolder(root);
            } else if (viewType == LEFT) {
                //第一列的viewHolder
                root = layoutInflater.inflate(R.layout.weektime_item_week_time_left, parent, false);
                return new TasksLeftHolder(root);
            } else {
                //数据区的viewHolder
                root = layoutInflater.inflate(R.layout.weektime_item_week_time, parent, false);
                return new TasksHolder(root);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int row = position/colNum;
            int col = position%colNum;
            if (getItemViewType(position) == HEAD) {
               TasksHeaderHolder tasksHeaderHolder = (TasksHeaderHolder)holder;
                tasksHeaderHolder.textViewHead.setText(datas[row][col].toString());
            } else if (getItemViewType(position) == LEFT) {
               TasksLeftHolder tasksLeftHolder = (TasksLeftHolder)holder;
                tasksLeftHolder.textViewLeft.setText(datas[row][col].toString());
            } else {
                TasksHolder tasksHolder = (TasksHolder)holder;
                tasksHolder.btnCell.setText("还剩"+"\n"+datas[row][col]+"\n"+"分钟");
                tasksHolder.btnCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewScheduleTaskActivity.this,ViewScheduleShowCellActivity.class);
                        Bundle bundle = new Bundle();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = sdf.format(dateList.get(col-1));
                        int dayTimeFragment =  dayTimeList.get(row-1).getId();
                        bundle.putInt("dayTimeFragmentId",dayTimeFragment);
                        bundle.putString("date",dateStr);
                        bundle.putInt("row",row);
                        bundle.putInt("planNodeId",planNode.getId());
                        intent.putExtra("bundle",bundle);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return (rowNum*colNum);
        }

        @Override
        public int getItemViewType(int position) {
            if(position/colNum==0){
                return HEAD;
            }else if(position%colNum==0){
                return LEFT;
            }else {
                return CELL;
            }
        }
    }

    class TasksHeaderHolder extends RecyclerView.ViewHolder{
        public TextView textViewHead;
        public TasksHeaderHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewHead = itemView.findViewById(R.id.itemWeekTime_head_tv);
        }
    }
    class TasksLeftHolder extends RecyclerView.ViewHolder{
        public TextView textViewLeft;
        public TasksLeftHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewLeft = itemView.findViewById(R.id.itemWeekTime_left_textView);
        }
    }
    class TasksHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout rootView;
        public Button btnCell;
        public TextView textViewRight;
        public TextView textViewBottom;
        public TasksHolder(@NonNull View itemView) {
            super(itemView);
            this.rootView = itemView.findViewById(R.id.itemWeekTime_rootView);
            this.btnCell = itemView.findViewById(R.id.itemWeekTime_cell_btn);
            this.textViewRight = itemView.findViewById(R.id.itemWeekTime_right_textView);
            this.textViewBottom = itemView.findViewById(R.id.itemWeekTime_bottom_textView);
        }
    }

    public void initCourseAndTask(){
        CourseDao courseDao = new CourseDao(this);
        TaskDao taskDao = new TaskDao(this);
        for (int i = 0; i < dateList.size(); i++) {//循环天
            int weekNum = 1;
            Date thisDay = dateList.get(i);
            //添加今天的任务
            List<Task> tasks = taskDao.selectDay(thisDay);
            for (int j = 0; j < tasks.size(); j++) {
                Task task = tasks.get(j);
                int row = dayTimeList.indexOf(task.getTimeFragment())+1;
                datas[row][i+1] = (int)datas[row][i+1]-task.getMintime();
            }
            //添加今天的课程
            try {
                weekNum = EverydayTotalTimeServer.dayForWeek(thisDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Course> courses = courseDao.selectByWeekDay(weekNum);
            for (int j = 0; j < courses.size(); j++) {
                Course course = courses.get(j);
                datas[course.getRow()][i+1] = 0;
            }
        }
    }

    public List<Date> startToEndDates(Date surplusStartDate, Date surplusEndDate){
        //获得在开始日期和结束日期之间的日期列表
        List<Date> dateList = new ArrayList<Date>();
        dateList.add(surplusStartDate);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(surplusStartDate);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(surplusEndDate);
        // 测试此日期是否在指定日期之后
        while (surplusEndDate.after(calBegin.getTime()))
        {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(calBegin.getTime());
        }
        return  dateList;
    }

    //两个时间段有多少分钟
    public static int timeMinutes(String start,String end){
        int start0 = Integer.parseInt(start.split(":")[0]);
        int start1 = Integer.parseInt(start.split(":")[1]);
        int end0 = Integer.parseInt(end.split(":")[0]);
        int end1 = Integer.parseInt(end.split(":")[1]);
        if(start0>end0||(start0==end0&&start1>end1)){
            return -1;
        }else {
            return (end0-start0)*60+end1-start1;
        }
    }

    //PlanNode还需分配的时间
    public int remianTime(PlanNode planNode){
        TaskDao taskDao = new TaskDao(ViewScheduleTaskActivity.this);
        List<Task> taskList = taskDao.selectByPlanNode(planNode);
        int needMinutes = planNode.getTimeNeeded();
        int remainMinutes = needMinutes;
        for (int i = 0; i < taskList.size(); i++) {
            remainMinutes -= taskList.get(i).getMintime();
        }
        return remainMinutes;
    }
}