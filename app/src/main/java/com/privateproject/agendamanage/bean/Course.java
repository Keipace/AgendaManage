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
    private Integer position;//元素所在位置

    public Course() {

    }

    public Course(Integer id, String classname, Integer position) {
        this.id = id;
        this.classname = classname;
        this.position = position;
    }

    public Course(Integer id, String classname, String address, Integer position) {
        this.id = id;
        this.classname = classname;
        this.address = address;
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return classname + '\n' +address +'\n' +"------ "+'\n';
    }
}
