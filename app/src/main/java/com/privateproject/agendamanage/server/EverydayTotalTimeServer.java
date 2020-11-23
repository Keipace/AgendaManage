package com.privateproject.agendamanage.server;

import android.content.Context;

import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.utils.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EverydayTotalTimeServer {

    private Context context;
    private DayTimeFragmentDao dayTimeFragmentDao;
    private CourseDao courseDao;

    private int emergencyTime;
    private int studyTime;

    public EverydayTotalTimeServer(Context context) {
        this.context = context;
        this.dayTimeFragmentDao = new DayTimeFragmentDao(context);
        this.courseDao = new CourseDao(context);
    }

    public int getEmergencyTime() {
        return emergencyTime;
    }

    public void setEmergencyTime(int emergencyTime) {
        this.emergencyTime = emergencyTime;
    }

    public int getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(int studyTime) {
        this.studyTime = studyTime;
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
                    int count = courseList.get(i).getPosition() % 8;
                    switch (count) {
                        case 1:
                            //星期一
                            //判断在时间段列表的那个位置，下面同理
                            int Monday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(0, totalTimeList.get(0) - Time.parseTime(dayTimeFragmentList.get(Monday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Monday).getStart())));
                            totalTimeList.remove(1);
                            break;
                        case 2:
                            //星期二
                            int Tuesday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(1, totalTimeList.get(1) - Time.parseTime(dayTimeFragmentList.get(Tuesday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Tuesday).getStart())));
                            totalTimeList.remove(2);
                            break;
                        case 3:
                            //星期三
                            int Wednesday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(2, totalTimeList.get(2) - Time.parseTime(dayTimeFragmentList.get(Wednesday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Wednesday).getStart())));
                            totalTimeList.remove(3);
                            break;
                        case 4:
                            //星期四
                            int Thursday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(3, totalTimeList.get(3) - Time.parseTime(dayTimeFragmentList.get(Thursday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Thursday).getStart())));
                            totalTimeList.remove(4);
                            break;
                        case 5:
                            //星期五
                            int Friday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(4, totalTimeList.get(4) - Time.parseTime(dayTimeFragmentList.get(Friday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Friday).getStart())));
                            totalTimeList.remove(5);
                            break;
                        case 6:
                            //星期六
                            int Saturday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(5, totalTimeList.get(5) - Time.parseTime(dayTimeFragmentList.get(Saturday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Saturday).getStart())));
                            totalTimeList.remove(6);
                            break;
                        case 7:
                            //星期天
                            int Sunday = courseList.get(i).getPosition() / 8 - 1;
                            totalTimeList.add(6, totalTimeList.get(6) - Time.parseTime(dayTimeFragmentList.get(Sunday).getEnd()).
                                    subOfMinute(Time.parseTime(dayTimeFragmentList.get(Sunday).getStart())));
                            totalTimeList.remove(7);
                            break;
                    }
                }
                return totalTimeList;
            }
        }
    }


    //返回应急时间列表
    public Map<Integer, Integer> emergencyTime(Date dateStart, Date dateEnd) {
        //第一天为周几
        int startWeekDay = 0;
        //最后一天为周几
        int endWeekDay = 0;
        try {
            startWeekDay = dayForWeek(dateStart);
            endWeekDay = dayForWeek(dateEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //day为安排剩余时间的天数
        int day = differentDays(dateStart, dateEnd);
        //应急时间量列表(第一个为天数偏移量，第二个为小时)
        Map<Integer, Integer> emergencyTimeMapList = new HashMap<>();
        //每周时间量列表
        List<Integer> totalWeekTimeList = totalTime();
        //day天内的时间量列表
        List<Integer> totalTimeList = new ArrayList<Integer>();
        if(day<=7){
                for (int i=0;i<day;i++){
                    totalTimeList.add(totalWeekTimeList.get(((startWeekDay-1)+i)%7));
                }

        }else {
            //加入第一周时间量列表
            for (int i = startWeekDay - 1; i < 7; i++) {
                totalTimeList.add(totalWeekTimeList.get(i));
            }
            //开始周和最后周的天数
            int startAndEndWeekDay = endWeekDay + (8 - startWeekDay);
            //除去开始周和最后一周的总周数
            int weekNum = (day - startAndEndWeekDay) / 7;
            for (int i = 0; i < weekNum; i++) {
                totalTimeList.addAll(totalWeekTimeList);
            }
            //加入最后一周时间量列表
            for (int i = 0; i < endWeekDay; i++) {
                totalTimeList.add(totalWeekTimeList.get(i));
            }
        }



        //分配时间段
        int timeCell = emergencyTime + studyTime;
        //分段开始天
        int start = 0;
        //记录超过emergencyTime的最新的一天
        int lastEnough = 0;
        //已记录的时间总量
        int sumtime = 0;
        for (int i = 0; i < totalTimeList.size(); i++) {

            sumtime += totalTimeList.get(i);

            if (totalTimeList.get(i) >= emergencyTime) {
                lastEnough = i;
            }
            if (sumtime >= timeCell) {
                //如果最后一天时间不够应急事件(则看前面有没有哪天够)
                if (totalTimeList.get(i) < emergencyTime) {

                    //如果lastEnough为第一天
                    if (lastEnough == start) {
                        //如果第一天够应急时间
                        if (totalTimeList.get(start) >= emergencyTime) {
                            emergencyTimeMapList.put(start, emergencyTime);
                        } else {
                            //如果第一天不够应急时间则往前分
                            int timeNeed = emergencyTime;//还需要分的应急时间
                            int dayNeed = i;//分到第几天了
                            while (timeNeed != 0) {
                                if (totalTimeList.get(dayNeed) >= timeNeed) {
                                    emergencyTimeMapList.put(dayNeed, timeNeed);
                                    break;
                                } else {
                                    if (totalTimeList.get(dayNeed)!=0){
                                        emergencyTimeMapList.put(dayNeed, totalTimeList.get(dayNeed));
                                        timeNeed = timeNeed - totalTimeList.get(dayNeed);
                                    }
                                }
                                dayNeed = dayNeed - 1;
                            }

                        }
                    } else {
                        //如果lastEnough不为第一天
                        emergencyTimeMapList.put(lastEnough, emergencyTime);
                    }

                    sumtime = 0;
                } else {//如果当天应急时间量够
                    //此时需需判断是否超过timeCell
                    emergencyTimeMapList.put(i, emergencyTime);
                    sumtime = totalTimeList.get(i) - emergencyTime;
                    if(sumtime>timeCell){
                        while (sumtime>=timeCell){
                            emergencyTimeMapList.put(i, emergencyTime);
                            sumtime = sumtime - timeCell;
                        }
                    }
                }
                start = start + 1;
                lastEnough = start;
            }
        }
        return emergencyTimeMapList;
    }





    /**
     * date2比date1多的天数
     *
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static int differentDays(Date dateStart, Date dateEnd) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dateStart);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dateEnd);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //不同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //同一年
        {
            return day2 - day1+1;
        }
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
