package com.privateproject.agendamanage.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;

public class MainExpandableListViewHolder {

    // 将每个group视图项包装成一个类
    public static class HeadHolder {
        // 每个属性对应页面中的每个控件
        public ConstraintLayout constraintLayout;
        public TextView groupView;
        public Button addBtn;
        // 构造方法，从页面中获取控件并赋值给对应的属性
        public HeadHolder(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_header_constraintLayout);
            groupView = itemView.findViewById(R.id.itemMain_header_planGroup);
            addBtn = itemView.findViewById(R.id.itemMain_header_add);
        }
    }
    // 将目标区的每个target视图项包装成一个类
    public static class ContentHolder {
        // 每个属性对应页面中的每个控件
        public ConstraintLayout constraintLayout;
        public TextView targetName,planOver,importance;
        public Button behindOver;
        // 构造方法，从页面中获取控件并赋值给对应的属性
        public ContentHolder(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_content_constraintLayout);
            targetName = itemView.findViewById(R.id.itemMain_content_targetName);
            behindOver = itemView.findViewById(R.id.itemMain_content_behindOver);
        }
    }
    // 将打卡区的每个dayTarget视图项包装成一个类
    public static class ContentHolderDayTarget {
        // 每个属性对应页面中的每个控件
        public ConstraintLayout constraintLayout;
        public TextView targetName,timeFragment,remainCounts;
        public Button behindOver;
        // 构造方法，从页面中获取控件并赋值给对应的属性
        public ContentHolderDayTarget(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_content_dayTarget_constraintLayout);
            targetName = itemView.findViewById(R.id.itemMain_content_dayTarget_targetName);
            timeFragment = itemView.findViewById(R.id.itemMain_content_dayTarget_timeFragment);
            remainCounts = itemView.findViewById(R.id.itemMain_content_dayTarget_remainCounts);
            behindOver = itemView.findViewById(R.id.itemMain_content_dayTarget_behindOver);
        }
    }

}
