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
import com.privateproject.agendamanage.module_weekTime.viewHolder.DayTimeViewHolder;
import com.privateproject.agendamanage.utils.PieChartView;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.List;
import java.util.zip.Inflater;

public class DayTimeSelectActivity extends AppCompatActivity {
    public static final int RESULTCODE_CREATETIME = 1;

    private WeektimeActivityDayTimeSelectBinding timebinding;
    private DayTimeFragmentDao dao;

    public class DayTimeViewHolder{
        public Button leftBtn;
        public Button rightBtn;
        public PieChartView pieChart;
        public Button addBtn;
        public RaiflatButton cancelBtn;
        public RaiflatButton deleteBtn;
        public RecyclerView recyclerList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timebinding = WeektimeActivityDayTimeSelectBinding.inflate(LayoutInflater.from(this));
        setContentView(timebinding.getRoot());
        dao = new DayTimeFragmentDao(this);
        DayTimeViewHolder holder=new DayTimeViewHolder();
        //View v = LayoutInflater.from(DayTimeSelectActivity.this).inflate(R.layout.weektime_activity_day_time_select,null);
        holder.leftBtn=findViewById(R.id.daytime_select_arrow_left_botton);
        holder.rightBtn=findViewById(R.id.daytime_select_arrow_right_botton);
        holder.pieChart=findViewById(R.id.mPieChart);
        holder.cancelBtn=findViewById(R.id.daytime_select_back_botton);
        holder.deleteBtn=findViewById(R.id.daytime_select_delete_botton);
        holder.addBtn=findViewById(R.id.daytime_select_add_botton);
        holder.recyclerList=findViewById(R.id.daytime_select_recyclelist);

        // 使用intent来控制是否点击的是 返回 按钮
        Intent intent = getIntent();
        intent.putExtra("isBack", true);
        setResult(RESULTCODE_CREATETIME, intent);

        // 显示时间段的列表
        DayTimeSelectRecycleAdapter adapter = new DayTimeSelectRecycleAdapter(DayTimeSelectActivity.this,holder);
        holder.recyclerList.setAdapter(adapter);

        //添加按钮监听器
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayTimeSelectAddServer.TimeSelectAlerDialog(DayTimeSelectActivity.this,dao,adapter,-1);
            }
        });
        // 返回 按钮监听器
        holder.leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 确定 按钮监听器
        holder.rightBtn.setOnClickListener(new View.OnClickListener() {
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