package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.LineChartBean;
import com.privateproject.agendamanage.server.EverydayTotalTimeServer;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimeLineChartActivity extends AppCompatActivity {
    private MaterialEditText startDate,endDate,emergencyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_chart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LineChart lineChart = findViewById(R.id.surplustime_line_chart);
        EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(TimeLineChartActivity.this);
        startDate = findViewById(R.id.surplustime_startdate_edittext);
        startDate.setText("2020-11-16");
        endDate = findViewById(R.id.surplustime_enddate_edittext);
        endDate.setText("2020-11-29");
        emergencyDate = findViewById(R.id.surplustime_emergencydate_edittext);
        emergencyDate.setText("2020-11-16");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获得者线图的开始日期和结束日期
        try {
            Date surplusStartDate = simpleDateFormat.parse(startDate.getText().toString());
            Date surplusEndDate = simpleDateFormat.parse(emergencyDate.getText().toString());
            Date emergencyStartDate = simpleDateFormat.parse(endDate.getText().toString());
            Map<String, Integer> surplusTimeMap = everydayTotalTimeServer.surplusTime(surplusStartDate,emergencyStartDate,surplusEndDate);
            List<Date> dateList = everydayTotalTimeServer.dateList(surplusStartDate,surplusEndDate);
            //判断map集合是否为空
            if (surplusTimeMap != null||surplusTimeMap.equals("")){

            }else {
                LineChartBean lineChartBean = new LineChartBean(lineChart,"剩余时间总量", Color.GREEN,surplusTimeMap,dateList);
                lineChartBean.setYAxis(16,0,10);
                lineChartBean.addEntry();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}