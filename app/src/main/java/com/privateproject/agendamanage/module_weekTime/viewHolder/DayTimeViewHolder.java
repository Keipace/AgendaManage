package com.privateproject.agendamanage.module_weekTime.viewHolder;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.utils.PieChartView;

public class DayTimeViewHolder{
    public Button leftBtn;
    public Button rightBtn;
    public PieChartView pieChart;
    public Button addBtn;
    public RaiflatButton cancelBtn;
    public RaiflatButton deleteBtn;
    public RecyclerView recyclerList;

    public DayTimeViewHolder(View view) {
        leftBtn=view.findViewById(R.id.daytime_select_arrow_left_botton);
        rightBtn=view.findViewById(R.id.daytime_select_arrow_right_botton);
        pieChart=view.findViewById(R.id.mPieChart);
        cancelBtn=view.findViewById(R.id.daytime_select_back_botton);
        deleteBtn=view.findViewById(R.id.daytime_select_delete_botton);
        addBtn=view.findViewById(R.id.daytime_select_add_botton);
        recyclerList=view.findViewById(R.id.daytime_select_recyclelist);
    }
}
