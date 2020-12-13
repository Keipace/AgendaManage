package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.DayTargetInfoActivity;
import com.privateproject.agendamanage.activity.TargetInfoActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.PlanNode;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.customDialog.CenterDialog;
import com.privateproject.agendamanage.databinding.ItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.PlanNodeDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class GoalListServer {
    private TargetDao targetDao;
    private DayTargetDao dayTargetDao;
    private List<Target> targets;
    private List<DayTarget> dayTargets;
    private Context context;
    private ItemMainAddtargetBinding targetBinding;
    private ItemMainAdddaytargetBinding daytargetBinding;
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
        ExpandingItem targetItem=expandingList.createNewItem(R.layout.expanding_layout);
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
        // 长按时添加第一层计划节点
        view.findViewById(R.id.itemMainContent_container_relativeLayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View root = LayoutInflater.from(context).inflate(R.layout.dialog_add_plannode, null);
                MaterialEditText nameEditText = root.findViewById(R.id.addPlanDialog_name_MaterialEditText);
                EditText decoration = root.findViewById(R.id.addPlanDialog_decoration_editText);
                EditText startDate = root.findViewById(R.id.addPlanDialog_startDate_editText);
                EditText endDate = root.findViewById(R.id.addPlanDialog_endDate_editText);
                Switch setTimeNeeded = root.findViewById(R.id.addPlanDialog_setTimeNeeded_switch);
                setTimeNeeded.setVisibility(View.GONE);
                TimeUtil.setDateStartToEnd(context, startDate, endDate, true);
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle("添加计划")
                        .setView(root)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null).create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                endDate.getText().toString(),
                                targets.get(position));
                        planNodeDao.addPlanNode(planNode);
                        targets = targetDao.selectAll();
                        dialog.dismiss();
                    }
                });
                return true;
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
        targetBinding=ItemMainAddtargetBinding.inflate(LayoutInflater.from(context));
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
        ExpandingItem dayTargetItem=expandingList.createNewItem(R.layout.expanding_layout);
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
        daytargetBinding = ItemMainAdddaytargetBinding.inflate(LayoutInflater.from(context));
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

}
