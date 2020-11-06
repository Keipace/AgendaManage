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

public class
TargetDialog extends Dialog implements View.OnClickListener {
    public TextView TargetTitle;
    public EditText targetName,targetDecoration;
    public RaiflatButton cancelBtn,confirmBtn;
    private String title,cancel,confirm;
    private TargetDialog.IOnCancelListener cancelListener;
    private TargetDialog.IOnConfirmListener confirmListener;

    public TargetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public TargetDialog setDayTargetTitle(String title) {
        this.title = title;
        return this;
    }

    public TargetDialog setCancelBtn(String cancel, TargetDialog.IOnCancelListener listener) {
        this.cancel = cancel;
        this.cancelListener=listener;
        return this;
    }

    public TargetDialog setConfirmBtn(String confirm, TargetDialog.IOnConfirmListener listener) {
        this.confirm = confirm;
        this.confirmListener=listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_main_addtarget);
        //设置宽度
        Display display=getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params=getWindow().getAttributes();
        Point size=new Point();
        display.getSize(size);
        params.width= (int) (size.x*0.8);//设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(params);

        //获取控件
        TargetTitle = findViewById(R.id.target_title_textView);
        cancelBtn = findViewById(R.id.target_cancel_textView);
        confirmBtn = findViewById(R.id.target_confirm_textView);
        targetName=findViewById(R.id.target_name_editText);
        targetDecoration=findViewById(R.id.target_decoration_editText);

        //给控件填内容
        if (!TextUtils.isEmpty(title)) {
            TargetTitle.setText(title);
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
        switch (v.getId()){
            case R.id.target_cancel_textView:
                if (cancelListener!=null)
                    cancelListener.OnCancel(this);
                break;
            case R.id.target_confirm_textView:
                if (confirmListener!=null)
                    confirmListener.OnConfirm(this);
                break;
        }
    }

    //监听器接口
    public interface IOnCancelListener{
        void OnCancel(TargetDialog dialog);
    }
    public interface IOnConfirmListener{
        void OnConfirm(TargetDialog dialog);
    }
}
