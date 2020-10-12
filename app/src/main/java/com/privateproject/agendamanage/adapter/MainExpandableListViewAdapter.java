package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

public class MainExpandableListViewAdapter extends BaseExpandableListAdapter {
    private static final String[] groups = {"目标区","打卡区"};
    private List<Target> targets;
    private List<DayTarget> dayTargets;
    private Context context;

    public MainExpandableListViewAdapter(Context context, List<Target> targets, List<DayTarget> dayTargets) {
        this.context = context;
        this.targets = targets;
        this.dayTargets = dayTargets;
    }

    public static String[] getGroups() {
        return groups;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<DayTarget> getDayTargets() {
        return dayTargets;
    }

    public void setDayTargets(List<DayTarget> dayTargets) {
        this.dayTargets = dayTargets;
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition==0)
            return targets.size();
        else if(groupPosition==1)
            return dayTargets.size();
        else
            throw new IndexOutOfBoundsException("groupPosition异常");
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition==0)
            return targets.get(childPosition);
        if(groupPosition==1)
            return targets.get(childPosition);
        else
            throw new IndexOutOfBoundsException("groupPosition异常");
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class HeadHolder {
        public ConstraintLayout constraintLayout;
        public TextView groupView;
        public Button addBtn;

        public HeadHolder(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_header_constraintLayout);
            groupView = itemView.findViewById(R.id.itemMain_header_planGroup);
            addBtn = itemView.findViewById(R.id.itemMain_header_add);
        }

    }
    static class ContentHolder {
        public ConstraintLayout constraintLayout;
        public TextView targetName,planOver,importance;
        public Button behindOver;

        public ContentHolder(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_content_constraintLayout);
            targetName = itemView.findViewById(R.id.itemMain_content_targetName);
            planOver = itemView.findViewById(R.id.itemMain_content_planOver);
            importance = itemView.findViewById(R.id.itemMain_content_importance);
            behindOver = itemView.findViewById(R.id.itemMain_content_behindOver);
        }

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HeadHolder headholder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_header, null);
            headholder = new HeadHolder(convertView);
            convertView.setTag(headholder);
        } else {
            headholder= (HeadHolder) convertView.getTag();
        }
        headholder.groupView.setText(groups[groupPosition]);
        headholder.addBtn.setTag(groupPosition);
        headholder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index= (int) v.getTag();
                Toast.makeText(context,""+index,Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ContentHolder contentholder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content, null);
            contentholder = new ContentHolder(convertView);
            convertView.setTag(contentholder);
        } else {
            contentholder= (ContentHolder) convertView.getTag();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        contentholder.targetName.setText(targets.get(childPosition).getName());
        contentholder.planOver.setText(sdf.format(targets.get(childPosition).getTimePlanOver()));
        contentholder.importance.setText(targets.get(childPosition).getImportance());
        contentholder.behindOver.setTag(childPosition);
        contentholder.behindOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int) view.getTag();
                Toast.makeText(context, ""+id, Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
