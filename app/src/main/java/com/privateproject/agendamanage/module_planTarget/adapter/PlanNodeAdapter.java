package com.privateproject.agendamanage.module_planTarget.adapter;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.module_planTarget.server.PlanNodeServer;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class PlanNodeAdapter extends RecyclerView.Adapter<PlanNodeAdapter.PlanNodeViewHolder> {
    public static final String SPLIT = "—>";
    private String path;
    private TextView pathShow;
    private FloatingActionButton floatingActionButton;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

    private Context context;
    private Target topParent;
    private PlanNodeDao dao;
    private TargetDao targetDao;

    private Stack<PlanNode> stack;
    private PlanNode parent;
    private List<PlanNode> planNodes;
    /*
    * 第一层时栈为空并且parent也为null，显示的列表为topParent的子类
    * 第二层时栈为空但是parent不为null，显示的列表为parent的子类*/

    public PlanNodeAdapter(Target topParent, Context context, TextView pathShow, FloatingActionButton floatingActionButton) {
        this.stack = new Stack<PlanNode>();
        this.topParent = topParent;
        if (topParent.getPlanNodes()!=null) {
            this.planNodes = new ArrayList<>(topParent.getPlanNodes());
            this.planNodes = this.dao.sortPlanNodeList(this.planNodes);
        }
        this.context = context;
        this.dao = new PlanNodeDao(context);
        this.path = topParent.getName()+":";
        this.pathShow = pathShow;
        this.pathShow.setText(this.path);
        this.targetDao = new TargetDao(context);
        this.floatingActionButton = floatingActionButton;
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddDialogPositiveBtnClick();
            }
        });
    }

    @NonNull
    @Override
    public PlanNodeAdapter.PlanNodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.plantarget_item_plannode, parent, false);
        return new PlanNodeViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanNodeAdapter.PlanNodeViewHolder holder, int position) {
        PlanNode tmp = this.planNodes.get(position);
        dao.initPlanNode(tmp);
        holder.title.setText(tmp.getName());
        holder.startTime.setText(sdf.format(tmp.getStartTime()));
        holder.endTime.setText(sdf.format(tmp.getEndTime()));
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
                    planNodes = dao.sortPlanNodeList(planNodes);
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.childrenCountTitle.setText("所需时间(分钟):");
            holder.childrenCount.setText(tmp.getTimeNeeded()+"");
            holder.constraintLayout.setOnClickListener(null);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddDialogPositiveBtnClick();
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
        //点击删除图标，删除此节点及其所包含的子节点
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent==null) {
                // 第一层删除时
                    deleteFirstLevelPlanNode(planNodes.get(position));
                    topParent = targetDao.selectById(topParent.getId());
                } else {
                // 其他层删除时
                    parent = deletePlanNodes(parent, planNodes.get(position));
                }
                ToastUtil.newToast(context,"删除成功");
                refreshList();
            }
        });
        //如果有推荐方法，显示相关的UI及内容
        showRecommendUI(holder, tmp);
        //长按推荐页面布局，作用为使用推荐方案按钮
        holder.recommendContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //把推荐方案变为可执行方案
                planNodes.get(position).saveRecommended();
                //更改文本框的数据
                holder.startTime.setText(sdf.format(tmp.getStartTime()));
                holder.endTime.setText(sdf.format(tmp.getEndTime()));
                holder.duringDay.setText(tmp.getDuringDay()+"");
                holder.recommendContainer.setVisibility(View.GONE);
                //更改数据库
                dao.updatePlanNode(planNodes.get(position));
                return true;
            }
        });
    }

    private void onAddDialogPositiveBtnClick() {
        View root = LayoutInflater.from(context).inflate(R.layout.plantarget_dialog_add_plan_node, null);
        MaterialEditText nameEditText = root.findViewById(R.id.addPlanDialog_name_MaterialEditText);
        EditText decoration = root.findViewById(R.id.addPlanDialog_decoration_editText);
        EditText startDate = root.findViewById(R.id.addPlanDialog_startDate_editText);
        EditText endDate = root.findViewById(R.id.addPlanDialog_endDate_editText);
        Switch setTimeNeeded = root.findViewById(R.id.addPlanDialog_setTimeNeeded_switch);
        ConstraintLayout container = root.findViewById(R.id.addPlanDialog_addPlanChild_container);
        MaterialEditText timeNeed = root.findViewById(R.id.addPlanDialog_setTimeNeeded_materialEditText);

        setTimeNeeded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timeNeed.setVisibility(View.VISIBLE);
                } else {
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
                // 0.首先判断target的预期结束时间和最晚截止时间
                if (topParent.getTimeDeadLine()==null || topParent.getTimePlanOver()==null) {
                    ToastUtil.newToast(context, "请先设置目标的最晚截止时间和预期结束时间");
                    return;
                }

                // 1.获取页面信息
                String name = nameEditText.getText().toString();
                String startDateString = startDate.getText().toString();
                String endDateString = endDate.getText().toString();
                if (name.equals("") ||
                        startDateString.equals("") ||
                        endDateString.equals("")) {
                    ToastUtil.newToast(context, "请输入完整信息！");
                    return;
                }
                String decor = decoration.getText().toString();
                if (decor.equals("")) {
                    decor = PlanNode.DEFAULT_DECORATION;
                }
                Integer time = null;
                if (setTimeNeeded.isChecked()) {
                    if (timeNeed.getText().toString().equals("")) {
                        ToastUtil.newToast(context, "请输入所需时间量！");
                        return;
                    }
                    time = Integer.parseInt(timeNeed.getText().toString());
                }

                // 2.判断所需时间是否符合开始日期和结束日期的要求
                if (setTimeNeeded.isChecked()) {
                    Date start = TimeUtil.getDate(startDateString);
                    Date end = TimeUtil.getDate(endDateString);
                    int totalTime = (TimeUtil.subDate(start, end)+1)*24*60;
                    if (time>totalTime) {
                        ToastUtil.newToast(context, "所需时间量过长,"+startDateString+"~"+endDateString+"最多有"+totalTime+"分钟");
                        return;
                    }
                }

                // 3.判断设置的开始日期和结束日期是否和已有的兄弟节点冲突
                Date start = TimeUtil.getDate(startDateString);
                Date end = TimeUtil.getDate(endDateString);
                int i;
                for (i = 0; i < planNodes.size(); i++) {
                    if (start.before(planNodes.get(i).getEndTime())) {
                        if (end.before(planNodes.get(i).getStartTime())) {
                            break;
                        } else {
                            ToastUtil.newToast(context, "选择的时间段与已有的时间段冲突！");
                            return;
                        }
                    }
                }

                // 4.判断设置的开始日期和结束日期是否超出父亲节点的范围（第一层添加时不用考虑）
                if (parent!=null) {
                    if (i==0) {
                        // 如果插入的节点是第一个节点，则第一个节点的开始日期不能超出父亲节点的开始日期
                        if (start.before(parent.getStartTime())) {
                            ToastUtil.newToast(context, "开始日期超出了上层计划的开始日期:"+TimeUtil.getDate(parent.getStartTime()));
                            return;
                        }
                    }
                    if (i==planNodes.size()) {
                        // 如果插入的节点是最后一个节点，则最后一个节点的结束日期不能超出父亲节点的结束日期
                        if (end.after(parent.getEndTime())) {
                            ToastUtil.newToast(context, "结束日期超出了上层计划的结束日期:"+TimeUtil.getDate(parent.getEndTime()));
                            return;
                        }
                    }
                }
                if (parent==null) {
                    if (i==0) {
                        // 如果插入的节点是第一个节点，则第一个节点的开始日期不能超出父亲节点的开始日期
                        if (start.before(TimeUtil.getOffCurrentDate(1))) {
                            ToastUtil.newToast(context, "开始时间不能早于明天");
                            return;
                        }
                    }
                    if (i==planNodes.size()) {
                        // 如果插入的节点是最后一个节点，则最后一个节点的结束日期超出target的预期结束时间时要提示
                        if (end.after(topParent.getTimePlanOver())) {
                            ToastUtil.newToast(context, "注意：结束日期超出了目标预期日期:"+TimeUtil.getDate(topParent.getTimePlanOver()));
                        }
                        // 如果插入的节点是最后一个节点，则最后一个节点的结束日期不能超出target的截止日期
                        if (end.after(topParent.getTimeDeadLine())) {
                            ToastUtil.newToast(context, "结束日期不能晚于目标截止日期:"+TimeUtil.getDate(topParent.getTimeDeadLine()));
                            return;
                        }
                    }
                }

                // 5.创建相应状态的planNode并修改父节点
                PlanNode planNode;
                // 用户设置的是所需时间量
                if (parent==null) {
                    if (setTimeNeeded.isChecked()) {
                        planNode = new PlanNode(name, decor, startDateString, endDateString, false, time, topParent);
                    } else {
                        planNode = new PlanNode(name, decor, startDateString, endDateString, topParent);
                    }
                    dao.addPlanNode(planNode);
                    topParent = targetDao.selectById(topParent.getId());
                } else {
                    if (setTimeNeeded.isChecked()) {
                        planNode = new PlanNode(name, decor, startDateString, endDateString, false, time);
                    } else {
                        planNode = new PlanNode(name, decor, startDateString, endDateString);
                    }
                    dao.addPlanNode(planNode);
                    dao.addChild(parent, planNode);
                }

                // 6.修改target的stateSave属性,并保存到数据库
                topParent.setStateSave(false);
                targetDao.updateTarget(topParent);

                // 7.刷新列表和页面
                refreshList();
                dialog.dismiss();
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
        this.planNodes = this.dao.sortPlanNodeList(this.planNodes);
        notifyDataSetChanged();
    }


    public void refreshList() {
        if (this.parent==null) {
            this.planNodes = new ArrayList<>(this.topParent.getPlanNodes());
        } else {
            this.parent = this.dao.selectById(this.parent.getId());
            this.planNodes = this.parent.getChildren();
        }
        this.planNodes = this.dao.sortPlanNodeList(this.planNodes);
        notifyDataSetChanged();
    }

    static class PlanNodeViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout constraintLayout,recommendContainer;
        public TextView title, childrenCount, childrenCountTitle,recommmendStartTime,recommendEndTime,recommendDruingDay;
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

            //推荐的组件(默认不显示)
            recommendContainer = itemView.findViewById(R.id.itemPlanNode_recommend_container);
            recommmendStartTime = itemView.findViewById(R.id.itemPlanNode_recommend_startTime_textView);
            recommendEndTime = itemView.findViewById(R.id.itemPlanNode_recommend_endTime_textView);
            recommendDruingDay = itemView.findViewById(R.id.itemPlanNode_recommend_duringDay_textView);


        }
    }

    private void showRecommendUI(PlanNodeAdapter.PlanNodeViewHolder holder, PlanNode planNode){
        if (planNode.isRecommended()){
            holder.recommmendStartTime.setText(planNode.getRecommendStartTime().toString());
            holder.recommendEndTime.setText(planNode.getRecommendEndTime().toString());
            holder.recommendDruingDay.setText(planNode.getRecommendDuringDay());
            holder.recommendContainer.setVisibility(View.VISIBLE);
        }
    }


    private PlanNode deletePlanNodes(PlanNode parentOf, PlanNode planNode){
        if (planNode.isHasChildren()&&planNode.getChildren() != null&&planNode.getChildren().size() !=0){
            for (int i = 0; i < planNode.getChildren().size(); i++) {
                deletePlanNodes(planNode, planNode.getChildren().get(i));
            }
        }
        return dao.deleteChild(parentOf, planNode);
    }

    private void deleteFirstLevelPlanNode(PlanNode planNode) {
        if (planNode.isHasChildren()&&planNode.getChildren() != null&&planNode.getChildren().size() !=0){
            for (int i = 0; i < planNode.getChildren().size(); i++) {
                deleteFirstLevelPlanNode(planNode.getChildren().get(i));
            }
        }
        dao.deletePlanNodeById(planNode.getId());
    }


}
