package com.privateproject.agendamanage.module_planTarget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;

import java.util.ArrayList;
import java.util.List;

public class NoTimeLastPlanNodeAdapter extends RecyclerView.Adapter<NoTimeLastPlanNodeAdapter.NoTimeLastPlanNodeViewHolder>{
    private Context context;
    private List<PlanNode> lastPlanNodeNoSetTime;
    private PlanNodeDao planNodeDao;

    public NoTimeLastPlanNodeAdapter(Context context, List<PlanNode> lastPlanNodeNoSetTime){
        this.context = context;
        this.lastPlanNodeNoSetTime = lastPlanNodeNoSetTime;
        this.planNodeDao = new PlanNodeDao(context);
    }
    @NonNull
    @Override
    public NoTimeLastPlanNodeAdapter.NoTimeLastPlanNodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoTimeLastPlanNodeAdapter.NoTimeLastPlanNodeViewHolder
                (LayoutInflater.from(context).inflate(R.layout.plantarget_item_notime_plannode, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoTimeLastPlanNodeViewHolder holder, int position) {
        //设置名称
        holder.lastPlanNodeName.setText(lastPlanNodeNoSetTime.get(position).getName()+"");
        //创建list查询plannode的父亲节点
        List<String> parentNameList = new ArrayList<String>();
        searchParent(lastPlanNodeNoSetTime.get(position),parentNameList);
        //计算路径
        String path = "";
        path = parentNameList.get(parentNameList.size()-1) + ":";
        for (int i = parentNameList.size()-2; i >= 0; i--) {
            path = path + parentNameList.get(i) + "—>";
        }
        path = path + lastPlanNodeNoSetTime.get(position).getName();
        //设置路径
        holder.lastPlanNodePath.setText(path);
    }

    @Override
    public int getItemCount() {
        return lastPlanNodeNoSetTime.size();
    }

    static class NoTimeLastPlanNodeViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout lastPlanNodeLayout;
        public TextView lastPlanNodeName,lastPlanNodePath;

        public NoTimeLastPlanNodeViewHolder(@NonNull View itemView) {
            super(itemView);

            lastPlanNodeLayout = itemView.findViewById(R.id.itemnotime_plannode_layout);
            lastPlanNodeName = itemView.findViewById(R.id.itemnotime_plannode_name);
            lastPlanNodePath = itemView.findViewById(R.id.itemnotime_plannode_path);

        }
    }
    public void searchParent(PlanNode planNode, List<String> parentNameList){
        if (planNode.getTopParent() == null){
            parentNameList.add(planNodeDao.selectParent(planNode).getName());
            searchParent(planNodeDao.selectParent(planNode),parentNameList);
        }else {
            parentNameList.add(planNode.getTopParent().getName());
        }
    }
}
