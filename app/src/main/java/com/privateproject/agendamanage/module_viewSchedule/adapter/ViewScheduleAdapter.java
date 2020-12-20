package com.privateproject.agendamanage.module_viewSchedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.Course;
import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.dao.TaskDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String dateStr;
    private CourseDao courseDao;
    private TaskDao taskDao;
    private DayTimeFragmentDao dao;

    private List<DayTimeFragment> dayTimeFragmentList;
    private List<Map<String,String>> courseAndTaskList;
    private List<Boolean> isTitle;

    public ViewScheduleAdapter(Context context,String dataStr){
        this.context=context;
        this.dateStr=dataStr;
        this.courseDao = new CourseDao(context);
        this.taskDao = new TaskDao(context);
        this.dao = new DayTimeFragmentDao(context);
        this.courseAndTaskList = init();
    }
    public List<Map<String, String>> getCourseAndTaskList() {
        return courseAndTaskList;
    }

    public void setCourseAndTaskList(List<Map<String, String>> courseAndTaskList) {
        this.courseAndTaskList = courseAndTaskList;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View root = LayoutInflater.from(context).inflate(R.layout.viewschedule_item_time,parent,false);
            return new ViewScheduleItemHolder(root);
        }else if (viewType==1){
            View root= LayoutInflater.from(context).inflate(R.layout.viewschedule_item_schedule,parent,false);
            return new ViewScheduleHolder(root);
        }

        return null;
    }

    private int titleIndex = 0;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewScheduleHolder) {
            ((ViewScheduleHolder) holder).data.setText(courseAndTaskList.get(position-titleIndex).get("name"));
            ((ViewScheduleHolder) holder).desc.setText(courseAndTaskList.get(position-titleIndex).get("des"));
        } else {
            ((ViewScheduleItemHolder) holder).time.setText(this.dayTimeFragmentList.get(titleIndex).toString());
            titleIndex++;
        }
        if (position==getItemCount()-1) {
            this.titleIndex = 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isTitle.get(position) ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return courseAndTaskList.size()+dayTimeFragmentList.size();
    }

    class ViewScheduleHolder extends RecyclerView.ViewHolder {
        TextView data;
        TextView desc;

        public ViewScheduleHolder(View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.tv_data);
            desc = itemView.findViewById(R.id.tv_desc);
        }
    }

    class ViewScheduleItemHolder extends RecyclerView.ViewHolder {
        TextView time;

        public ViewScheduleItemHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.viewScheduleFragment_time);
        }
    }

    public List<Map<String,String>> init(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        int weekDay = 1;
        try {
            date = sdf.parse(dateStr);
            weekDay = dayForWeek(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Course> courseList = courseDao.selectByWeekDay(weekDay);
        List<Task> taskList = taskDao.selectDay(date);
        List<Map<String,String>> courseAndTaskList = new ArrayList<Map<String, String>>();
       /* if(courseList!=null&&courseList.size()!=0){
            for (int i = 0; i < courseList.size(); i++) {
                Course course = courseList.get(i);
                Map<String,String> courseMap = new HashMap<String, String>();
                courseMap.put("name",course.getClassname());
                courseMap.put("des",course.getAddress());
                courseAndTaskList.add(courseMap);
            }
        }

        if(taskList!=null&&taskList.size()!=0){
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                Map<String,String> taskMap = new HashMap<String, String>();
                taskMap.put("name",task.getName());
                taskMap.put("des",task.getTimeFragment().toString());
                courseAndTaskList.add(taskMap);
                task.getTimeFragment();
            }
        }*/

        //给时间段和日程加boolean标识
        isTitle=new ArrayList<Boolean>();
        dayTimeFragmentList=dao.selectAll();
        if(dayTimeFragmentList!=null&&dayTimeFragmentList.size()!=0) {
            for (int i = 0; i < dayTimeFragmentList.size(); i++) {
                isTitle.add(true);
                if(courseList!=null&&courseList.size()!=0){
                    for (int j = 0; j < courseList.size(); j++) {
                        Course course = courseList.get(j);
                        if (course.getRow()-1 == i) {
                            Map<String,String> courseMap = new HashMap<String, String>();
                            courseMap.put("name",course.getClassname());
                            courseMap.put("des",course.getAddress());
                            courseAndTaskList.add(courseMap);
                            isTitle.add(false);
                        }
                    }
                }

                if(taskList!=null&&taskList.size()!=0){
                    for (int k = 0; k < taskList.size(); k++) {
                        Task task = taskList.get(k);
                        if (task.getTimeFragment().getId() == dayTimeFragmentList.get(i).getId()){
                            Map<String,String> taskMap = new HashMap<String, String>();
                            taskMap.put("name",task.getName());
                            taskMap.put("des",task.getTimeFragment().toString());
                            courseAndTaskList.add(taskMap);
                            isTitle.add(false);
                        }
                    }
                }

            }
        }

        return courseAndTaskList;

    }

    //判断当前日期是周几(周日返回7)
    public static int dayForWeek(Date date) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }


}
