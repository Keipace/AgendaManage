package com.privateproject.agendamanage.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.privateproject.agendamanage.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* ID
* 名称
* 描述
* 开始时间
* 结束时间
* 天数
*
* 子节点
*   有子节点时指向多个PlanNode类
*   没有子节点时要指明估算的时间量
* */
@DatabaseTable
public class PlanNode {
    public static final String DEFAULT_DECORATION = "无";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String decoration;
    @DatabaseField
    private Date startTime;
    @DatabaseField
    private Date endTime;
    @DatabaseField
    private int duringDay;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Target topParent;

    // 子节点属性
    @DatabaseField(columnName = "hasChildren")
    private boolean hasChildren = true;
    @DatabaseField(columnName = "childrenIds")
    private String childrenIds = "";
    private List<PlanNode> children;
    @DatabaseField
    private int timeNeeded;

    private PlanNode() {}

    public PlanNode(String name, String startTime, String endTime) {
        this(name, DEFAULT_DECORATION, startTime, endTime);
    }

    public PlanNode(String name, String decoration, String startTime, String endTime) {
        this.name = name;
        this.decoration = decoration;
        setStartAndEndTime(startTime, endTime);
    }

    public PlanNode(String name, String startTime, String endTime, boolean hasChildren, Object children) {
        this(name, DEFAULT_DECORATION, startTime, endTime, hasChildren, children);
    }

    public PlanNode(String name, String decoration, String startTime, String endTime, boolean hasChildren, Object children) {
        this.name = name;
        this.decoration = decoration;
        setStartAndEndTime(startTime, endTime);
        setChildren(hasChildren, children);
    }

    public PlanNode(String name, String startTime, String endTime, Target topParent) {
        this(name, DEFAULT_DECORATION, startTime, endTime, topParent);
    }

    public PlanNode(String name, String decoration, String startTime, String endTime, Target topParent) {
        this.name = name;
        this.decoration = decoration;
        this.topParent = topParent;
        setStartAndEndTime(startTime, endTime);
    }

    public PlanNode(String name, String startTime, String endTime, boolean hasChildren, Object children, Target topParent) {
        this(name, DEFAULT_DECORATION, startTime, endTime, hasChildren, children, topParent);
    }

    public PlanNode(String name, String decoration, String startTime, String endTime, boolean hasChildren, Object children, Target topParent) {
        this.name = name;
        this.decoration = decoration;
        this.topParent = topParent;
        setStartAndEndTime(startTime, endTime);
        setChildren(hasChildren, children);
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
        if (decoration==null)
            this.decoration = DEFAULT_DECORATION;
        else
            this.decoration = decoration;
    }


    public void setStartAndEndTime(String startTime, String endTime) {
        if (startTime==null || endTime==null)
            throw new RuntimeException("开始时间或结束时间不能为空！");
        Date start = TimeUtil.getDate(startTime);
        Date end = TimeUtil.getDate(endTime);
        // 开始时间不能早于结束时间
        if (TimeUtil.compareDate(start, end) > 0) {
            throw new RuntimeException("开始时间不能早于结束时间");
        } else {
            this.startTime = start;
            this.endTime = end;
            this.duringDay = TimeUtil.subDate(this.startTime, this.endTime);
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getDuringDay() {
        return duringDay;
    }

    /*
    * 设置子节点
    * 如果没有子节点，则需要指定预估时间量，即children参数类型为Integer类型
    * 如果有子节点，则子节点指向多个PlanNode，即children参数类型为List<PlanNode>*/
    public void setChildren(boolean hasChildren, Object children) {
        if (hasChildren) {
            if (children != null && children instanceof List) {
                List<PlanNode> tmpChildren = (List<PlanNode>)children;
                this.children = tmpChildren;
                this.childrenIds = "";
                for (int i = 0; i < this.children.size(); i++) {
                    this.childrenIds += this.children.get(i).getId() + ",";
                }
            } else {
                throw new RuntimeException("传入参数的类型应该是List<PlanNode>类型");
            }
        } else {
            if (children != null && children instanceof Integer) {
                this.timeNeeded = (Integer)children;
            } else {
                throw new RuntimeException("传入参数的类型应该是Integer类型");
            }
        }
        this.hasChildren = hasChildren;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public int getTimeNeeded() {
        return timeNeeded;
    }

    public List<PlanNode> getChildren() {
        return children;
    }

    public String getChildrenIds() {
        return childrenIds;
    }

    public void setTopParent(Target topParent) {
        this.topParent = topParent;
    }

    public Target getTopParent() {
        return topParent;
    }
}
