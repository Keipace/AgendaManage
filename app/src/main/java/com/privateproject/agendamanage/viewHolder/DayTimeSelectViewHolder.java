package com.privateproject.agendamanage.viewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;

public class DayTimeSelectViewHolder {

    public static class DayTimeSelectTextViewRecycleViewHolder extends RecyclerView.ViewHolder {
        public TextView RecycleTextView;
        public ConstraintLayout daytimeSelectContainer;
        public CheckBox daytimeSelectCheckBox;

        public DayTimeSelectTextViewRecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            RecycleTextView = itemView.findViewById(R.id.daytime_select_textview);
            daytimeSelectContainer = itemView.findViewById(R.id.daytime_select_container);
            daytimeSelectCheckBox = itemView.findViewById(R.id.daytime_select_checkbox);

        }
    }
}
