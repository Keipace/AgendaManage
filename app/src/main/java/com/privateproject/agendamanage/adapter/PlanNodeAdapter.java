package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.PlanNode;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.db.PlanNodeDao;
import com.privateproject.agendamanage.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PlanNodeAdapter extends RecyclerView.Adapter<PlanNodeAdapter.PlanNodeViewHolder> {
    public static final String SPLIT = "—>";
    private String path;
    private TextView pathShow;

    private Context context;
    private Target topParent;
    private PlanNodeDao dao;

    private Stack<PlanNode> stack;
    private PlanNode parent;
    private List<PlanNode> planNodes;
    /*
    * 第一层时栈为空并且parent也为null，显示的列表为topParent的子类
    * 第二层时栈为空但是parent不为null，显示的列表为parent的子类*/

    public PlanNodeAdapter(Target topParent, Context context, TextView pathShow) {
        this.stack = new Stack<PlanNode>();
        this.topParent = topParent;
        this.planNodes = new ArrayList<>(topParent.getPlanNodes());
        this.context = context;
        this.dao = new PlanNodeDao(context);
        this.path = topParent.getName()+":";
        this.pathShow = pathShow;
        this.pathShow.setText(this.path);
    }

    @NonNull
    @Override
    public PlanNodeAdapter.PlanNodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.item_plan_node, parent, false);
        return new PlanNodeViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanNodeAdapter.PlanNodeViewHolder holder, int position) {
        PlanNode tmp = this.planNodes.get(position);
        holder.title.setText(tmp.getName());
        holder.childrenCount.setText((tmp.getChildren()==null?0:tmp.getChildren().size())+"");
        holder.startTime.setText(TimeUtil.getDate(tmp.getStartTime()));
        holder.endTime.setText(TimeUtil.getDate(tmp.getEndTime()));
        holder.duringDay.setText(tmp.getDuringDay()+"");

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent!=null) {
                    stack.push(parent);
                }
                if (parent==null) {
                    path += planNodes.get(position).getName();
                } else {
                    path += SPLIT+planNodes.get(position).getName();
                }
                pathShow.setText(path);
                parent = planNodes.get(position);
                dao.initPlanNode(parent);
                planNodes = parent.getChildren();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.planNodes==null)
            return 0;
        return this.planNodes.size();
    }

    public void back() {
        if (this.stack.isEmpty() && this.parent==null) {
            // 已经在第一层了，点返回无反应
            return;
        } else if (this.stack.isEmpty()) {
            // 特殊：第二层返回第一层
            this.path = topParent.getName()+":";
            this.pathShow.setText(this.path);
            this.parent = null;
            this.planNodes = new ArrayList<>(this.topParent.getPlanNodes());
        } else {
            String[] tmp = this.path.split(SPLIT);
            String tmpPath = "";
            for (int i = 0; i < tmp.length-2; i++) {
                tmpPath += tmp[i];
            }
            tmpPath += tmp[tmp.length-2];
            this.path = tmpPath;
            this.pathShow.setText(path);
            this.parent = this.stack.pop();
            this.planNodes = this.parent.getChildren();
        }
        notifyDataSetChanged();
    }

    static class PlanNodeViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout constraintLayout;
        public TextView title, childrenCount;
        public TextView startTime, endTime;
        public TextView duringDay;

        public PlanNodeViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.itemPlanNode_container);
            title = itemView.findViewById(R.id.itemPlanNode_title_textView);
            childrenCount = itemView.findViewById(R.id.itemPlanNode_childrenCount_textView);
            startTime = itemView.findViewById(R.id.itemPlanNode_startTime_textView);
            endTime = itemView.findViewById(R.id.itemPlanNode_endTime_textView);
            duringDay = itemView.findViewById(R.id.itemPlanNode_duringDay_textView);
        }
    }
}
