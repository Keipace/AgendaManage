package com.privateproject.agendamanage.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.PlanNode;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.db.PlanNodeDao;
import com.privateproject.agendamanage.server.PlanNodeServer;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

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
        if (topParent.getPlanNodes()!=null)
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
        dao.initPlanNode(tmp);
        holder.title.setText(tmp.getName());
        holder.startTime.setText(TimeUtil.getDate(tmp.getStartTime()));
        holder.endTime.setText(TimeUtil.getDate(tmp.getEndTime()));
        holder.duringDay.setText(tmp.getDuringDay()+"");

        if (tmp.isHasChildren()) {
            holder.childrenCountTitle.setText("子节点数量:");
            holder.childrenCount.setText((tmp.getChildren()==null?0:tmp.getChildren().size())+"");
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
        } else {
            holder.childrenCountTitle.setText("所需时间(分钟):");
            holder.childrenCount.setText(tmp.getTimeNeeded()+"");
            holder.constraintLayout.setOnClickListener(null);
        }

        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View root = LayoutInflater.from(context).inflate(R.layout.dialog_add_plannode, null);
                MaterialEditText nameEditText = root.findViewById(R.id.addPlanDialog_name_MaterialEditText);
                EditText decoration = root.findViewById(R.id.addPlanDialog_decoration_editText);
                EditText startDate = root.findViewById(R.id.addPlanDialog_startDate_editText);
                EditText endDate = root.findViewById(R.id.addPlanDialog_endDate_editText);
                Switch setTimeNeeded = root.findViewById(R.id.addPlanDialog_setTimeNeeded_switch);
                ConstraintLayout container = root.findViewById(R.id.addPlanDialog_addPlanChild_container);
                MaterialEditText timeNeed = root.findViewById(R.id.addPlanDialog_setTimeNeeded_materialEditText);

                setTimeNeeded.setChecked(!tmp.isHasChildren());
                if (setTimeNeeded.isChecked()) {
                    container.setVisibility(View.GONE);
                    timeNeed.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.VISIBLE);
                    timeNeed.setVisibility(View.GONE);
                }

                setTimeNeeded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            container.setVisibility(View.GONE);
                            timeNeed.setVisibility(View.VISIBLE);
                        } else {
                            container.setVisibility(View.VISIBLE);
                            timeNeed.setVisibility(View.GONE);
                        }
                    }
                });

                TimeUtil.setDateStartToEnd(context, startDate, endDate, true);
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle("添加计划")
                        .setView(root)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null).create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 用户设置的是所需时间量
                        if (setTimeNeeded.isChecked()) {
                            if (timeNeed.getText().toString().equals("")) {
                                ToastUtil.newToast(context, "请输入所需时间量！");
                                return;
                            }
                            Integer time = Integer.parseInt(timeNeed.getText().toString());
                            tmp.setChildren(false, time);
                            dao.updatePlanNode(tmp);
                            refreshList();
                            dialog.dismiss();
                            return;
                        }
                        // 用户设置的是添加子计划
                        String decor = decoration.getText().toString();
                        if (nameEditText.getText().toString().equals("") ||
                                startDate.getText().toString().equals("") ||
                                endDate.getText().toString().equals("")) {
                            ToastUtil.newToast(context, "请输入完整信息！");
                            return;
                        }
                        if (decor.equals("")) {
                            decor = PlanNode.DEFAULT_DECORATION;
                        }
                        PlanNode planNode = new PlanNode(nameEditText.getText().toString(), decor,
                                startDate.getText().toString(),
                                endDate.getText().toString());
                        dao.addPlanNode(planNode);
                        dao.addChild(planNodes.get(position), planNode);
                        refreshList();
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });

        PlanNodeServer planNodeServer = new PlanNodeServer(context);
        PlanNodeServer.DataChangeListener dataChangeListener = new PlanNodeServer.DataChangeListener() {
            @Override
            public void refresh() {
                refreshList();
            }
        };
        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planNodeServer.showEditDialog(tmp,dataChangeListener);
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
                tmpPath += tmp[i]+SPLIT;
            }
            tmpPath += tmp[tmp.length-2];
            this.path = tmpPath;
            this.pathShow.setText(path);
            this.parent = this.stack.pop();
            this.planNodes = this.parent.getChildren();
        }
        notifyDataSetChanged();
    }



    public void refreshList() {
        if (this.parent==null) {
            this.planNodes = new ArrayList<>(this.topParent.getPlanNodes());
        } else {
            this.planNodes = this.parent.getChildren();
        }
        notifyDataSetChanged();
    }

    static class PlanNodeViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout constraintLayout;
        public TextView title, childrenCount, childrenCountTitle;
        public TextView startTime, endTime;
        public TextView duringDay;

        public ImageView imageDelete;
        public ImageView imageEdit;
        public PlanNodeViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.itemPlanNode_container);
            title = itemView.findViewById(R.id.itemPlanNode_title_textView);
            childrenCount = itemView.findViewById(R.id.itemPlanNode_childrenCount_textView);
            startTime = itemView.findViewById(R.id.itemPlanNode_startTime_textView);
            endTime = itemView.findViewById(R.id.itemPlanNode_endTime_textView);
            duringDay = itemView.findViewById(R.id.itemPlanNode_duringDay_textView);
            childrenCountTitle = itemView.findViewById(R.id.itemPlanNode_childrenCountTitle_textView);

            imageDelete = itemView.findViewById(R.id.itemPlanNode_delete_imageView);
            imageEdit = itemView.findViewById(R.id.itemPlanNode_edit_imageView);
        }
    }
}
