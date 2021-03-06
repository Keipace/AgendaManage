package com.privateproject.agendamanage.module_weekTime.server;

import android.content.Context;
import android.content.SharedPreferences;

import com.privateproject.agendamanage.db.bean.Course;
import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.dao.TaskDao;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.privateproject.agendamanage.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 计算从dateStart开始到dateEnd几天内的应急时间量
 * 返回应急时间量的Map，Map中的key表示距离dateStart几天即偏移量，表示dateStart当天时key为0；value表示这一天有多少的分钟的应急时间量
 * Map中只存有应急时间量的那一天，不存没有应急时间量的天
 *
 * 比如：从2020-10-01到2020-10-31这段时间的应急时间量有
 * 2020-10-01:15min、2020-10-04:60min、2020-10-11:75min
 * 则Map中存有<0,15>、<3,60>、<10,75>*/
public class EverydayTotalTimeServer {
    public static final String KEY_EMERGENCY_START = "emergencyStartDate";
    public static final String KEY_STUDY_TIME = "studyTime";
    public static final String KEY_EMERGENCY_TIME = "emergencyTime";
    public static final String FILENAME_PREFERENCE = "TimeServer";

    private Context context;
    private DayTimeFragmentDao dayTimeFragmentDao;
    private CourseDao courseDao;
    private TaskDao taskDao;

    public EverydayTotalTimeServer(Context context) {
        this.context = context;
        this.dayTimeFragmentDao = new DayTimeFragmentDao(context);
        this.courseDao = new CourseDao(context);
        this.taskDao = new TaskDao(context);
        this.totalTimes = totalTime();
    }

    // 计算总时间量
    private List<Integer> totalTimes;
    public List<Integer> totalTime() {
        List<DayTimeFragment> dayTimeFragmentList = this.dayTimeFragmentDao.selectAll();

        if (dayTimeFragmentList == null || dayTimeFragmentList.size() == 0) {
            //时间段列表为空时，返回null
            this.totalTimes = null;
            return null;
        } else {
            List<Course> courseList = this.courseDao.selectAll();
            List<Integer> totalTimeList = new ArrayList<Integer>();
            //计算时间段列表中时间段的总时间（分钟）
            int totalTimeSelect = 0;
            for (int i = 0; i < dayTimeFragmentList.size(); i++) {
                totalTimeSelect += Time.parseTime(dayTimeFragmentList.get(i).getEnd()).
                        subOfMinute(Time.parseTime(dayTimeFragmentList.get(i).getStart()));
            }
            //设置返回的时间列表的初始值
            for (int i = 0; i < 7; i++) {
                totalTimeList.add(totalTimeSelect);
            }
            //日程表为空时，直接返回初始值
            if (courseList != null && courseList.size() != 0) {
                //依次遍历日程列表，并计算判断在星期几的那个时间段，减去这个时间段的时间
                for (int i = 0; i < courseList.size(); i++) {
                    // 星期几
                    int col = courseList.get(i).getCol();
                    // 该课程在第row个时间段，计算该时间段的时间量
                    int row = courseList.get(i).getRow()-1;
                    int timeFragment = Time.parseTime(dayTimeFragmentList.get(row).getEnd()).
                            subOfMinute(Time.parseTime(dayTimeFragmentList.get(row).getStart()));
                    switch (col) {
                        case 1:
                            //星期一
                            //判断在时间段列表的那个位置，下面同理
                            totalTimeList.set(0, totalTimeList.get(0) - timeFragment);
                            break;
                        case 2:
                            //星期二
                            totalTimeList.set(1, totalTimeList.get(1) - timeFragment);
                            break;
                        case 3:
                            //星期三
                            totalTimeList.set(2, totalTimeList.get(2) - timeFragment);
                            break;
                        case 4:
                            //星期四
                            totalTimeList.set(3, totalTimeList.get(3) - timeFragment);
                            break;
                        case 5:
                            //星期五
                            totalTimeList.set(4, totalTimeList.get(4) - timeFragment);
                            break;
                        case 6:
                            //星期六
                            totalTimeList.set(5, totalTimeList.get(5) - timeFragment);
                            break;
                        case 7:
                            //星期天
                            totalTimeList.set(6, totalTimeList.get(6) - timeFragment);
                            break;
                    }
                }
            }
            this.totalTimes = totalTimeList;
            return totalTimeList;
        }
    }

    // 从星期几开始偏移
    private int weekDayStartIndex;
    public void setWeekDayStartIndex(Date startDate) {
        try {
            this.weekDayStartIndex = dayForWeek(startDate)-1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalTimeOfDay(int offsetCount) {
        int weekDay = (this.weekDayStartIndex+offsetCount)%7;
        return this.totalTimes.get(weekDay);
    }

    // 设置应急时间量的比例,并保存到文件中
    public void setEmergency(int studyInt, int emergencyInt) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_STUDY_TIME, studyInt);
        editor.putInt(KEY_EMERGENCY_TIME, emergencyInt);
        editor.commit();
    }

