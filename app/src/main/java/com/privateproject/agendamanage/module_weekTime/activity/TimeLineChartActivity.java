package com.privateproject.agendamanage.module_weekTime.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.BarChartBean;
import com.privateproject.agendamanage.db.bean.LineChartBean;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeLineChartActivity extends AppCompatActivity {
    private MaterialEditText startDate,endDate,emergencyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weektime_activity_time_line_chart);

    }

    @Override
    protected void onResume() {
        super.onResume();

        EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(TimeLineChartActivity.this);
        startDate = findViewById(R.id.surplustime_startdate_edittext);
        startDate.setText("2020-12-07");
        endDate = findViewById(R.id.surplustime_enddate_edittext);
        endDate.setText("2020-12-13");
        emergencyDate = findViewById(R.id.surplustime_emergencydate_edittext);
        emergencyDate.setText("2020-12-07");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //设置日期选择框
        TimeUtil.setDateStartToEnd(TimeLineChartActivity.this,startDate,endDate,true);
        TimeUtil.setDateStartToEnd(TimeLineChartActivity.this,emergencyDate,startDate,true);

        Button lineChartBtn = findViewById(R.id.surplustime_line_chart_btn);
        lineChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得者线图的开始日期和结束日期
                try {
                    Date surplusStartDate = simpleDateFormat.parse(startDate.getText().toString());
                    Date surplusEndDate = simpleDateFormat.parse(endDate.getText().toString());
                    Date emergencyStartDate = simpleDateFormat.parse(emergencyDate.getText().toString());
                    List<Integer> surplusTimeList = everydayTotalTimeServer.surplusTime(surplusStartDate,emergencyStartDate,surplusEndDate);
                    List<Date> dateList = everydayTotalTimeServer.dateList(surplusStartDate,surplusEndDate);
                    //判断map集合是否为空
                    if (surplusTimeList == null||surplusTimeList.equals("")){
                        ToastUtil.newToast(TimeLineChartActivity.this,"必要数据未填充");
                    }else {
                        LineChart lineChart = findViewById(R.id.surplustime_line_chart);
                        lineChart.setVisibility(View.VISIBLE);
                        LineChartBean lineChartBean = new LineChartBean(lineChart,"剩余时间总量", Color.GREEN,surplusTimeList,dateList);
                        lineChartBean.setYAxis(1000,0,10);
                        lineChartBean.addEntry();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        Button barChartBtn = findViewById(R.id.surplustime_bar_chart_btn);
        barChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得柱状图的开始日期和结束日期
                try {
                    Date surplusStartDate = simpleDateFormat.parse(startDate.getText().toString());
                    Date surplusEndDate = simpleDateFormat.parse(endDate.getText().toString());
                    Date emergencyStartDate = simpleDateFormat.parse(emergencyDate.getText().toString());
                    List<Integer> surplusTimeList = everydayTotalTimeServer.surplusTime(surplusStartDate,emergencyStartDate,surplusEndDate);
                    List<Date> dateList = everydayTotalTimeServer.dateList(surplusStartDate,surplusEndDate);
                    //判断map集合是否为空
                    if (surplusTimeList == null||surplusTimeList.equals("")){
                        ToastUtil.newToast(TimeLineChartActivity.this,"必要数据未填充");
                    }else {
                        //x轴Map集合
                        Map<Integer,String> x = new HashMap<Integer, String>();
                        //y轴Map集合
                        Map<Integer,Integer> y = new HashMap<Integer, Integer>();
                        List<String> dateStrList = formatteDate(dateList);
                        //填充x轴数据
                        for (int i = 0; i < dateList.size(); i++) {
                            x.put(i,dateStrList.get(i));
                        }
                        //填充y轴数据
                        for(int i = 0; i < surplusTimeList.size(); i++){
                            y.put(i,surplusTimeList.get(i));
                        }
                        BarChart barChart = findViewById(R.id.surplustime_bar_chart);
                        barChart.setVisibility(View.VISIBLE);
                        BarChartBean barChartBean = new BarChartBean(barChart);
                        barChartBean.showBarChart(y,x,"剩余时间总量",Color.GREEN);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public List<String> formatteDate(List<Date> dateList){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        List<String> dateStrList = new ArrayList<String>();
        for (int i = 0; i < dateList.size(); i++) {
            String date = format.format(dateList.get(i));
            dateStrList.add(date);
        }
        return dateStrList;
    }
}