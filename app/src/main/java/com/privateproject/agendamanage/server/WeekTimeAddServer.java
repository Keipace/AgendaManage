package com.privateproject.agendamanage.server;

import android.content.Context;

import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.db.CourseDao;

public class WeekTimeAddServer {
    private Context context;
    private Course course;

    public WeekTimeAddServer(Context context) {
        this.context = context;
        this.course = new Course();
    }

    //添加一个Course
    public void addCoures(CourseDao courseDao, Integer position, String courseName, String address) {
        course.setPosition(position);
        course.setClassname(courseName);
        course.setAddress(address);
        courseDao.addCourse(course);
    }

}
