package com.privateproject.agendamanage.module_weekTime.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.privateproject.agendamanage.R;

import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.module_weekTime.fragment.WeekTimeFragment;

import java.util.List;

public class WeekTimeActivity extends AppCompatActivity {
    public static final int REQUESTCODE_CREATETIME = 1;
    private WeekTimeFragment weekTimeFragment;
    private DayTimeFragmentDao dayTimeFragmentDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weektime_activity_week_time);

        //跳转按钮
        Button button = findViewById(R.id.WeekActivity_toTimeFrag_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeekTimeActivity.this, DayTimeSelectActivity.class);
                startActivity(intent);
            }
        });

        //返回按钮
        ImageButton imageButton=findViewById(R.id.iv_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        boolean isTimeFragmentNone = false;
        // 查询数据库判断是否有时间段
        dayTimeFragmentDao = new DayTimeFragmentDao(this);
        List<DayTimeFragment> dayTimeList = dayTimeFragmentDao.selectAll();
        if (dayTimeList == null||dayTimeList.size() == 0) {
            isTimeFragmentNone = true;
        }

        // 如果没有时间段
        if (isTimeFragmentNone) {
            Intent intent = new Intent(this, DayTimeSelectActivity.class);
            startActivityForResult(intent, REQUESTCODE_CREATETIME);
            // 如果有时间段
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_CREATETIME && resultCode == DayTimeSelectActivity.RESULTCODE_CREATETIME) {
            boolean isBack = data.getBooleanExtra("isBack", false);
            if (isBack) {
                // 选择时间段页面点击 返回 按钮
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWeekTime();
    }

    public void showWeekTime() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (weekTimeFragment == null) { //第一次显示课程表
            weekTimeFragment = new WeekTimeFragment();
            transaction.add(R.id.WeekActivity_container_contrainlayout, weekTimeFragment).commitAllowingStateLoss();
        } else{
            weekTimeFragment.refresh();
        }

    }
}
