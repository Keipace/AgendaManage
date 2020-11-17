package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.privateproject.agendamanage.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.databinding.ActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.databinding.ItemDaytimeAddLayoutBinding;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

public class DayTimeSelectActivity extends AppCompatActivity {
    private ActivityDayTimeSelectBinding timebinding;
    private DayTimeFragmentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timebinding = ActivityDayTimeSelectBinding.inflate(LayoutInflater.from(this));
        setContentView(timebinding.getRoot());

        DayTimeSelectRecycleAdapter adapter = new DayTimeSelectRecycleAdapter(DayTimeSelectActivity.this,timebinding);
        adapter.setAdapter(adapter);
        timebinding.daytimeSelectRecyclelist.setLayoutManager(new LinearLayoutManager(DayTimeSelectActivity.this));
        timebinding.daytimeSelectRecyclelist.setAdapter(adapter);

        //添加按钮监听器
        timebinding.daytimeSelectAddBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao = new DayTimeFragmentDao(DayTimeSelectActivity.this);
                DayTimeSelectAddServer.TimeSelectAlerDialog(DayTimeSelectActivity.this,dao,adapter,true,-1);
            }
        });
    }

}