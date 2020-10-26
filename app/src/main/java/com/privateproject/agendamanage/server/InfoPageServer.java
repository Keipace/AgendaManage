package com.privateproject.agendamanage.server;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.TargetInfoActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.databinding.ActivityTargetInfoBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.TargetUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class InfoPageServer {


    // target详情页需要的属性
    private TargetDao targetDao;
    private ActivityTargetInfoBinding targetPageBinding;
    private Context targetContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // target详情页需要的构造方法
    public InfoPageServer(Context targetContext,ActivityTargetInfoBinding pageBinding) {
        this.targetPageBinding = pageBinding;
        this.targetContext = targetContext;
        this.targetDao = new TargetDao(targetContext);
        // 设置页面输入框的约束
        TargetUtil.setTargetConstraint(targetPageBinding.targetInfoTimeNeedEditView, targetPageBinding.targetInfoNameEditView, targetPageBinding.targetInfoDecorationEditView);
    }


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
    public void setDayTargetInfoPageContent(int id) {
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



    private boolean isTargetInfoEditing = false;
    public void setTargetInfoPageContent(int id) {
        // 根据id查询数据库
        Target target = targetDao.selectById(id);
        // 将查询到的信息显示到Target详情页上
        showTarget(target);

        // 设置 返回/完成 按钮的监听器
        targetPageBinding.targetInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTargetInfoEditing) {//完成按钮
                    // 按钮获取焦点
                    ComponentUtil.ButtonRequestFocus(targetPageBinding.targetInfoBackButton);
                    // 判断必要项是否填写完全
                    boolean success = isTargetInfoNull();
                    if (success){//不为空时
                        /*
                         * 1.修改页面按钮和编辑状态、获取页面的输入信息
                         * 2.修改数据库信息
                         * 3.查询数据库信息并显示在页面上*/
                        //变换按钮,设置按钮文字，并设置不可编辑
                        isTargetInfoEditing = false;
                        targetPageBinding.targetInfoEditButton.setText("编辑");
                        targetPageBinding.targetInfoBackButton.setText("返回");
                        //设置页面不可编辑
                        inputTargetEditable(false);
                        Target target1 = targetDao.selectById(id);
                        try {
                            target1 = getInput(target1);
                        } catch (ParseException e) {
                            //日期转字符串类型转换错误
                            e.printStackTrace();
                        }
                        //修改数据库
                        targetDao.updateTarget(target1);
                        // 查询数据库并显示在页面上
                        showTarget(targetDao.selectById(target1.getId()));
                    }else {// 如果必要项为空，则提示用户
                        ToastUtil.newToast(targetContext, "请输入完整信息");
                    }
                } else {//返回按钮，从当前的target详情页跳转到主页面
                    Intent intent = new Intent(targetContext, MainActivity.class);
                    targetContext.startActivity(intent);
                }
            }
        });

        // 设置 编辑/取消 按钮的监听器
        targetPageBinding.targetInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTargetInfoEditing) {//取消按钮
                    /*
                     * 1.设置页面不可编辑状态并变换按钮
                     * 2.恢复页面（查询数据库并填充页面）*/
                    isTargetInfoEditing = false;
                    inputTargetEditable(false);
                    targetPageBinding.targetInfoEditButton.setText("编辑");
                    targetPageBinding.targetInfoBackButton.setText("返回");
                    // 查询数据库并恢复页面
                    showTarget(targetDao.selectById(id));
                } else {// 编辑按钮
                    /*
                     * 1.设置页面可编辑状态并变换按钮*/
                    isTargetInfoEditing = true;
                    inputTargetEditable(true);
                    targetPageBinding.targetInfoEditButton.setText("取消");
                    targetPageBinding.targetInfoBackButton.setText("完成");
                }
            }
        });
    }

    // 设置页面的输入框可以编辑
    public void inputTargetEditable(boolean enable) {
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoNameEditView);
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoDecorationEditView);
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoTimeNeedEditView);
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoTimePlanOverEditView);
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoTimeDeadLineEditView);
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoTimePreDoEditView);
        ComponentUtil.EditTextEnable(enable, targetPageBinding.targetInfoTimeRealOverEditView);
