package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.privateproject.agendamanage.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.databinding.ActivityDayTimeSelectBinding;

public class DayTimeSelectActivity extends AppCompatActivity {
    private ActivityDayTimeSelectBinding timebinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timebinding = ActivityDayTimeSelectBinding.inflate(LayoutInflater.from(this));
        setContentView(timebinding.getRoot());

        DayTimeSelectRecycleAdapter adapter = new DayTimeSelectRecycleAdapter(DayTimeSelectActivity.this);
        adapter.setAdapter(adapter);
        timebinding.daytimeSelectRecyclelist.setLayoutManager(new LinearLayoutManager(DayTimeSelectActivity.this));
        timebinding.daytimeSelectRecyclelist.setAdapter(adapter);
    }

}