package com.privateproject.agendamanage.server;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.activity.DayTargetInfoActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityDayTargetInfoBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.DayTargetUtil;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InfoPageServer {

    private ActivityDayTargetInfoBinding pageBinding;
    private Context context;

    private DayTargetDao dayTargetDao;
    private TargetDao targetDao;


    public InfoPageServer(Context context,ActivityDayTargetInfoBinding pageBinding) {
        this.pageBinding = pageBinding;
        this.context = context;
        this.targetDao = new TargetDao(context);
        this.dayTargetDao = new DayTargetDao(context);
    }

    private boolean isDayTargetInfoEditing = false;
    // 根据id查询数据库并将查询到的数据填充到页面中
    public boolean setDayTargetInfoPageContent(int id) {
        if(id==-1)
            return false;
        DayTarget dayTarget = dayTargetDao.selectById(id);
        pageBinding.DaytargetInfoIdTextView.setText(""+id);
        showDayTarget(dayTarget);



        pageBinding.DaytargetInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pageBinding.DaytargetInfoBackButton.setFocusableInTouchMode(true);
                pageBinding.DaytargetInfoBackButton.setFocusable(true);
                pageBinding.DaytargetInfoBackButton.requestFocus();


                try {
                    boolean success = convertDayTargetMessage();
                    if(success){
                        if(isDayTargetInfoEditing) {//完成按钮
                            /*根据textView存的id来查询target
                             * 获取页面的输入信息
                             * 修改数据库信息
                             * 查询数据库信息并显示在页面上*/
                            try {
                                pageBinding.DaytargetInfoEditButton.setText("编辑");
                                pageBinding.DaytargetInfoBackButton.setText("返回");
                                inputDayEditable(false);

                                DayTarget dayTarget1 = dayTargetDao.selectById(Integer.parseInt(pageBinding.DaytargetInfoIdTextView.getText().toString()));
                                dayTarget1 = getInput(dayTarget1);
                                dayTargetDao.updateDayTarget(dayTarget1);
                                showDayTarget(dayTargetDao.selectById(dayTarget1.getId()));
                                isDayTargetInfoEditing = false;
                            } catch (ParseException p) {
                                ToastUtil.newToast(context, "错误");
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            }
                        } else {//返回按钮
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        }
                    }else{
                        ToastUtil.newToast(context, "请输入完整信息");
                    }
                } catch (ParseException e) {
                    ToastUtil.newToast(context, "添加失败");
                    e.printStackTrace();
                }

            }
        });
        pageBinding.DaytargetInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*默认状态下是编辑、返回，编辑状态下是取消、完成*/
                if (isDayTargetInfoEditing) {
                    inputDayEditable(false);
                    //设置时间选择器不可以弹出
                    pageBinding.DaytargetInfoTimeFragmentStartEditText.setOnTouchListener(null);
                    pageBinding.DaytargetInfoTimeFragmentEndEditText.setOnTouchListener(null);
                    pageBinding.DaytargetInfoEditButton.setText("编辑");
                    pageBinding.DaytargetInfoBackButton.setText("返回");
                    isDayTargetInfoEditing = false;
                } else {
                    inputDayEditable(true);

                    // DayTarget详情页的“名称”不能为空，当焦点发生变化时触发监听器
                    pageBinding.DaytargetInfoNameEditView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            pageBinding.DaytargetInfoNameEditView.setText(StringUtils.moveSpaceString(pageBinding.DaytargetInfoNameEditView.getText().toString()));
                        }
                    });
                    // DayTarget详情页的“描述”不能为空，当焦点发生变化时触发监听器
                    pageBinding.DaytargetInfoDecorationEditView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                // 此处为得到焦点时的处理内容
                            } else {
                                // 此处为失去焦点时的处理内容
                                pageBinding.DaytargetInfoDecorationEditView.setText(StringUtils.moveSpaceString(pageBinding.DaytargetInfoDecorationEditView.getText().toString()));
                            }
                        }
                    });

                    // 计划完成次数，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
                    pageBinding.DaytargetInfoPlanCountsEditView.addTextChangedListener(
                            DayTargetUtil.getTextWatcher(pageBinding.DaytargetInfoPlanCountsEditView));
                    pageBinding.DaytargetInfoPlanCountsEditView.setOnFocusChangeListener(
                            DayTargetUtil.getNotZeroFocusChangeListener(pageBinding.DaytargetInfoPlanCountsEditView));

                    // 频率，输入的时候处理开头的0和只允许一位小数；失去焦点时处理只输入一个0和结尾是小数点的问题
                    pageBinding.DaytargetInfoFrequencyEditView.addTextChangedListener(
                            DayTargetUtil.getTextWatcher(pageBinding.DaytargetInfoFrequencyEditView));
                    pageBinding.DaytargetInfoFrequencyEditView.setOnFocusChangeListener(
                            DayTargetUtil.getNotZeroFocusChangeListener(pageBinding.DaytargetInfoFrequencyEditView));

                    // 开始时间和结束时间，点击时弹出选择时间的提示框
                    pageBinding.DaytargetInfoTimeFragmentStartEditText.setOnTouchListener(DayTargetUtil.getOnTimeTouchListener(
                            context,pageBinding.DaytargetInfoTimeFragmentStartEditText, pageBinding.DaytargetInfoTimeFragmentEndEditText,true));
                    pageBinding.DaytargetInfoTimeFragmentEndEditText.setOnTouchListener(DayTargetUtil.getOnTimeTouchListener(
                            context,pageBinding.DaytargetInfoTimeFragmentStartEditText, pageBinding.DaytargetInfoTimeFragmentEndEditText,false));
                    pageBinding.DaytargetInfoEditButton.setText("取消");
                    pageBinding.DaytargetInfoBackButton.setText("完成");



                    isDayTargetInfoEditing = true;
                }
            }
        });
        return true;
    }
    private boolean convertDayTargetMessage() throws ParseException {
        // 判断必要项是否有空的
        if (pageBinding.DaytargetInfoNameEditView.getText().toString().equals("")
                || pageBinding.DaytargetInfoFrequencyEditView.getText().toString().equals("")
                || pageBinding.DaytargetInfoTimeFragmentStartEditText.getText().toString().equals("")
                || pageBinding.DaytargetInfoTimeFragmentEndEditText.getText().toString().equals("")
                || pageBinding.DaytargetInfoPlanCountsEditView.getText().toString().equals(""))
            return false;
        else
            return true;
    }
    // 设置页面的输入框可以编辑
    public void inputDayEditable(boolean enable) {
        ComponentUtil.EditTextEnable(enable, pageBinding.DaytargetInfoNameEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.DaytargetInfoDecorationEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.DaytargetInfoFrequencyEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.DaytargetInfoPlanCountsEditView);
    }

    //把用户编辑过后的详情页数据提交到数据库
    public DayTarget getInput(DayTarget dayTarget) throws ParseException {
        dayTarget.setName(pageBinding.DaytargetInfoNameEditView.getText().toString());
        dayTarget.setDecoration(pageBinding.DaytargetInfoDecorationEditView.getText().toString());
        dayTarget.setFrequency(Integer.parseInt(pageBinding.DaytargetInfoFrequencyEditView.getText().toString()));
        dayTarget.setTimeFragmentStart(pageBinding.DaytargetInfoTimeFragmentStartEditText.getText().toString());
        dayTarget.setTimeFragmentEnd(pageBinding.DaytargetInfoTimeFragmentEndEditText.getText().toString());
        dayTarget.setPlanCounts(Integer.parseInt(pageBinding.DaytargetInfoPlanCountsEditView.getText().toString()));
        dayTarget.setDoneCounts(Integer.parseInt(pageBinding.DaytargetInfoDoneCountsEditView.getText().toString()));

        return dayTarget;
    }

    public void showDayTarget(DayTarget dayTarget) {
        pageBinding.DaytargetInfoNameEditView.setText(dayTarget.getName());
        pageBinding.DaytargetInfoDecorationEditView.setText(dayTarget.getDecoration());
        pageBinding.DaytargetInfoFrequencyEditView.setText(""+dayTarget.getFrequency());
        pageBinding.DaytargetInfoTimeFragmentStartEditText.setText(dayTarget.getTimeFragmentStart());
        pageBinding.DaytargetInfoTimeFragmentEndEditText.setText(dayTarget.getTimeFragmentEnd());
        pageBinding.DaytargetInfoPlanCountsEditView.setText(""+dayTarget.getPlanCounts());
        pageBinding.DaytargetInfoDoneCountsEditView.setText(""+dayTarget.getDoneCounts());
        pageBinding.DaytargetInfoRemianCountsEditView.setText(""+(dayTarget.getPlanCounts()-dayTarget.getDoneCounts()));
    }

}
