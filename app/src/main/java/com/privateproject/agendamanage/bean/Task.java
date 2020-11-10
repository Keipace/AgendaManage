package com.privateproject.agendamanage.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Task {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String name;//名称
    @DatabaseField
    private String plan;//描述
    @DatabaseField(canBeNull = false)
    private int offsetDay;//日期偏移量
    @DatabaseField(foreign = true,canBeNull = false)
    private DayTimeFragment startTime;//开始时间点
    @DatabaseField(foreign = true,canBeNull = false)
    private DayTimeFragment endTime;//结束时间点

    private Task() {
        super();
    }

    public Task(String name, int offsetDay, DayTimeFragment startTime, DayTimeFragment endTime) {
        this.name = name;
        this.offsetDay = offsetDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(String name, String plan, int offsetDay, DayTimeFragment startTime, DayTimeFragment endTime) {
        this.name = name;
        this.plan = plan;
        this.offsetDay = offsetDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getOffsetDay() {
        return offsetDay;
    }

    public void setOffsetDay(int offsetDay) {
        this.offsetDay = offsetDay;
    }

    public DayTimeFragment getStartTime() {
        return startTime;
    }

    public void setStartTime(DayTimeFragment startTime) {
        this.startTime = startTime;
    }

    public DayTimeFragment getEndTime() {
        return endTime;
    }

    public void setEndTime(DayTimeFragment endTime) {
        this.endTime = endTime;
    }
}
