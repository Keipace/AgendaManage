package com.privateproject.agendamanage.module_weekTime.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.privateproject.agendamanage.databinding.WeektimeActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.module_weekTime.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.module_weekTime.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.List;

public class DayTimeSelectActivity extends AppCompatActivity {
    public static final int RESULTCODE_CREATETIME = 1;

    private WeektimeActivityDayTimeSelectBinding timebinding;
    private DayTimeFragmentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timebinding = WeektimeActivityDayTimeSelectBinding.inflate(LayoutInflater.from(this));
        setContentView(timebinding.getRoot());
        dao = new DayTimeFragmentDao(this);

        // 使用intent来控制是否点击的是 返回 按钮
        Intent intent = getIntent();
        intent.putExtra("isBack", true);
        setResult(RESULTCODE_CREATETIME, intent);

        // 显示时间段的列表
        DayTimeSelectRecycleAdapter adapter = new DayTimeSelectRecycleAdapter(DayTimeSelectActivity.this,timebinding);
        timebinding.daytimeSelectRecyclelist.setAdapter(adapter);

        //添加按钮监听器
        timebinding.daytimeSelectAddBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayTimeSelectAddServer.TimeSelectAlerDialog(DayTimeSelectActivity.this,dao,adapter,-1);
            }
        });
        // 返回 按钮监听器
        timebinding.daytimeSelectArrowLeftBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 确定 按钮监听器
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