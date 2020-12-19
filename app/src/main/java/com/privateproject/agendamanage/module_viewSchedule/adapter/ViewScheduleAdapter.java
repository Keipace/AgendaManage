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
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.TaskDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewScheduleAdapter extends RecyclerView.Adapter<ViewScheduleAdapter.ViewScheduleHolder> {
    private Context context;
    private String dateStr;
    private CourseDao courseDao;
    private TaskDao taskDao;
    private List<Map<String,String>> courseAndTaskList;
    public ViewScheduleAdapter(Context context,String dataStr){
        this.context=context;
        this.dateStr=dataStr;
        this.courseDao = new CourseDao(context);
        this.taskDao = new TaskDao(context);
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
    public ViewScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(context).inflate(R.layout.viewschedule_item_schedule,parent,false);
        return new ViewScheduleHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewScheduleHolder holder, int position) {
        holder.data.setText(courseAndTaskList.get(position).get("name"));
        holder.desc.setText(courseAndTaskList.get(position).get("des"));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return courseAndTaskList.size();
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

    public List<Map<String,String>> init(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        int weekDay = 1;
        try {
            date = sdf.parse(dateStr);
            weekDay = dayForWeek(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Course> courseList = courseDao.selectByWeekDay(weekDay);
//        List<Task> taskList = taskDao.selectDay(date);
        List<Map<String,String>> courseAndTaskList = new ArrayList<Map<String, String>>();
        if(courseList!=null&&courseList.size()!=0){
            for (int i = 0; i < courseList.size(); i++) {
                Course course = courseList.get(i);
                Map<String,String> courseMap = new HashMap<String, String>();
                courseMap.put("name",course.getClassname());
                courseMap.put("des",course.getAddress());
                courseAndTaskList.add(courseMap);
            }
        }
//        if(taskList!=null&&taskList.size()!=0){
//            for (int i = 0; i < taskList.size(); i++) {
//                Task task = taskList.get(i);
//                Map<String,String> taskMap = new HashMap<String, String>();
//                taskMap.put("name",task.getName());
//                taskMap.put("des",task.getTimeFragment().toString());
//                courseAndTaskList.add(taskMap);
//            }
//        }
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
