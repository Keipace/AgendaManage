package com.privateproject.agendamanage.db.bean;


import androidx.annotation.Nullable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class DayTimeFragment {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String start;
    @DatabaseField(canBeNull = false)
    private String end;

    public Integer getId(){
        return id;
    }

    public DayTimeFragment(String start, String end) {
        this.start = start;
        this.end = end;
    }

    private DayTimeFragment() {
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DayTimeFragment)
            return this.toString().equals(obj.toString());
        else
            return false;
    }

    @Override
    public String toString() {
        return start + '-' + end ;
    }
}
