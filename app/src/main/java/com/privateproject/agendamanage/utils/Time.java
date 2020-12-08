package com.privateproject.agendamanage.utils;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* Time对象封装小时数和分钟数
*
* 构造方法：
* Time(int hour, int minute)  自定义小时数和分钟数
* Time()  默认时间为00:00
* 提供set/get方法来设置或获取小时数和分钟数
*
* static getCurrentTime()：获取当前时间的Time对象
*
* equals()：判断两个时间是否相同
* clone()：复制一个相同的Time对象
*
* toString()：Time类型 --> hh:mm字符串
* static parseTime()：hh:mm字符串 --> Time类型
*
* before()：判断当前对象是否早于指定对象
* after()：判断当前对象是否晚于指定对象
* compareTo()：比较当前对象与指定对象
*
* 时间的减法，注意减法返回的是绝对值（即便一个早的时间减晚的时间也返回的是间隔的绝对值）
* sub()：返回间隔的Time类型，即将相差的小时数和分钟数包装成Time类型
* subOfHour()：返回间隔的小时数，不满一个小时返回0
* subOfMinute()：返回间隔的分钟数
*
* 时间的偏移，注意不会修改当前时间，而返回一个新的Time
* later()：在当前时间的基础上向后推迟hour和minute
* earlier()：在当前时间的基础上向前提前hour和minute
* offset()：在当前时间的基础上偏移hour和minute
* */
public class Time {

    private int hour;
    private int minute;

    public Time(int hour, int minute) {
        setHour(hour);
        setMinute(minute);
    }

    //默认构造方法返回的时间是 00:00
    public Time() {
        setHour(0);
        setMinute(0);
    }

    public int getHour() {
        return hour;
    }

    // 设置小时数，小时数只允许在0~23之间
    public void setHour(int hour) {
        if(hour<0 || hour>=24)
            throw new RuntimeException("小时数只允许在0~23之间");
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    // 设置分钟数，分钟数只允许在0~59之间
    public void setMinute(int minute) {
        if(minute<0 || hour>=60)
            throw new RuntimeException("分钟数只允许在0~59");
        this.minute = minute;
    }

    // 只有表示同一时间的两个Time对象才返回true
    @Override
    public boolean equals(@Nullable Object obj) {
        Time time = (Time)obj;
        return this.hour==time.getHour()&&this.minute==time.getMinute();
    }

    // toString方法返回 hh:mm 格式的字符串，不省略0
    @Override
    public String toString() {
        String tmp = "";
        if(this.hour <= 9) {
            tmp += "0"+this.hour;
        } else {
            tmp += this.hour;
        }
        tmp += ":";
        if(this.minute <= 9) {
            tmp += "0"+this.minute;
        } else {
            tmp += this.minute;
        }
        return tmp;
    }

    public String getString() {
        String tmp = "";
        if (this.hour!=0) {
            tmp += this.hour+"小时";
        }
        if (this.minute!=0) {
            tmp += this.minute+"分钟";
        }
        return tmp.equals("")?"0分钟":tmp;
    }

    // 将 hh:mm(不省略0) 格式的字符串转换为 Time类型的对象
    public static Time parseTime(String time) {
        Pattern pattern = Pattern.compile("^[0-2][0-9]:[0-5][0-9]$");
        Matcher matcher = pattern.matcher(time);
        if (!matcher.matches()) {
            throw new RuntimeException("时间格式不正确");
        }
        String[] tmp = time.split(":");
        int hour_tmp = Integer.parseInt(tmp[0]);
        int minute_tmp = Integer.parseInt(tmp[1]);
        return new Time(hour_tmp, minute_tmp);
    }

    // 判断 this 对象是否早于 when
    public boolean before(Time when) {
        if(when.getHour()>this.getHour() || (when.getHour()==this.getHour()&&when.getMinute()>this.getMinute()))
            return true;
        return false;
    }

    // 判断 this 对象是否晚于 when
    public boolean after(Time when) {
        if(when.getHour()<this.getHour() || (when.getHour()==this.getHour()&&when.getMinute()<this.getMinute()))
            return true;
        return false;
    }

    /*
    * 1.当 this 对象早于 t 对象时， 返回 -1
    * 2.当 this 对象晚于 t 对象时， 返回 1
    * 3.当 this 对象等于 t 对象时， 返回 0*/
    public int compareTo(Time t) {
        if(this.before(t))
            return -1;
        if(this.after(t))
            return 1;
        return 0;
    }

    // 获取当前时间的 Time 对象
    public static Time getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return parseTime(sdf.format(date));
    }

