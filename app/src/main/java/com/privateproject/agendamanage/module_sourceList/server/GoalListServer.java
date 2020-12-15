package com.privateproject.agendamanage.module_sourceList.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.databinding.SourcelistItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.databinding.SourcelistItemMainAddtargetBinding;
import com.privateproject.agendamanage.module_planTarget.adapter.NoTimeLastPlanNodeAdapter;
import com.privateproject.agendamanage.module_sourceList.activity.DayTargetInfoActivity;
import com.privateproject.agendamanage.module_sourceList.activity.TargetInfoActivity;
import com.privateproject.agendamanage.db.bean.DayTarget;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.module_weekTime.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;
import com.privateproject.agendamanage.utils.CenterDialog;
import com.privateproject.agendamanage.db.dao.DayTargetDao;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xujiaji.happybubble.BubbleDialog;

import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GoalListServer {
    private TargetDao targetDao;
    private DayTargetDao dayTargetDao;
    private List<Target> targets;
    private List<DayTarget> dayTargets;
    private Context context;
    private SourcelistItemMainAddtargetBinding targetBinding;
    private SourcelistItemMainAdddaytargetBinding daytargetBinding;
    private PlanNodeDao planNodeDao;

    public GoalListServer(Context context) {
        this.targetDao = new TargetDao(context);
        this.dayTargetDao=new DayTargetDao(context);
        this.targets = targetDao.selectAll();
        this.dayTargets = dayTargetDao.selectAll();
        this.context=context;
        this.planNodeDao = new PlanNodeDao(context);
    }

    public void createTargetItem(ExpandingList expandingList, OnItemClick onItemClick) {
        ExpandingItem targetItem=expandingList.createNewItem(R.layout.sourcelist_expanding_layout);
        if (targetItem != null) {
            //为header设置头像和头像的颜色
            targetItem.setIndicatorColorRes(R.color.colorAccent);
            targetItem.setIndicatorIconRes(R.drawable.time);
            //设置header的文本为 目标区
            ((TextView) targetItem.findViewById(R.id.itemMainHeader_name_textView)).setText("目标区");
            // 创建子项并为子项绑定数据和监听器
            targetItem.createSubItems(this.targets.size());
            for (int i = 0; i < targetItem.getSubItemsCount(); i++) {
                View view=targetItem.getSubItemView(i);
                configureTargetItem(view, i, targetItem, onItemClick);
            }
            // 设置添加按钮的监听器
            targetItem.findViewById(R.id.itemMainHeader_add_imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInsertTargetDialog(new OnItemCreated(){
                        // 保存数据后，新建一个subItem显示新添加的数据，并为其设置数据和监听器
                        @Override
                        public void itemCreated(String name, String decoration,int position) {
                            View newSubItem =targetItem.createSubItem();
                            configureTargetItem(newSubItem, position, targetItem, onItemClick);
                        }
                    });
                }
            });
        }
    }

    private void configureTargetItem(View view, int position, ExpandingItem targetItem, OnItemClick onItemClick) {
        // 设置名称
        ((TextView)view.findViewById(R.id.itemMainContent_name_textView)).setText(targets.get(position).getName());
        // 设置删除按钮监听器
        view.findViewById(R.id.itemMainContent_delete_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除item视图
                targetItem.removeSubItem(view);
                // 删除数据
                removeTarget(position);
            }
        });

        // 鼠标点击提示图标时
        ImageView warningImageView = view.findViewById(R.id.itemMainContent_warning_imageView);
        // 初始化target时提示图标是否显示
        if (targets.get(position).isStateSave())
            warningImageView.setVisibility(View.GONE);
        else warningImageView.setVisibility(View.VISIBLE);
        warningImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1.如果存在 没有时间量的末节点 则弹出框列出那些节点（节点名和路径）

                // 2.时间不符合剩余时间量要求，则弹出框提示

                // 3.没问题时正常保存

                //加载页面
                View dialogView = LayoutInflater.from(context).inflate(R.layout.plantarget_dialog_bubble_target,null);
                //气泡弹出框
                BubbleDialog bubbleDialog = new BubbleDialog(context)
                        .addContentView(dialogView)
                        .setClickedView(warningImageView)
                        .setPosition(BubbleDialog.Position.BOTTOM)
                        .setOffsetY(-8)
                        .calBar(true);
                bubbleDialog.show();
                //获取跳转文本的监听器
                TextView textView = dialogView.findViewById(R.id.bubble_dialog_jump_textView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bubbleDialog.dismiss();
                        //处理保存逻辑代码
                        //获得target的planNodes,也就是第一代PlanNode
                        List<PlanNode> planNodes = new ArrayList<>(targets.get(position).getPlanNodes());
                        //初始化list保存最后一个planNode设置的时间
                        List<PlanNode> lastPlanNodeList = new ArrayList<PlanNode>();
                        //初始化list保存最后一个但未设置时间的planNode
                        List<PlanNode> lastPlanNodeNoSetTime = new ArrayList<PlanNode>();
                        for (int i = 0; i < planNodes.size(); i++) {
                            //为lastPlanNodeList填充数据
                            searchLastPlanNode(planNodes.get(i),lastPlanNodeList,lastPlanNodeNoSetTime);
                        }
                        //如果列表为空，证明用户已设置所有最后节点的时间，进行2选项判断
                        if (lastPlanNodeNoSetTime == null||lastPlanNodeNoSetTime.size() == 0){
                            //获得每天剩余的时间总量
                            EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(context);
                            for (int i = 0; i < lastPlanNodeList.size(); i++) {
                                //调用surplusTime（开始日期，应急时间开始日期，结束日期）获得此PlanNode所在日期的 每 日 剩 余 时 间 量
                                List<Integer> surplusTimeList = everydayTotalTimeServer.surplusTime(lastPlanNodeList.get(i).getStartTime(), lastPlanNodeList.get(i).getEndTime());
                                if (surplusTimeList == null||surplusTimeList.size() == 0){
                                    ToastUtil.newToast(context,"还未设置时间段，请先去设置时间段！");
                                    return;
                                }
                                //每日剩余时间量相加
                                int allSurplusTime = 0;
                                for (int j = 0; j < surplusTimeList.size(); j++) {
                                    allSurplusTime = allSurplusTime + surplusTimeList.get(i);
                                }
                                if (allSurplusTime <= lastPlanNodeList.get(i).getTimeNeeded()){
                                    savePlanNodeDialog();
                                    return;
                                }
                            }
                            ToastUtil.newToast(context,"保存成功");
                            //提示图标消失
                            warningImageView.setVisibility(View.GONE);
                            //stateSave属性设为true，并修改数据库
                            targets.get(position).setStateSave(true);
                            targetDao.updateTarget(targets.get(position));
                        }else {  //列表不为空，为用户弹出提示框，显示为设置时间的最后节点
                            //初始化adapter
                            NoTimeLastPlanNodeAdapter adapter = new NoTimeLastPlanNodeAdapter(context,lastPlanNodeNoSetTime);
                            //加载界面
                            View noTimePlanNode = LayoutInflater.from(context).inflate(R.layout.plantarget_dialog_notime_plannode,null);
                            RecyclerView recyclerView = noTimePlanNode.findViewById(R.id.notime_plannode_recycle);
                            //设置Adapter
                            recyclerView.setAdapter(adapter);
                            //创建弹出框
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("提示")
                                    .setView(noTimePlanNode)
                                    .setNegativeButton("确定",null)
                                    .create().show();
                        }

                    }
                });
            }
        });
        // 点击 安排计划 时
        view.findViewById(R.id.itemMainContent_plan_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.planTarget(targets.get(position));
            }
        });
        // 点击时跳转到详情页
        view.findViewById(R.id.itemMainContent_container_relativeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定从MainActivity跳转到详情页
                Intent intent = new Intent(context, TargetInfoActivity.class);
                // 跳转的时候将该child对应的id值传送过去
                intent.putExtra("id", targets.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    // 从数据库和list中删除指定的target
    private void removeTarget(int position) {
        Target target = targets.get(position);
        targetDao.deleteTargetById(target.getId());
        this.targets.remove(position);
    }

    private void showInsertTargetDialog(final OnItemCreated positive){
        targetBinding=SourcelistItemMainAddtargetBinding.inflate(LayoutInflater.from(context));
        // 新建弹出框
        CenterDialog targetDialog=new CenterDialog(context, R.style.DayTargetDialog);
        // 设置标题、取消按钮、确定按钮
        targetDialog.setTitle("目标").setView(targetBinding.getRoot())
                .setCancelBtn("×", new CenterDialog.IOnCancelListener() {
            @Override
            public void OnCancel(CenterDialog dialog) {
                dialog.dismiss();
            }
        }).setConfirmBtn("√", new CenterDialog.IOnConfirmListener() {
            // 点击添加弹出框的确定按钮时
            @Override
            public void OnConfirm(CenterDialog dialog) {
                // 将焦点获取到确定按钮上
                ComponentUtil.requestFocus(targetDialog.confirmBtn);
                // 获取输入的名称和描述
                String name = targetBinding.targetNameEditText.getText().toString();
                String decoration = targetBinding.targetDecorationEditText.getText().toString();
                // 判断必要项是否有空的，有空则不存
                if(name.equals("")){
                    // 如果用户输入的信息不完整就提示
                    ToastUtil.newToast(context, "请输入完成信息");
                } else {
                    // 将输入的数据保存到数据库
                    Target target=new Target(name,decoration);
                    targetDao.addTarget(target);
                    //将输入的数据添加到list中
                    targets.add(target);
                    // 保存数据后，新建一个subItem显示新添加的数据，并为其设置name和删除按钮监听器
                    positive.itemCreated(name, decoration, targets.size()-1);
                    //取消弹出框
                    targetDialog.dismiss();
                }

            }
        }).setCancelable(false);
        targetDialog.show();
    }

    public void createDayTargetItem(ExpandingList expandingList) {
        ExpandingItem dayTargetItem=expandingList.createNewItem(R.layout.sourcelist_expanding_layout);
        if (dayTargetItem != null) {
            //为header设置头像和头像的颜色
            dayTargetItem.setIndicatorColorRes(R.color.molired);
            dayTargetItem.setIndicatorIconRes(R.drawable.time);
            //设置header的文本为 打卡区
            ((TextView) dayTargetItem.findViewById(R.id.itemMainHeader_name_textView)).setText("打卡区");

            dayTargetItem.createSubItems(this.dayTargets.size());
            for (int i = 0; i < dayTargetItem.getSubItemsCount(); i++) {
                View view=dayTargetItem.getSubItemView(i);
                configureDayTargetItem(view, i, dayTargetItem);
            }

            // 给添加按钮添加监听器
            dayTargetItem.findViewById(R.id.itemMainHeader_add_imageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 调用showInsertDayTargetDialog显示弹出框
                    showInsertDayTargetDialog(new OnItemCreated(){
                        // 保存数据后，新建一个subItem显示新添加的数据，并为其设置name和删除按钮监听器
                        @Override
                        public void itemCreated(String name, String decoration,int position) {
                            View newSubItem =dayTargetItem.createSubItem();
                            configureDayTargetItem(newSubItem, position, dayTargetItem);
                        }
                    });
                }
            });
        }
    }

    private void configureDayTargetItem(View view, int position, ExpandingItem dayTargetItem) {
        // 设置每个子项的名称
        ((TextView)view.findViewById(R.id.itemMainContent_name_textView)).setText(dayTargets.get(position).getName());
        // 设置每个子项的删除按钮
        view.findViewById(R.id.itemMainContent_delete_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除该子项的视图
                dayTargetItem.removeSubItem(view);
                // 删除该子项的数据库和list数据
                removeDayTarget(position);
            }
        });
        view.findViewById(R.id.itemMainContent_container_relativeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定从MainActivity跳转到详情页
                Intent intent = new Intent(context, DayTargetInfoActivity.class);
                // 跳转的时候将该child对应的id值传送过去
                intent.putExtra("id", dayTargets.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    // 删除该子项的数据库和list数据
    private void removeDayTarget(int position) {
        DayTarget dayTarget = dayTargets.get(position);
        dayTargetDao.deleteDayTargetById(dayTarget.getId());
        this.dayTargets.remove(position);
    }

    private void showInsertDayTargetDialog(final OnItemCreated positive){
        daytargetBinding = SourcelistItemMainAdddaytargetBinding.inflate(LayoutInflater.from(context));
        // 新建弹出框
        CenterDialog dayTargetDialog=new CenterDialog(context, R.style.DayTargetDialog);
        // 设置弹出框的标题、取消和确定按钮
        dayTargetDialog.setTitle("打卡").setView(daytargetBinding.getRoot())
                .setCancelBtn("×", new CenterDialog.IOnCancelListener() {
            @Override
            public void OnCancel(CenterDialog dialog) {
                dialog.dismiss();
            }
        }).setConfirmBtn("√", new CenterDialog.IOnConfirmListener() {
            // 点击添加弹出框的确定按钮时
            @Override
            public void OnConfirm(CenterDialog dialog) {
                // 将焦点获取到确定按钮上
                ComponentUtil.requestFocus(dayTargetDialog.confirmBtn);
                // 获取输入的名称和描述
                String name = daytargetBinding.daytargetDayNameEditText.getText().toString();
                String decoration = daytargetBinding.daytargetDayDecorationEditText.getText().toString();
                // 判断必要项是否有空的，有空则不存
                if(name.equals("")){
                    // 如果用户输入的信息不完整就提示
                    ToastUtil.newToast(context, "请输入完成信息");
                } else {
                    // 将输入的数据保存到数据库
                    DayTarget dayTarget=new DayTarget(name,decoration);
                    dayTargetDao.addDayTarget(dayTarget);
                    //将输入的数据添加到list中
                    dayTargets.add(dayTarget);
                    // 保存数据后，新建一个subItem显示新添加的数据，并为其设置name和删除按钮监听器
                    positive.itemCreated(name, decoration, dayTargets.size()-1);
                    //取消弹出框
                    dayTargetDialog.dismiss();
                }

            }
        }).setCancelable(false);
        dayTargetDialog.show();
    }

    interface OnItemCreated {
        void itemCreated(String name, String decoration, int position);
    }

    public interface OnItemClick {
        void planTarget(Target topTarget);
    }
    //查询所有的最后一个PlanNode，并保存到列表中
    public void searchLastPlanNode(PlanNode planNode, List<PlanNode> lastPlanNodeList, List<PlanNode> lastPlanNodeNoSetTime){
        this.planNodeDao.initPlanNode(planNode);
        if (planNode.isHasChildren()&&planNode.getChildren() != null&&planNode.getChildren().size() !=0){
            for (int i = 0; i < planNode.getChildren().size(); i++) {
                searchLastPlanNode(planNode.getChildren().get(i),lastPlanNodeList,lastPlanNodeNoSetTime);
            }
        }
        if (!planNode.isHasChildren())           //如果没有孩子，返回它的时间
            lastPlanNodeList.add(planNode);
        if (planNode.isHasChildren()&&(planNode.getChildren() == null||planNode.getChildren().size() == 0))
            lastPlanNodeNoSetTime.add(planNode);
    }
    //保存出现错误时出现dialog弹出框
    public void savePlanNodeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示")
                .setMessage("你输入的计划有冲突，请重新安排，或者使用我们推荐的方案？")
                .setNegativeButton("使用推荐方案", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //实现推荐方案代码
                        /*
                        to do...
                         */
                    }
                })
                .setPositiveButton("自己调整",null)
                .create().show();
    }
}
