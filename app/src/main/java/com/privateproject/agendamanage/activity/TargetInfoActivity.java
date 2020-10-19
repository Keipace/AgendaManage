package com.privateproject.agendamanage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityTargetInfoBinding;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TargetInfoActivity extends AppCompatActivity {
    private ActivityTargetInfoBinding pageBinding;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private TargetDao targetDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBinding = ActivityTargetInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(pageBinding.getRoot());
        this.targetDao = new TargetDao(this);

        int id = getIntent().getIntExtra("id", -1);
        if(!setPageContent(id)) {
            Intent intent = new Intent(this, MainActivity.class);
            ToastUtil.newToast(this, "打开页面失败");
            startActivity(intent);
        }
        inputEditable(false);
    }

    private boolean isEditing = false;
    // 根据id查询数据库并将查询到的数据填充到页面中
    public boolean setPageContent(int id) {
        if(id==-1)
            return false;
        Target target = targetDao.selectById(id);
        pageBinding.targetInfoIdTextView.setText(""+id);
        showTarget(target);
        pageBinding.targetInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditing) {//完成按钮
                    /*根据textView存的id来查询target
                     * 获取页面的输入信息
                     * 修改数据库信息
                     * 查询数据库信息并显示在页面上*/
                    try {
                        pageBinding.targetInfoEditButton.setText("编辑");
                        pageBinding.targetInfoBackButton.setText("返回");
                        inputEditable(false);

                        Target target1 = targetDao.selectById(Integer.parseInt(pageBinding.targetInfoIdTextView.getText().toString()));
                        target1 = getInput(target1);
                        targetDao.updateTarget(target1);
                        showTarget(targetDao.selectById(target1.getId()));
                        isEditing = false;
                    } catch (ParseException p) {
                        ToastUtil.newToast(TargetInfoActivity.this, "错误");
                        Intent intent = new Intent(TargetInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {//返回按钮
                    Intent intent = new Intent(TargetInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        pageBinding.targetInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*默认状态下是编辑、返回，编辑状态下是取消、完成*/
                if (isEditing) {
                    inputEditable(false);
                    pageBinding.targetInfoEditButton.setText("编辑");
                    pageBinding.targetInfoBackButton.setText("返回");
                    isEditing = false;
                } else {
                    inputEditable(true);
                    pageBinding.targetInfoEditButton.setText("取消");
                    pageBinding.targetInfoBackButton.setText("完成");
                    isEditing = true;
                }
            }
        });
        return true;
    }

    // 设置页面的输入框可以编辑
    public void inputEditable(boolean enable) {
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoNameEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoDecorationEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoTimeNeedEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoTimePlanOverEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoTimeDeadLineEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoTimePreDoEditView);
        ComponentUtil.EditTextEnable(enable, pageBinding.targetInfoTimeRealOverEditView);
        pageBinding.targetInfoImportanceRadioGroup.setClickable(enable);
    }

    public Target getInput(Target target) throws ParseException {
        int importance = Target.IMPORTANCE_MIDDLE;
        if (pageBinding.targetInfoImportanceRadioGroup.getCheckedRadioButtonId()==R.id.targetInfo_low_radioButton)
            importance = Target.IMPORTANCE_LOW;
        else if(pageBinding.targetInfoImportanceRadioGroup.getCheckedRadioButtonId()==R.id.targetInfo_high_radioButton)
            importance = Target.IMPORTANCE_HIGH;
        /*String name, String decoration, double timeNeed, Date timePlanOver, Date timeDeadLine, Date timeRealOver, int importance, Date timePreDo*/
        target.setImportance(importance);
        target.setName(pageBinding.targetInfoNameEditView.getText().toString());
        target.setDecoration(pageBinding.targetInfoDecorationEditView.getText().toString());
        target.setTimeDeadLine(sdf.parse(pageBinding.targetInfoTimeDeadLineEditView.getText().toString()));
        target.setTimeNeed(Double.parseDouble(pageBinding.targetInfoTimeNeedEditView.getText().toString()));
        target.setTimePlanOver(sdf.parse(pageBinding.targetInfoTimePlanOverEditView.getText().toString()));
        target.setTimePreDo(sdf.parse(pageBinding.targetInfoTimePreDoEditView.getText().toString()));
        target.setTimeRealOver(sdf.parse(pageBinding.targetInfoTimeRealOverEditView.getText().toString()));
        return target;
    }

    public void showTarget(Target target) {
        pageBinding.targetInfoNameEditView.setText(target.getName());
        pageBinding.targetInfoDecorationEditView.setText(target.getDecoration());
        pageBinding.targetInfoTimeNeedEditView.setText(""+target.getTimeNeed());
        pageBinding.targetInfoTimePlanOverEditView.setText(sdf.format(target.getTimePlanOver()));
        pageBinding.targetInfoTimeDeadLineEditView.setText(sdf.format(target.getTimeDeadLine()));
        pageBinding.targetInfoTimePreDoEditView.setText(sdf.format(target.getTimePreDo()));
        pageBinding.targetInfoTimeRealOverEditView.setText(sdf.format(target.getTimeRealOver()));
        switch (target.getImportanceRealNum()) {
            case Target.IMPORTANCE_HIGH:
                pageBinding.targetInfoImportanceRadioGroup.check(R.id.targetInfo_high_radioButton);
                break;
            case Target.IMPORTANCE_LOW:
                pageBinding.targetInfoImportanceRadioGroup.check(R.id.targetInfo_low_radioButton);
                break;
            default:
                pageBinding.targetInfoImportanceRadioGroup.check(R.id.targetInfo_middle_radioButton);
        }
    }
}