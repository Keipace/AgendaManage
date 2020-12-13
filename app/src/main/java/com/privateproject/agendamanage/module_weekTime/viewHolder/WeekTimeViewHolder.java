package com.privateproject.agendamanage.module_weekTime.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;

public class WeekTimeViewHolder {
    public static class WeekTimeCellViewHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout rootView;
        public Button btnCell;
        public TextView textViewRight;
        public TextView textViewBottom;

        public WeekTimeCellViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rootView = itemView.findViewById(R.id.itemWeekTime_rootView);
            this.btnCell = itemView.findViewById(R.id.itemWeekTime_cell_btn);
            this.textViewRight = itemView.findViewById(R.id.itemWeekTime_right_textView);
            this.textViewBottom = itemView.findViewById(R.id.itemWeekTime_bottom_textView);
        }
    }

    public static class WeekTimeHeadViewHoder extends RecyclerView.ViewHolder{
        public TextView textViewHead;

        public WeekTimeHeadViewHoder(@NonNull View itemView) {
            super(itemView);
            this.textViewHead = itemView.findViewById(R.id.itemWeekTime_head_tv);
        }
    }

    public static class WeekTimeLeftViewHoder extends RecyclerView.ViewHolder{
        public TextView textViewLeft;

        public WeekTimeLeftViewHoder(@NonNull View itemView) {
            super(itemView);
            this.textViewLeft = itemView.findViewById(R.id.itemWeekTime_left_textView);
        }
    }
}