//        pageBinding.targetInfoImportanceRadioGroup.setClickable(enable);
        for (int i = 0; i < targetPageBinding.targetInfoImportanceRadioGroup.getChildCount(); i++) {
            targetPageBinding.targetInfoImportanceRadioGroup.getChildAt(i).setEnabled(enable);
        }
        if(enable) {
            TargetUtil.setPreDoPlanDeadLineTimeEditText(targetContext,targetPageBinding.targetInfoTimePlanOverEditView,targetPageBinding.targetInfoTimeDeadLineEditView,targetPageBinding.targetInfoTimePreDoEditView);
             targetPageBinding.targetInfoTimeRealOverEditView.setOnTouchListener(
                    TargetUtil.getOnTouchListener(targetContext,targetPageBinding.targetInfoTimeRealOverEditView));

        }else {
            //取消期待完成时间、最晚完成时间、预实施时间、实际完成时间的setOnTouchListener监听器
            targetPageBinding.targetInfoTimePlanOverEditView.setOnTouchListener(null);
            targetPageBinding.targetInfoTimeDeadLineEditView.setOnTouchListener(null);
            targetPageBinding.targetInfoTimePreDoEditView.setOnTouchListener(null);
            targetPageBinding.targetInfoTimeRealOverEditView.setOnTouchListener(null);
        }


    }

    //记录下填写的信息，填入数据库时使用
    public Target getInput(Target target) throws ParseException {
        int importance = -1;
        if (targetPageBinding.targetInfoImportanceRadioGroup.getCheckedRadioButtonId()== R.id.targetInfo_low_radioButton){
            importance = Target.IMPORTANCE_LOW;
        } else if(targetPageBinding.targetInfoImportanceRadioGroup.getCheckedRadioButtonId()==R.id.targetInfo_high_radioButton){
            importance = Target.IMPORTANCE_HIGH;
        }else if(targetPageBinding.targetInfoImportanceRadioGroup.getCheckedRadioButtonId()==R.id.targetInfo_middle_radioButton){
            importance = Target.IMPORTANCE_MIDDLE;
        }
        /*String name, String decoration, double timeNeed, Date timePlanOver, Date timeDeadLine, Date timeRealOver, int importance, Date timePreDo*/
        target.setImportance(importance);
        target.setName(targetPageBinding.targetInfoNameEditView.getText().toString());
        target.setDecoration(targetPageBinding.targetInfoDecorationEditView.getText().toString());
        target.setTimeNeed(Double.parseDouble(targetPageBinding.targetInfoTimeNeedEditView.getText().toString()));
        target.setTimeDeadLine(sdf.parse(targetPageBinding.targetInfoTimeDeadLineEditView.getText().toString()));
        target.setTimePlanOver(sdf.parse(targetPageBinding.targetInfoTimePlanOverEditView.getText().toString()));
        target.setTimePreDo(sdf.parse(targetPageBinding.targetInfoTimePreDoEditView.getText().toString()));
        target.setTimeRealOver(sdf.parse(targetPageBinding.targetInfoTimeRealOverEditView.getText().toString()));
        return target;
    }

    //显示出target的信息（用作还未存到数据库时）
    public void showTarget(Target target) {
        targetPageBinding.targetInfoNameEditView.setText(target.getName());
        if(target.getDecoration().equals(Target.DEFAULT_DECORATION)){
            targetPageBinding.targetInfoDecorationEditView.setText("");
        }else {
            targetPageBinding.targetInfoDecorationEditView.setText(target.getDecoration());
        }
        if (target.getTimeNeed()==-1){
            targetPageBinding.targetInfoTimeNeedEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimeNeedEditView.setText(""+target.getTimeNeed());
        }
        if(target.getTimePlanOver()==null){
            targetPageBinding.targetInfoTimePlanOverEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimePlanOverEditView.setText(sdf.format(target.getTimePlanOver()));
        }

        if(target.getTimeDeadLine()==null){
            targetPageBinding.targetInfoTimeDeadLineEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimeDeadLineEditView.setText(sdf.format(target.getTimeDeadLine()));
        }

        if(target.getTimePreDo()==null){
            targetPageBinding.targetInfoTimePreDoEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimePreDoEditView.setText(sdf.format(target.getTimePreDo()));
        }

        if(target.getTimeRealOver()==null){
            targetPageBinding.targetInfoTimeRealOverEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimeRealOverEditView.setText(sdf.format(target.getTimeRealOver()));
        }
        switch (target.getImportanceRealNum()) {
            case Target.IMPORTANCE_HIGH:
                targetPageBinding.targetInfoImportanceRadioGroup.check(R.id.targetInfo_high_radioButton);
                break;
            case Target.IMPORTANCE_LOW:
                targetPageBinding.targetInfoImportanceRadioGroup.check(R.id.targetInfo_low_radioButton);
                break;
            case Target.IMPORTANCE_MIDDLE:
                targetPageBinding.targetInfoImportanceRadioGroup.check(R.id.targetInfo_middle_radioButton);
                break;
            default:
                return;
        }
    }

    // 判断必要项是否有空的
    private boolean isTargetInfoNull() {
        if(targetPageBinding.targetInfoNameEditView.getText().toString().equals("")
                || targetPageBinding.targetInfoTimeNeedEditView.getText().toString().equals("")
                || targetPageBinding.targetInfoTimePlanOverEditView.getText().toString().equals("")
                || targetPageBinding.targetInfoTimeDeadLineEditView.getText().toString().equals("")
                || targetPageBinding.targetInfoTimePreDoEditView.getText().toString().equals("")
                || targetPageBinding.targetInfoTimeRealOverEditView.getText().toString().equals("")){
            return false;
        }
        return true;
    }

}
