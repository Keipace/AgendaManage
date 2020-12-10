package com.privateproject.agendamanage.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.chartView.PieChartView;
import com.rey.material.widget.RadioButton;

public class DayTimeSelectViewHolder {

    public static class DayTimeSelectTextViewRecycleViewHolder extends RecyclerView.ViewHolder {
        public TextView RecycleTextView;
        public ConstraintLayout daytimeSelectContainer;
        public RadioButton daytimeSelectCheckBox;
        public PieChartView pieChartView;

        public DayTimeSelectTextViewRecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            RecycleTextView = itemView.findViewById(R.id.daytime_select_textview);
            daytimeSelectContainer = itemView.findViewById(R.id.daytime_select_container);
            daytimeSelectCheckBox = itemView.findViewById(R.id.daytime_select_checkbox);
            pieChartView = itemView.findViewById(R.id.mPieChart);

        }
    }
}
