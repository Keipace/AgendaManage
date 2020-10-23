package com.privateproject.agendamanage.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.privateproject.agendamanage.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static final String DEFAULT_DECORATION = "无";
    public static final int DEFAULT_FREQUENCY = 1;
    public static final String DEFAULT_TIMEFRAGMENTSTART = "-1";
    public static final String DEFAULT_TIMEFRAGMENTEND = "-1";
    public static final int DEFAULT_PLANCOUNTS = -1;
    public static final int DEFAULT_DONECOUNTS = -1;

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
    @DatabaseField(columnName = "decoration", canBeNull = false)
    private String decoration;
    @DatabaseField(columnName = "frequency", canBeNull = false)
    private int frequency;
    @DatabaseField(columnName = "time_fragment_start", canBeNull = false)
    private String timeFragmentStart;
    @DatabaseField(columnName = "time_fragment_end", canBeNull = false)
    private String timeFragmentEnd;
    @DatabaseField(columnName = "plan_counts", canBeNull = false)
    private int planCounts;
    @DatabaseField(columnName = "done_counts", canBeNull = false)
    private int doneCounts;

    private DayTarget() {}

    /*只有name的初始化*/
    public DayTarget(String name) {
        this.name = name;
        this.timeFragmentStart = DEFAULT_TIMEFRAGMENTSTART;
        this.timeFragmentEnd = DEFAULT_TIMEFRAGMENTEND;
        this.planCounts = DEFAULT_PLANCOUNTS;
        this.doneCounts = DEFAULT_DONECOUNTS;
        this.decoration = DEFAULT_DECORATION;
        this.frequency = DEFAULT_FREQUENCY;
    }

    // decoration使用默认值“无”，frequency使用默认值1
    public DayTarget(String name, String timeFragmentStart, String timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
        this.decoration = DEFAULT_DECORATION;
        this.frequency = DEFAULT_FREQUENCY;
    }

    // decoration使用自定义值，frequency使用默认值1
    public DayTarget(String name, String decoration, String timeFragmentStart, String timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.decoration = decoration;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
        this.frequency = DEFAULT_FREQUENCY;
    }

    // decoration使用默认值“无”， frequency使用自定义值
    public DayTarget(String name, int frequency, String timeFragmentStart, String timeFragmentEnd, int planCounts, int doneCounts) {
        this.name = name;
        this.frequency = frequency;
        this.timeFragmentStart = timeFragmentStart;
        this.timeFragmentEnd = timeFragmentEnd;
        this.planCounts = planCounts;
        this.doneCounts = doneCounts;
        this.decoration = DEFAULT_DECORATION;
    }

    // decoration使用自定义值， frequency使用自定义值
    public DayTarget(String name, String decoration, int frequency, String timeFragmentStart, String timeFragmentEnd, int planCounts, int doneCounts) {
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

    public String getTimeFragmentStart() {
        return timeFragmentStart;
    }

    public void setTimeFragmentStart(String timeFragmentStart) {
        this.timeFragmentStart = timeFragmentStart;
    }

    public String getTimeFragmentEnd() {
        return timeFragmentEnd;
    }

    public void setTimeFragmentEnd(String timeFragmentEnd) {
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
