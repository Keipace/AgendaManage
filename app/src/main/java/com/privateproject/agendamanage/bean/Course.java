package com.privateproject.agendamanage.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "course")
public class Course {
    //事件、地点
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String classname;//事件
    @DatabaseField
    private String address;//地点
    @DatabaseField(canBeNull = false)
    private int col;//元素所在列，代表星期几
    @DatabaseField(canBeNull = false)
    private int row;// 元素所在行，代表第几个时间段

    private Course() {}

    public Course(String classname, int col, int row) {
        this.classname = classname;
        this.col = col;
        this.row = row;
    }

    public Course(String classname, String address, int row, int col) {
        this.classname = classname;
        this.address = address;
        this.col = col;
        this.row = row;
    }

    public Integer getId() {
        return id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return classname + '\n' +address +'\n' +"------ "+'\n';
    }
}