    // 获取两个时间的间距，并返回Time类型（注意1:20-2:00与2:00-1:20返回的都是00:40）
    public Time sub(Time time) {
        int hour_tmp, minute_tmp;
        if(this.before(time)) {
            hour_tmp = time.hour-this.hour;
            minute_tmp = time.minute-this.minute;
            if(minute_tmp<0) {
                hour_tmp--;
                minute_tmp += 60;
            }
            return new Time(hour_tmp, minute_tmp);
        } else if(this.after(time)) {
            hour_tmp = this.hour-time.hour;
            minute_tmp = this.minute-time.minute;
            if(minute_tmp<0) {
                hour_tmp--;
                minute_tmp += 60;
            }
            return new Time(hour_tmp, minute_tmp);
        } else {
            return new Time();
        }
    }

    // 获取两个时间相差的小时数，如果不满一个小时则返回0
    public int subOfHour(Time time) {
        if(this.before(time))
            return time.hour-this.hour;
        else if(this.after(time))
            return this.hour-time.hour;
        else
            return 0;
    }

    // 获取两个时间相差的分钟数
    public int subOfMinute(Time time) {
        if(this.before(time)) {
            return (time.hour-this.hour)*60 + time.minute-this.minute;
        } else if(this.after(time)) {
            return (this.hour-time.hour)*60 + this.minute-time.minute;
        } else {
            return 0;
        }
    }

    // 将 this 代表的时间向后推迟hour和minute后返回新的时间
    public Time later(int hour, int minute) {
        if(hour<0 || hour>=24 || minute<0 || minute>=60) {
            throw new RuntimeException("必须满足0<=hour<24和0<=minute<60");
        }
        int hour_tmp, minute_tmp;
        hour_tmp = this.hour+hour;
        minute_tmp = this.minute+minute;
        if (minute_tmp>=60) {
            hour_tmp++;
            minute_tmp -= 60;
        }
        if (hour_tmp>=24) {
            hour_tmp -= 24;
        }
        return new Time(hour_tmp, minute_tmp);
    }

    public Time later(Time time) {
        return later(time.getHour(), time.getMinute());
    }

    // 将 this 代表的时间向前提前hour和minute后返回新的时间
    public Time earlier(int hour, int minute) {
        if(hour<0 || hour>=24 || minute<0 || minute>=60) {
            throw new RuntimeException("必须满足0<=hour<24和0<=minute<60");
        }
        int hour_tmp, minute_tmp;
        hour_tmp = this.hour-hour;
        minute_tmp = this.minute-minute;
        if (minute_tmp<0) {
            hour_tmp--;
            minute_tmp += 60;
        }
        if (hour_tmp<0) {
            hour_tmp += 24;
        }
        return new Time(hour_tmp, minute_tmp);
    }

    public Time earlier(Time time) {
        return earlier(time.getHour(), time.getMinute());
    }

    // 根据isLater判断是向前或向后偏移hour和minute
    public Time offset(int hour, int minute, boolean isLater) {
        if(isLater) {
            return this.later(hour, minute);
        } else {
            return this.earlier(hour, minute);
        }
    }

    // 根据isLater判断是向前或向后偏移time指定的hour和minute
    public Time offset(Time time, boolean isLater) {
        if (isLater) {
            return this.later(time.getHour(), time.getMinute());
        } else {
            return this.earlier(time.getHour(), time.getMinute());
        }
    }

    @Override
    protected Time clone() throws CloneNotSupportedException {
        return new Time(this.getHour(), this.getMinute());
    }
}
