package com.privateproject.agendamanage.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/*
DayTarget:
#   id 标识
    name 名称
#   decoration 描述
#   frequency 频率，以天为单位
    timeFragmentStart 每次时间段的开始时间
    timeFragmentEnd 每次时间段的结束时间
    planCounts 目标总次数
    doneCounts 已完成次数

其中除了decoration字段有默认值“无”、frequency有默认值“1”外，其余字段均需要设置值
# id：只有获取方法，不能手动对其进行设置
# decoration：默认值为“无”
# frequency：默认值为1
针对是否自定义decoration、frequency的值有4种构造方法
*/
@DatabaseTable(tableName = "day_target")
public class DayTarget {
    /*
    DayTarget:
        id 自动生成，自增，唯一并且不能为空
        name 不能为空
        decoration 默认值为“无”
        frequency 默认值为1
        timeFragmentStart 不能为空
        timeFragmentEnd 不能为空
        planCounts 不能为空
        doneCounts 不能为空
    */
    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, unique = true)
    private Integer id;
    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;
    @DatabaseField(columnName = "decoration", defaultValue = "无")
    private String decoration = "无";
    @DatabaseField(columnName = "frequency", canBeNull = false, defaultValue = "1")
    private int frequency = 1;
    @DatabaseField(columnName = "time_fragment_start", canBeNull = false)
    private Date timeFragmentStart;
    @DatabaseField(columnName = "time_fragment_end", canBeNull = false)
    private Date timeFragmentEnd;
    @DatabaseField(columnName = "plan_counts", canBeNull = false)
    private int planCounts;
    @DatabaseField(columnName = "done_counts", canBeNull = false)
    private int doneCounts;

    // decoration使用默认值“无”，frequency使用默认值1
    public DayTarget(String name, Date timeFragmentStart, Date timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
    }

    // decoration使用自定义值，frequency使用默认值1
    public DayTarget(String name, String decoration, Date timeFragmentStart, Date timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.decoration = decoration;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
    }

    // decoration使用默认值“无”， frequency使用自定义值
    public DayTarget(String name, int frequency, Date timeFragmentStart, Date timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.frequency = frequency;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
    }

    // decoration使用自定义值， frequency使用自定义值
    public DayTarget(String name, String decoration, int frequency, Date timeFragmentStart, Date timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.decoration = decoration;
        this.frequency = frequency;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
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

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Date getTimeFragmentStart() {
        return timeFragmentStart;
    }

    public void setTimeFragmentStart(Date timeFragmentStart) {
        this.timeFragmentStart = timeFragmentStart;
    }

    public Date getTimeFragmentEnd() {
        return timeFragmentEnd;
    }

    public void setTimeFragmentEnd(Date timeFragmentEnd) {
        this.timeFragmentEnd = timeFragmentEnd;
    }

    public int getPlanCounts() {
        return planCounts;
    }

    public void setPlanCounts(int planCounts) {
        this.planCounts = planCounts;
    }

    public int getDoneCounts() {
        return doneCounts;
    }

    public void setDoneCounts(int doneCounts) {
        this.doneCounts = doneCounts;
    }

    @Override
    public String toString() {
        return "DayTarget{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", decoration='" + decoration + '\'' +
                ", frequency=" + frequency +
                ", timeFragmentStart=" + timeFragmentStart +
                ", timeFragmentEnd=" + timeFragmentEnd +
                ", planCounts=" + planCounts +
                ", doneCounts=" + doneCounts +
                '}';
    }
}