    /*
    * 计算从dateStart开始到dateEnd几天内的应急时间量
    * 返回应急时间量的Map，Map中的key表示距离dateStart几天即偏移量，表示dateStart当天时key为0；value表示这一天有多少的分钟的应急时间量
    * Map中只存有应急时间量的那一天，不存没有应急时间量的天
    *
    * 比如：从2020-10-01到2020-10-31这段时间的应急时间量有
    * 2020-10-01:15min、2020-10-04:60min、2020-10-11:75min
    * 则Map中存有<0,15>、<3,60>、<10,75>*/
    public Map<Integer, Integer> emergencyTime(Date dateStart, Date dateEnd) {
        // 获取存储在文件中 应急时间比例
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME_PREFERENCE, Context.MODE_PRIVATE);
        int emergencyTime = sharedPreferences.getInt(KEY_EMERGENCY_TIME, -1);
        int studyTime = sharedPreferences.getInt(KEY_STUDY_TIME, -1);
        if (emergencyTime==-1 || studyTime==-1) {
            throw new RuntimeException("请先设置应急时间的比例");
        }

        // 计算两个时间相差的天数 左右都包含
        int day = differentDays(dateStart, dateEnd);
        // 存计算的应急时间的结果， key为偏移量， value为该天的应急时间量
        Map<Integer, Integer> emergencyTimeMap = new HashMap<Integer, Integer>();
        // 设置 获取每天总时间量时 偏移量对应的起始时间
        setWeekDayStartIndex(dateStart);

        int timeCell = emergencyTime + studyTime;
        int start = 0;
        int sumtime = 0;
        for (int i = 0; i < day; i++) {
            sumtime += getTotalTimeOfDay(i);
            // 累计的时间量已经够比例了
            if (sumtime >= timeCell) {
                //如果最后一天时间不够应急时间量
                if (getTotalTimeOfDay(i) < emergencyTime) {
                    //如果最后一天不够应急时间则往前分
                    emergencyTimeMap.put(i,getTotalTimeOfDay(i)-sumtime+timeCell);
                    int timeNeed = emergencyTime-(getTotalTimeOfDay(i)-sumtime+timeCell);//还需要分的应急时间
                    int dayNeed = i-1;//分到第几天了
                    while (timeNeed > 0) {
                        if (getTotalTimeOfDay(dayNeed) >= timeNeed) {
                            emergencyTimeMap.put(dayNeed, timeNeed);
                            break;
                        } else {
                            int time = getTotalTimeOfDay(dayNeed);
                            if (time!=0){ // 防止时间量为0的记录到应急时间量结果中
                                emergencyTimeMap.put(dayNeed, time);
                                timeNeed = timeNeed - time;
                            }
                        }
                        dayNeed = dayNeed - 1;
                    }
                    sumtime = sumtime-timeCell;
                } else {//如果当天应急时间量够
                    //此时需需判断是否超过timeCell
                    emergencyTimeMap.put(i, emergencyTime);
                    sumtime = sumtime - timeCell;
                    while (sumtime>=timeCell){ // 防止一天可以分多次应急时间量
                        emergencyTimeMap.put(i, emergencyTimeMap.get(i)+emergencyTime);
                        sumtime = sumtime - timeCell;
                    }
                }
                start = start + 1;
            }
        }
        return emergencyTimeMap;
    }

    //返回日期列表
    public List<Date> dateList(Date surplusStartDate, Date surplusEndDate){
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

    //返回剩余时间量的集合
    public List<Integer> surplusTime(Date surplusStartDate, Date surplusEndDate){
        try {
            List<Integer> surplusTimeList = new ArrayList<Integer>();

            // 1.获取总时间量
            List<Integer> totalTime = totalTime();
            if (totalTime == null||totalTime.size() == 0){
                ToastUtil.newToast(context,"还未设置时间段，请先去设置时间段！");
                return null;
            }
            int thisday = dayForWeek(surplusStartDate)-1;
            for (int i = 0; i < TimeUtil.subDate(surplusStartDate, surplusEndDate)+1; i++) {
                surplusTimeList.add(totalTime.get(thisday));
                thisday++;
                if (thisday>=7) {
                    thisday %= 7;
                }
            }

            // 2.减去应急时间量
            Date emergencyStartDate = getEmergencyStartDate();
            if (emergencyStartDate.after(surplusStartDate)) {
                ToastUtil.newToast(context, "请设置应急时间的开始日期,不应晚于"+TimeUtil.getDate(surplusStartDate));
                return null;
            }
            int off = TimeUtil.subDate(surplusStartDate, emergencyStartDate);
            Map<Integer,Integer> emergencyTimeMap  = emergencyTime(emergencyStartDate,surplusEndDate);
            for (int i = 0; i < surplusTimeList.size(); i++) {
                if (emergencyTimeMap.containsKey(off+i)){
                    surplusTimeList.set(i,surplusTimeList.get(i)-emergencyTimeMap.get(off+i));
                }
            }

            // 3.减去已安排计划的时间
            List<Task> tasks = this.taskDao.selectDuringDay(surplusStartDate, surplusEndDate);
            for (int i = 0; i < tasks.size(); i++) {
                int index = TimeUtil.subDate(surplusStartDate, tasks.get(i).getDay());
                DayTimeFragment timeFragment = tasks.get(i).getTimeFragment();
                int usedTime = Time.parseTime(timeFragment.getEnd()).subOfMinute(Time.parseTime(timeFragment.getStart()));
                surplusTimeList.set(index, surplusTimeList.get(index)-usedTime);
            }

            return surplusTimeList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setEmergencyStartDate(String emergencyStartDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMERGENCY_START, emergencyStartDate);
        editor.commit();
    }

    public Date getEmergencyStartDate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME_PREFERENCE, Context.MODE_PRIVATE);
        return TimeUtil.getDate(sharedPreferences.getString(KEY_EMERGENCY_START, null));
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

            return timeDistance + (day2 - day1+1);
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
