package com.privateproject.agendamanage.bean;


import androidx.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class DayTimeFragment {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String timePoint;
    @DatabaseField(canBeNull = false, columnName = "order")
    private int order;

    public Integer getId(){
        return id;
    }

    public DayTimeFragment(int order, String timePoint) {
        this.order = order;
        this.timePoint = timePoint;
    }

    private DayTimeFragment() {
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this.timePoint.equals(obj.toString());
    }

    @Override
    public String toString() {
        return this.timePoint;
    }
}
