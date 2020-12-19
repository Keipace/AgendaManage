package com.privateproject.agendamanage.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.privateproject.agendamanage.utils.TimeUtil;

import java.util.Date;

@DatabaseTable
public class Task {
    public static final String DEFAULT_PLAN = "无";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String name;//名称
    @DatabaseField
    private String plan;//描述
    @DatabaseField(canBeNull = false)
    private Date day;//所在日期
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DayTimeFragment timeFragment;//所在时间段
    @DatabaseField(canBeNull = false)
    private Integer mintime;//所需分钟


    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PlanNode parent;

    public Task() {
        super();
    }

    public Task(String name, String day, DayTimeFragment timeFragment, int mintime,PlanNode parent) {
        this(name, DEFAULT_PLAN, day, timeFragment, mintime, parent);
    }

    public Task(String name, String plan, String day, DayTimeFragment timeFragment, int mintime, PlanNode parent) {
        this.name = name;
        this.plan = plan;
        this.day = TimeUtil.getDate(day);
        this.timeFragment = timeFragment;
        this.mintime = mintime;
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public void setDay(String day) {
        this.day = TimeUtil.getDate(day);
    }

    public Date getDay() {
        return day;
    }

    public DayTimeFragment getTimeFragment() {
        return timeFragment;
    }

    public void setTimeFragment(DayTimeFragment timeFragment) {
        this.timeFragment = timeFragment;
    }

    public PlanNode getParent() {
        return parent;
    }
    public Integer getMintime() {
        return mintime;
    }

    public void setMintime(Integer mintime) {
        this.mintime = mintime;
    }
}
