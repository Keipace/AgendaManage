package com.privateproject.agendamanage.server;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.databinding.ActivityTargetInfoBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.ToastUtil;

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
    }

    // 根据id查询数据库并将查询到的数据填充到页面中,同时设置监听器
    public void setDayTargetInfoPageContent(int id) {
        // 根据id查询数据库
        DayTarget dayTarget = dayTargetDao.selectById(id);
        // 将查询到的信息显示到dayTarget详情页上
        showDayTarget(dayTarget);

        // 设置 返回 按钮的监听器
        this.dayTargetpageBinding.DaytargetInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)dayTargetContext).finish();
            }
        });
        // 设置 编辑 按钮的监听器
        this.dayTargetpageBinding.DaytargetInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.newToast(dayTargetContext,"编辑");
            }
        });
    }

    // 将dayTarget的信息显示到dayTarget详情页
    public void showDayTarget(DayTarget dayTarget) {
        this.dayTargetpageBinding.DaytargetInfoNameTextView.setText(dayTarget.getName());
        if (dayTarget.getDecoration().equals(DayTarget.DEFAULT_DECORATION)){
            //this.dayTargetpageBinding.DaytargetInfoDecorationEditView.setText("");
        } else {
            this.dayTargetpageBinding.DaytargetInfoDecorationEditView.setText(dayTarget.getDecoration());
        }

        if (dayTarget.getFrequency() == DayTarget.DEFAULT_FREQUENCY){
            //this.dayTargetpageBinding.DaytargetInfoFrequencyEditView.setText("");
        }else{
            this.dayTargetpageBinding.DaytargetInfoFrequencyEditView.setText(""+dayTarget.getFrequency());
        }

        if (dayTarget.getTimeFragmentStart().equals(DayTarget.DEFAULT_TIMEFRAGMENTSTART)){
            //this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartTextView.setText("");
        }else{
            this.dayTargetpageBinding.DaytargetInfoTimeFragmentStartTextView.setText(dayTarget.getTimeFragmentStart());
        }

        if (dayTarget.getTimeFragmentEnd().equals(DayTarget.DEFAULT_TIMEFRAGMENTEND)){
            //this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndTextView.setText("");
        }else{
            this.dayTargetpageBinding.DaytargetInfoTimeFragmentEndTextView.setText(dayTarget.getTimeFragmentEnd());
        }

        if (dayTarget.getPlanCounts() == DayTarget.DEFAULT_PLANCOUNTS){
            //this.dayTargetpageBinding.DaytargetInfoPlanCountsEditView.setText("");
            //this.dayTargetpageBinding.DaytargetInfoRemianCountsEditView.setText("");
        } else {
            this.dayTargetpageBinding.DaytargetInfoPlanCountsEditView.setText(""+dayTarget.getPlanCounts());
            this.dayTargetpageBinding.DaytargetInfoRemianCountsEditView.setText(""+(dayTarget.getPlanCounts()-dayTarget.getDoneCounts()));
        }

        if (dayTarget.getDoneCounts() == DayTarget.DEFAULT_DONECOUNTS){
            //this.dayTargetpageBinding.DaytargetInfoDoneCountsEditView.setText("");
        }else{
            this.dayTargetpageBinding.DaytargetInfoDoneCountsEditView.setText(""+dayTarget.getDoneCounts());
        }
    }



    public void setTargetInfoPageContent(int id) {
        // 根据id查询数据库
        Target target = targetDao.selectById(id);
        // 将查询到的信息显示到Target详情页上
        showTarget(target);

        // 设置 返回 按钮的监听器
        targetPageBinding.targetInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)targetContext).finish();
            }
        });

        // 设置 编辑 按钮的监听器
        targetPageBinding.targetInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.newToast(targetContext,"编辑");
            }
        });
    }

    //显示出target的信息（用作还未存到数据库时）
    public void showTarget(Target target) {
        targetPageBinding.targetInfoNameTextView.setText(target.getName());
        if(target.getDecoration().equals(Target.DEFAULT_DECORATION)){
            //targetPageBinding.targetInfoDecorationEditView.setText("");
        }else {
            targetPageBinding.targetInfoDecorationEditView.setText(target.getDecoration());
        }
        if (target.getTimeNeed()==Target.DEFAULT_TIMENEED){
            //targetPageBinding.targetInfoTimeNeedEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimeNeedEditView.setText(""+target.getTimeNeed());
        }
        if(target.getTimePlanOver()==null){
           // targetPageBinding.targetInfoTimePlanOverEditView.setText("");
        }else {
            //targetPageBinding.targetInfoTimePlanOverEditView.setText(sdf.format(target.getTimePlanOver()));
        }

        if(target.getTimeDeadLine()==null){
            //targetPageBinding.targetInfoTimeDeadLineEditView.setText("");
        }else {
            targetPageBinding.targetInfoTimeDeadLineEditView.setText(sdf.format(target.getTimeDeadLine()));
        }

        if(target.getTimePreDo()==null){
            //targetPageBinding.targetInfoTimePreDoEditView.setText("");
        }else {
            //targetPageBinding.targetInfoTimePreDoEditView.setText(sdf.format(target.getTimePreDo()));
        }

        if(target.getTimeRealOver()==null){
            //targetPageBinding.targetInfoTimeRealOverEditView.setText("");
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

}
