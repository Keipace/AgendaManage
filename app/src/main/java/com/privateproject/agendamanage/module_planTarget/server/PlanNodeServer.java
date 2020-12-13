package com.privateproject.agendamanage.module_planTarget.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class PlanNodeServer {
    private Context context;
    private PlanNodeDao dao;
    private boolean isEdit = false;

    public PlanNodeServer(Context context) {
        this.context = context;
        this.dao = new PlanNodeDao(context);
    }

    public void showEditDialog(PlanNode parent, DataChangeListener dataChange){

        View root = LayoutInflater.from(context).inflate(R.layout.plantarget_dialog_add_plannode, null);
        MaterialEditText nameEditText = root.findViewById(R.id.addPlanDialog_name_MaterialEditText);
        EditText decoration = root.findViewById(R.id.addPlanDialog_decoration_editText);
        EditText startDate = root.findViewById(R.id.addPlanDialog_startDate_editText);
        EditText endDate = root.findViewById(R.id.addPlanDialog_endDate_editText);
        Switch setTimeNeeded = root.findViewById(R.id.addPlanDialog_setTimeNeeded_switch);
        ConstraintLayout container = root.findViewById(R.id.addPlanDialog_addPlanChild_container);
        MaterialEditText timeNeed = root.findViewById(R.id.addPlanDialog_setTimeNeeded_materialEditText);

        setTimeNeeded.setChecked(!parent.isHasChildren());
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

        nameEditText.setText(parent.getName());
        decoration.setText(parent.getDecoration());
        startDate.setText(TimeUtil.getDate(parent.getStartTime()));
        endDate.setText(TimeUtil.getDate(parent.getEndTime()));
        timeNeed.setText(parent.getTimeNeeded()+"");

        ComponentUtil.EditTextEnable(false,nameEditText);
        ComponentUtil.EditTextEnable(false,decoration);
        ComponentUtil.EditTextEnable(false,timeNeed);
        setUnable(startDate);
        setUnable(endDate);
        setTimeNeeded.setEnabled(false);


        TimeUtil.setDateStartToEnd(context, startDate, endDate, true);
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("编辑计划")
                .setView(root)
                .setPositiveButton("编辑", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.show();

        Button positiveButton = dialog.getButton((DialogInterface.BUTTON_POSITIVE));
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEdit==true){//编辑状态

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

                    //用户设置的是所需时间量
                    if (setTimeNeeded.isChecked()) {
                        if (timeNeed.getText().toString().equals("")) {
                            ToastUtil.newToast(context, "请输入所需时间量！");
                            return;
                        }
                        Integer time = Integer.parseInt(timeNeed.getText().toString());
                        parent.setChildren(false, time);

                        parent.setName(nameEditText.getText().toString());
                        parent.setDecoration(decor);
                        parent.setStartAndEndTime(startDate.getText().toString(),endDate.getText().toString());
                        dao.updatePlanNode(parent);
                        dataChange.refresh();
                        dialog.dismiss();
                        return;
                    }

                    if(!parent.isHasChildren()){//最后一个节点
                        //修改时间和名称等，变成不是最后一个
                        if (!setTimeNeeded.isChecked()) {//说明还是最后一个节点
                            parent.setChildren(true,new ArrayList<PlanNode>());
                        }
                    }else {//不是最后一个
                        if (parent.getChildren() == null||parent.getChildren().size() == 0){//没有孩子
                            //修改名称等，变为最后一个
                            if (setTimeNeeded.isChecked()) {//说明变为最后一个
                            }
                        }
                    }

                    parent.setName(nameEditText.getText().toString());
                    parent.setDecoration(decor);
                    parent.setStartAndEndTime(startDate.getText().toString(),endDate.getText().toString());
                    dao.updatePlanNode(parent);

                    dataChange.refresh();
                    dialog.dismiss();
//                    if(!parent.isHasChildren()){//最后一个节点
//                        //修改时间和名称等，变成不是最后一个
//                        if (setTimeNeeded.isChecked()) {//说明还是最后一个节点
//
//                        }else {//说明变为不是最后一个节点
//                            parent.setChildren(true,new ArrayList<PlanNode>());
//                        }
//                    }else {//不是最后一个
//                        if (parent.getChildren() == null||parent.getChildren().size() == 0){//没有孩子
//                            //修改名称等，变为最后一个
//                            if (setTimeNeeded.isChecked()) {//说明变为最后一个
//
//                            }else {//不是最后一个
//
//                            }
//
//                        }else {//有孩子
//                            //只能修改名称等
//
//                        }
//                    }


                }else {//详情状态

                    if(!parent.isHasChildren()){//最后一个子节点
                        //修改时间和名称等，变成不是最后一个
                        ComponentUtil.EditTextEnable(true,timeNeed);
                        setTimeNeeded.setEnabled(true);
                    }else {//不是最后一个
                        if (parent.getChildren() == null||parent.getChildren().size() == 0){//没有孩子
                            //修改名称等，变为最后一个
                            ComponentUtil.EditTextEnable(true,timeNeed);
                            setTimeNeeded.setEnabled(true);

                        }else {//有孩子
                            //只能修改名称等

                        }
                    }
                    ComponentUtil.EditTextEnable(true,nameEditText);
                    ComponentUtil.EditTextEnable(true,decoration);
                    setAble(endDate);
                    setAble(startDate);
                    isEdit = true;
                    positiveButton.setText("确定");

                }
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit == true){//编辑状态
                    isEdit = false;
                    positiveButton.setText("编辑");
                    nameEditText.setText(parent.getName());
                    decoration.setText(parent.getDecoration());
                    startDate.setText(TimeUtil.getDate(parent.getStartTime()));
                    endDate.setText(TimeUtil.getDate(parent.getEndTime()));
                    timeNeed.setText(parent.getTimeNeeded()+"");
                    ComponentUtil.EditTextEnable(false,nameEditText);
                    ComponentUtil.EditTextEnable(false,decoration);
                    ComponentUtil.EditTextEnable(false,timeNeed);
                    setUnable(startDate);
                    setUnable(endDate);
                    setTimeNeeded.setEnabled(false);
                }else {//详情状态
                    dialog.dismiss();
                }
            }
        });
    }

    public void showAddDialog(PlanNode parent, DataChangeListener dataChange){
        View root = LayoutInflater.from(context).inflate(R.layout.plantarget_dialog_add_plannode, null);
        MaterialEditText nameEditText = root.findViewById(R.id.addPlanDialog_name_MaterialEditText);
        EditText decoration = root.findViewById(R.id.addPlanDialog_decoration_editText);
        EditText startDate = root.findViewById(R.id.addPlanDialog_startDate_editText);
        EditText endDate = root.findViewById(R.id.addPlanDialog_endDate_editText);
        Switch setTimeNeeded = root.findViewById(R.id.addPlanDialog_setTimeNeeded_switch);
        ConstraintLayout container = root.findViewById(R.id.addPlanDialog_addPlanChild_container);
        MaterialEditText timeNeed = root.findViewById(R.id.addPlanDialog_setTimeNeeded_materialEditText);

        setTimeNeeded.setChecked(!parent.isHasChildren());
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
                    parent.setChildren(false, time);
                    dao.updatePlanNode(parent);
                    dataChange.refresh();
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
                dao.addChild(parent, planNode);
                dataChange.refresh();
                dialog.dismiss();
            }
        });
    }

    public void setUnable(EditText mEditText){
        mEditText.setKeyListener(null);//不可粘贴，长按不会弹出粘贴框
        mEditText.setFocusable(false);//不可编辑
        mEditText.setEnabled(false);

    }

    public void setAble(EditText mEditText){
        mEditText.setFocusable(true);//不可编辑
        mEditText.setEnabled(true);
    }
    public static interface DataChangeListener {
        void refresh();
    }


}
