package com.privateproject.agendamanage.module_weekTime.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.databinding.WeektimeActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.module_weekTime.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.module_weekTime.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.PieChartView;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DayTimeSelectActivity extends AppCompatActivity {
    public static final int RESULTCODE_CREATETIME = 1;

    private RaiflatButton addTimeButton, backButton, deleteTimeButton;
    private Button arrowLeftButton, arrRightButton;
    private RecyclerView selectTimeRecycleView;
    private PieChartView mPieChart;
    private DayTimeFragmentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weektime_activity_day_time_select);
        dao = new DayTimeFragmentDao(this);

        // 使用intent来控制是否点击的是 返回 按钮
        Intent intent = getIntent();
        intent.putExtra("isBack", true);
        setResult(RESULTCODE_CREATETIME, intent);

        //初始化组件
        arrowLeftButton = findViewById(R.id.daytime_select_arrow_left_botton);
        arrRightButton = findViewById(R.id.daytime_select_arrow_right_botton);

        addTimeButton = findViewById(R.id.daytime_select_add_botton);
        backButton = findViewById(R.id.daytime_select_back_botton);
        deleteTimeButton = findViewById(R.id.daytime_select_delete_botton);

        selectTimeRecycleView = findViewById(R.id.daytime_select_recyclelist);

        mPieChart = findViewById(R.id.mPieChart);

        List<RaiflatButton> buttons = new ArrayList<RaiflatButton>();
        buttons.add(addTimeButton);
        buttons.add(backButton);
        buttons.add(deleteTimeButton);

        // 显示时间段的列表
        DayTimeSelectRecycleAdapter adapter = new DayTimeSelectRecycleAdapter(DayTimeSelectActivity.this, buttons, mPieChart);
        selectTimeRecycleView.setAdapter(adapter);

        //添加按钮监听器
        addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayTimeSelectAddServer.TimeSelectAlerDialog(DayTimeSelectActivity.this,dao,adapter,-1);
            }
        });
        // 返回 按钮监听器
        arrowLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 确定 按钮监听器
        arrRightButton.setOnClickListener(new View.OnClickListener() {
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