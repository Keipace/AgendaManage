package com.privateproject.agendamanage.server;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.databinding.ActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;


public class InfoPageServer {
    // target详情页需要的属性
    private TargetDao targetDao;

    // dayTarget详情页需要的属性
    private ActivityDayTargetInfoBinding dayTargetpageBinding;
    private Context dayTargetContext;
    private DayTargetDao dayTargetDao;

    // dayTarget详情页需要的构造方法
    public InfoPageServer(Context dayTargetContext,ActivityDayTargetInfoBinding pageBinding) {
        this.dayTargetpageBinding = pageBinding;
        this.dayTargetContext = dayTargetContext;
        this.dayTargetDao = new DayTargetDao(dayTargetContext);
        // 设置页面输入框的约束
        DayTargetUtil.setDayTargetConstraint(pageBinding.DaytargetInfoNameEditView, pageBinding.DaytargetInfoDecorationEditView, pageBinding.DaytargetInfoPlanCountsEditView, pageBinding.DaytargetInfoFrequencyEditView);
    }

    private boolean isDayTargetInfoEditing = false;
    // 根据id查询数据库并将查询到的数据填充到页面中,同时设置监听器
    public boolean setDayTargetInfoPageContent(int id) {
        // 根据id查询数据库
        DayTarget dayTarget = dayTargetDao.selectById(id);
        // 将查询到的信息显示到dayTarget详情页上
        showDayTarget(dayTarget);

        // 设置 返回/完成 按钮的监听器
        this.dayTargetpageBinding.DaytargetInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDayTargetInfoEditing) {//完成按钮
                    // 按钮获取焦点
                    ComponentUtil.ButtonRequestFocus(dayTargetpageBinding.DaytargetInfoBackButton);
                    // 判断必要项是否填写完全
                    boolean success = isDayTargetInfoNull();
                    if(success) { //不为空时
                        /*
                         * 1.修改页面按钮和编辑状态、获取页面的输入信息
                         * 2.修改数据库信息
                         * 3.查询数据库信息并显示在页面上*/
                        // 变换按钮
                        isDayTargetInfoEditing = false;
                        dayTargetpageBinding.DaytargetInfoEditButton.setText("编辑");
                        dayTargetpageBinding.DaytargetInfoBackButton.setText("返回");
                        // 设置页面不可编辑
                        inputDayEditable(false);
                        DayTarget dayTarget1 = dayTargetDao.selectById(id);
                        dayTarget1 = getInput(dayTarget1);
                        // 修改数据库
                        dayTargetDao.updateDayTarget(dayTarget1);
                        // 查询数据库并显示在页面上
                        showDayTarget(dayTargetDao.selectById(id));
                    } else {// 如果必要项为空，则提示用户
                        ToastUtil.newToast(dayTargetContext, "请输入完整信息");
                    }
                } else {//返回按钮，从当前的dayTarget详情页跳转到主页面
                    Intent intent = new Intent(dayTargetContext, MainActivity.class);
                    dayTargetContext.startActivity(intent);
                }
            }
        });
        // 设置 编辑/取消 按钮的监听器
        this.dayTargetpageBinding.DaytargetInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDayTargetInfoEditing) { //取消按钮
                    /*
                    * 1.设置页面不可编辑状态并变换按钮
                    * 2.恢复页面（查询数据库并填充页面）*/
                    isDayTargetInfoEditing = false;
                    inputDayEditable(false);
                    dayTargetpageBinding.DaytargetInfoEditButton.setText("编辑");
                    dayTargetpageBinding.DaytargetInfoBackButton.setText("返回");
                    // 查询数据库并恢复页面
                    showDayTarget(dayTargetDao.selectById(id));
                } else { // 编辑按钮
                    /*
                    * 1.设置页面可编辑状态并变换按钮*/
                    isDayTargetInfoEditing = true;
                    inputDayEditable(true);
                    dayTargetpageBinding.DaytargetInfoEditButton.setText("取消");
                    dayTargetpageBinding.DaytargetInfoBackButton.setText("完成");
                }
            }
        });
        return true;
    }
    // 判断页面必要项是否有空的
    private boolean isDayTargetInfoNull(){
        if (this.dayTargetpageBinding.DaytargetInfoNameEditView.getText().toString().equals("")
                || this.dayTargetpageBinding.DaytargetInfoFrequencyEditView.getText().toString().equals("")
                || this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartEditText.getText().toString().equals("")
                || this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndEditText.getText().toString().equals("")
                || this.dayTargetpageBinding.DaytargetInfoPlanCountsEditView.getText().toString().equals(""))
            return false;
        else
            return true;
    }
    // 设置页面的输入框可以编辑
    public void inputDayEditable(boolean enable) {
        ComponentUtil.EditTextEnable(enable, this.dayTargetpageBinding.DaytargetInfoNameEditView);
        ComponentUtil.EditTextEnable(enable, this.dayTargetpageBinding.DaytargetInfoDecorationEditView);
        ComponentUtil.EditTextEnable(enable, this.dayTargetpageBinding.DaytargetInfoFrequencyEditView);
        ComponentUtil.EditTextEnable(enable, this.dayTargetpageBinding.DaytargetInfoPlanCountsEditView);
        if(enable) {
            // 开始时间和结束时间，点击时弹出选择时间的提示框
            DayTargetUtil.setTimeStartToEnd(this.dayTargetContext, this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartEditText, this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndEditText);
        } else {
            //设置时间选择器不可以弹出
            this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartEditText.setOnTouchListener(null);
            this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndEditText.setOnTouchListener(null);
        }
    }

    // 获取页面输入的信息并包装为DayTarget对象
    public DayTarget getInput(DayTarget dayTarget){
        dayTarget.setName(this.dayTargetpageBinding.DaytargetInfoNameEditView.getText().toString());
        dayTarget.setDecoration(this.dayTargetpageBinding.DaytargetInfoDecorationEditView.getText().toString());
        dayTarget.setFrequency(Integer.parseInt(this.dayTargetpageBinding.DaytargetInfoFrequencyEditView.getText().toString()));
        dayTarget.setTimeFragmentStart(this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartEditText.getText().toString());
        dayTarget.setTimeFragmentEnd(this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndEditText.getText().toString());
        dayTarget.setPlanCounts(Integer.parseInt(this.dayTargetpageBinding.DaytargetInfoPlanCountsEditView.getText().toString()));
        dayTarget.setDoneCounts(Integer.parseInt(this.dayTargetpageBinding.DaytargetInfoDoneCountsEditView.getText().toString()));
        return dayTarget;
    }

    // 将dayTarget的信息显示到dayTarget详情页
    public void showDayTarget(DayTarget dayTarget) {
        this.dayTargetpageBinding.DaytargetInfoNameEditView.setText(dayTarget.getName());
        this.dayTargetpageBinding.DaytargetInfoDecorationEditView.setText(dayTarget.getDecoration());
        this.dayTargetpageBinding.DaytargetInfoFrequencyEditView.setText(""+dayTarget.getFrequency());
        this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartEditText.setText(dayTarget.getTimeFragmentStart());
        this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndEditText.setText(dayTarget.getTimeFragmentEnd());
        this.dayTargetpageBinding.DaytargetInfoPlanCountsEditView.setText(""+dayTarget.getPlanCounts());
        this.dayTargetpageBinding.DaytargetInfoDoneCountsEditView.setText(""+dayTarget.getDoneCounts());
        this.dayTargetpageBinding.DaytargetInfoRemianCountsEditView.setText(""+(dayTarget.getPlanCounts()-dayTarget.getDoneCounts()));
    }

}
