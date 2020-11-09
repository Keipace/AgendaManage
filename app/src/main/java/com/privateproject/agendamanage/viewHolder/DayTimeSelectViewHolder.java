package com.privateproject.agendamanage.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;

public class DayTimeSelectViewHolder {

    public static class DayTimeSelectLayoutRecycleViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout RecycleLayout;
        public TextView RecycleLayoutTextView;

        public DayTimeSelectLayoutRecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            RecycleLayout = itemView.findViewById(R.id.daytime_select_constraintlayout);
            RecycleLayoutTextView = itemView.findViewById(R.id.daytime_select_constraintlayout_textview);
        }
    }

    public static class DayTimeSelectTextViewRecycleViewHolder extends RecyclerView.ViewHolder {
        public TextView RecycleTextView;
        public ConstraintLayout daytimeSelectContainer;

        public DayTimeSelectTextViewRecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            RecycleTextView = itemView.findViewById(R.id.daytime_select_textview);
            daytimeSelectContainer = itemView.findViewById(R.id.daytime_select_container);
        }
    }
}
