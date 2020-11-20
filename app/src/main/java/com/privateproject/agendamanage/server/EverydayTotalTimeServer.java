package com.privateproject.agendamanage.server;

import android.content.Context;

import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.utils.Time;

import java.util.ArrayList;
import java.util.List;

public class EverydayTotalTimeServer {

    private Context context;
    private DayTimeFragmentDao dayTimeFragmentDao;
    private CourseDao courseDao;

    public EverydayTotalTimeServer(Context context, DayTimeFragmentDao dayTimeFragmentDao, CourseDao courseDao) {
        this.context = context;
        this.dayTimeFragmentDao = new DayTimeFragmentDao(context);
        this.courseDao = new CourseDao(context);
    }

    public List<Integer> totalTime() {
        List<DayTimeFragment> dayTimeFragmentList = this.dayTimeFragmentDao.selectAll();
        List<Course> courseList = this.courseDao.selectAll();
        List<Integer> totalTimeList = new ArrayList<Integer>();

        if (dayTimeFragmentList == null || dayTimeFragmentList.size() == 0) {
            //时间段列表为空时，返回null
            return null;
        } else {
            //计算时间段列表中时间段的总时间（分钟）
            int totalTimeSelect = 0;
            for (int i = 0; i < dayTimeFragmentList.size(); i++) {
                totalTimeSelect = totalTimeSelect + Time.parseTime(dayTimeFragmentList.get(i).getEnd()).
                        subOfMinute(Time.parseTime(dayTimeFragmentList.get(i).getStart()));
            }
            //设置返回的时间列表的初始值
            for (int i = 0; i < 7; i++) {
                totalTimeList.add(totalTimeSelect);
            }
            //日程表为空时，直接返回初始值
            if (courseList == null || courseList.size() == 0) {
                return totalTimeList;
            } else {
                //依次遍历日程列表，并计算判断在星期几的那个时间段，减去这个时间段的时间
                for (int i = 0; i < courseList.size(); i++) {
                    //判断星期几
                    int count = courseList.get(i).getPosition()%8;
                    switch (count){
                        case 1:
                            //星期一
                            //判断在时间段列表的那个位置，下面同理
                            int Monday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(0,totalTimeList.get(0)-Time.parseTime(dayTimeFragmentList.get(Monday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Monday).getStart())));
                            totalTimeList.remove(1);
                            break;
                        case 2:
                            //星期二
                            int Tuesday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(1,totalTimeList.get(1)-Time.parseTime(dayTimeFragmentList.get(Tuesday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Tuesday).getStart())));
                            totalTimeList.remove(2);
                            break;
                        case 3:
                            //星期三
                            int Wednesday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(2,totalTimeList.get(2)-Time.parseTime(dayTimeFragmentList.get(Wednesday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Wednesday).getStart())));
                            totalTimeList.remove(3);
                            break;
                        case 4:
                            //星期四
                            int Thursday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(3,totalTimeList.get(3)-Time.parseTime(dayTimeFragmentList.get(Thursday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Thursday).getStart())));
                            totalTimeList.remove(4);
                            break;
                        case 5:
                            //星期五
                            int Friday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(4,totalTimeList.get(4)-Time.parseTime(dayTimeFragmentList.get(Friday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Friday).getStart())));
                            totalTimeList.remove(5);
                            break;
                        case 6:
                            //星期六
                            int Saturday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(5,totalTimeList.get(5)-Time.parseTime(dayTimeFragmentList.get(Saturday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Saturday).getStart())));
                            totalTimeList.remove(6);
                            break;
                        case 7:
                            //星期天
                            int Sunday = courseList.get(i).getPosition()/8-1;
                            totalTimeList.add(6,totalTimeList.get(6)-Time.parseTime(dayTimeFragmentList.get(Sunday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Sunday).getStart())));
                            totalTimeList.remove(7);
                            break;
                    }
                }
                return totalTimeList;
            }
        }
    }
}
