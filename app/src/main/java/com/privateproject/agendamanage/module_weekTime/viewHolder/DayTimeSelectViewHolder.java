package com.privateproject.agendamanage.module_weekTime.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.utils.PieChartView;
import com.rey.material.widget.RadioButton;

public class DayTimeSelectViewHolder {

    public static class DayTimeSelectTextViewRecycleViewHolder extends RecyclerView.ViewHolder {
        public TextView RecycleTextView;
        public ConstraintLayout daytimeSelectContainer;
        public RadioButton daytimeSelectCheckBox;
        public PieChartView pieChartView;
        public RaiflatButton deleteButton;
        public RaiflatButton cancelButton;
        public RaiflatButton addButton;

        public DayTimeSelectTextViewRecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            RecycleTextView = itemView.findViewById(R.id.daytime_select_textview);
            daytimeSelectContainer = itemView.findViewById(R.id.daytime_select_container);
            daytimeSelectCheckBox = itemView.findViewById(R.id.daytime_select_checkbox);
            pieChartView = itemView.findViewById(R.id.mPieChart);
            deleteButton = itemView.findViewById(R.id.daytime_select_delete_botton);
            cancelButton = itemView.findViewById(R.id.daytime_select_back_botton);
            addButton = itemView.findViewById(R.id.daytime_select_add_botton);

        }
    }
}
