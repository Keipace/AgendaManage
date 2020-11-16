package com.privateproject.agendamanage.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;

public class DayTargetDialog extends Dialog implements View.OnClickListener {
    public TextView dayTargetTitle;
    public RaiflatButton cancelBtn, confirmBtn;
    public String title, cancel, confirm;
    public EditText dayTargetDayName,dayTargetDayDecoration;
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public DayTargetDialog(@NonNull Context context) {
        super(context);
    }

    public DayTargetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public DayTargetDialog setDayTargetTitle(String title) {
        this.title = title;
        return this;
    }

    public DayTargetDialog setCancelBtn(String cancel, IOnCancelListener listener) {
        this.cancel = cancel;
        this.cancelListener = listener;
        return this;
    }

    public DayTargetDialog setConfirmBtn(String confirm, IOnConfirmListener listener) {
        this.confirm = confirm;
        this.confirmListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_main_adddaytarget);
        //设置宽度
        WindowManager manager = getWindow().getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point size = new Point();
        display.getSize(size);
        params.width = (int) (size.x * 0.8);//设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(params);

        dayTargetTitle=findViewById(R.id.daytarget_title_textView);
        cancelBtn=findViewById(R.id.daytarget_cancel_textView);
        confirmBtn=findViewById(R.id.daytarget_confirm_textView);
        dayTargetDayName=findViewById(R.id.daytarget_day_name_editText);
        dayTargetDayDecoration=findViewById(R.id.daytarget_day_decoration_editText);

        //给控件填内容
        if (!TextUtils.isEmpty(title)) {
            dayTargetTitle.setText(title);
        }
        if (!TextUtils.isEmpty(cancel)) {
            cancelBtn.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            confirmBtn.setText(confirm);
        }
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

    }

    //执行监听器事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.daytarget_cancel_textView:
                if (cancelListener != null)
                    cancelListener.OnCancel(this);
                break;
            case R.id.daytarget_confirm_textView:
                if (confirmListener != null)
                    confirmListener.OnConfirm(this);
                break;
        }
    }

    //监听器接口
    public interface IOnCancelListener {
        void OnCancel(DayTargetDialog dialog);
    }
    public interface IOnConfirmListener {
        void OnConfirm(DayTargetDialog dialog);
    }
}
