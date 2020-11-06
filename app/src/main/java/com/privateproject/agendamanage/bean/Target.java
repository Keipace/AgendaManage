package com.privateproject.agendamanage.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/*
Target 一次性目标
#   id 标识
    name 名称
#   decoration 描述
    timeNeed 预估所需时间
    timePlanOver 期待完成时间
    timeDeadLine 最晚完成时间
    timeRealOver 实际完成时间
#   importance 重要性
    timePreDo 预实施时间

其中除了decoration字段有默认值“无”外，其余字段均需要设置值
# id：只有获取方法，不能手动对其进行设置
# decoration：默认值为“无”
# importance：有三种值IMPORTANCE_HIGH、IMPORTANCE_MIDDLE、IMPORTANCE_LOW，分别使用本类中的常量对其赋值
针对是否自定义decoration的值而有两种构造方法
*/

@DatabaseTable(tableName = "target")
public class Target {
    public static final int IMPORTANCE_HIGH = 1;
    public static final int IMPORTANCE_MIDDLE = 2;
    public static final int IMPORTANCE_LOW = 3;

    public static final String DEFAULT_DECORATION = "无";
    public static final double DEFAULT_TIMENEED = -1.0d;
    public static final int DEFAULT_IMPORTANCE = -1;

    /*
    Target:
        id 自动生成，自增，唯一并且不能为空
        name 不能为空
        decoration 默认值为“无”
        timeNeed 不能为空
        timePlanOver 不能为空
        timeDeadLine 不能为空
        timeRealOver 不能为空
        importance 不能为空
        timePreDo 不能为空
    */
    @DatabaseField(columnName = "id", unique = true, generatedId = true, canBeNull = false)
    private Integer id;
    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;
    @DatabaseField(columnName = "decoration")
    private String decoration;
    @DatabaseField(columnName = "time_need", canBeNull = false)
    private double timeNeed;
    @DatabaseField(columnName = "time_planover")
    private Date timePlanOver;
    @DatabaseField(columnName = "time_deadline")
    private Date timeDeadLine;
    @DatabaseField(columnName = "time_realover")
    private Date timeRealOver;
    @DatabaseField(columnName = "importance", canBeNull = false)
    private int importance;
    @DatabaseField(columnName = "time_predo")
    private Date timePreDo;

    private Target() {}

    // 只有name初始化
    public Target(String name) {
        this(name, DEFAULT_DECORATION);
    }

    // 只有name和decoration的初始化
    public Target(String name, String decoration) {
        this.name = name;
        this.decoration = decoration;
        this.timeNeed = DEFAULT_TIMENEED;
        this.importance = DEFAULT_IMPORTANCE;
    }

    // decoration使用默认值“无”
    public Target(String name, double timeNeed, Date timePlanOver, Date timeDeadLine, Date timeRealOver, int importance, Date timePreDo) {
        this(name, DEFAULT_DECORATION, timeNeed, timePlanOver, timeDeadLine, timeRealOver, importance, timePreDo);
    }

    // decoration使用自定义值
    public Target(String name, String decoration, double timeNeed, Date timePlanOver, Date timeDeadLine, Date timeRealOver, int importance, Date timePreDo) {
        this.name = name;
        this.decoration = decoration;
        this.timeNeed = timeNeed;
        this.timePlanOver = timePlanOver;
        this.timeDeadLine = timeDeadLine;
        this.timeRealOver = timeRealOver;
        this.importance = importance;
        this.timePreDo = timePreDo;
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

    public double getTimeNeed() {
        return timeNeed;
    }

    public void setTimeNeed(double timeNeed) {
        this.timeNeed = timeNeed;
    }

    public Date getTimePlanOver() {
        return timePlanOver;
    }

    public void setTimePlanOver(Date timePlanOver) {
        this.timePlanOver = timePlanOver;
    }

    public Date getTimeDeadLine() {
        return timeDeadLine;
    }

    public void setTimeDeadLine(Date timeDeadLine) {
        this.timeDeadLine = timeDeadLine;
    }

    public Date getTimeRealOver() {
        return timeRealOver;
    }

    public void setTimeRealOver(Date timeRealOver) {
        this.timeRealOver = timeRealOver;
    }

    public String getImportance() {
        if (this.importance==IMPORTANCE_HIGH) {
            return "高";
        } else if (this.importance==IMPORTANCE_MIDDLE){
            return "中";
        } else {
            return "低";
        }
    }

    public int getImportanceRealNum() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public Date getTimePreDo() {
        return timePreDo;
    }

    public void setTimePreDo(Date timePreDo) {
        this.timePreDo = timePreDo;
    }

    @Override
    public String toString() {
        return "Target{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", decoration='" + decoration + '\'' +
                ", timeNeed=" + timeNeed +
                ", timePlanOver=" + timePlanOver +
                ", timeDeadLine=" + timeDeadLine +
                ", timeRealOver=" + timeRealOver +
                ", importance=" + importance +
                ", timePreDo=" + timePreDo +
                '}';
    }
}
