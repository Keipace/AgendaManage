package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.databinding.ActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.databinding.ItemDaytimeAddLayoutBinding;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.List;

public class DayTimeSelectActivity extends AppCompatActivity {
    public static final int RESULTCODE_CREATETIME = 1;

    private ActivityDayTimeSelectBinding timebinding;
    private DayTimeFragmentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timebinding = ActivityDayTimeSelectBinding.inflate(LayoutInflater.from(this));
        setContentView(timebinding.getRoot());
        dao = new DayTimeFragmentDao(this);
        Intent intent = getIntent();
        intent.putExtra("isBack", true);
        setResult(RESULTCODE_CREATETIME, intent);

        DayTimeSelectRecycleAdapter adapter = new DayTimeSelectRecycleAdapter(DayTimeSelectActivity.this,timebinding);
        adapter.setAdapter(adapter);
        timebinding.daytimeSelectRecyclelist.setAdapter(adapter);

        //添加按钮监听器
        timebinding.daytimeSelectAddBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao = new DayTimeFragmentDao(DayTimeSelectActivity.this);
                DayTimeSelectAddServer.TimeSelectAlerDialog(DayTimeSelectActivity.this,dao,adapter,true,-1);
            }
        });
        timebinding.daytimeSelectArrowLeftBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        timebinding.daytimeSelectArrowRightBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DayTimeFragment> dayTimeFragmentList = dao.selectAll();
                if (dayTimeFragmentList == null||dayTimeFragmentList.size() == 0){
                    ToastUtil.newToast(DayTimeSelectActivity.this,"时间段为空，请先设置时间段");
                }else {
                    intent.putExtra("isBack", false);
                    finish();
                }
            }
        });
    }

}