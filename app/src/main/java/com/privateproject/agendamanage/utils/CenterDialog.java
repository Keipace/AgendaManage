package com.privateproject.agendamanage.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.privateproject.agendamanage.R;

public class CenterDialog extends Dialog implements View.OnClickListener {
    public TextView titleTextView;
    public RaiflatButton cancelBtn, confirmBtn;
    public ConstraintLayout constraintLayout;
    public View view;
    public String position="Center";
    public String title, cancel, confirm;
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public CenterDialog(@NonNull Context context) {
        super(context);
    }

    public CenterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    public CenterDialog(@NonNull Context context, int themeResId, String position) {
        super(context, themeResId);
        this.position=position;
    }

    public CenterDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CenterDialog setCancelBtn(String cancel, IOnCancelListener listener) {
        this.cancel = cancel;
        this.cancelListener = listener;
        return this;
    }

    public CenterDialog setView(View view) {
        this.view=view;
        return this;
    }

    public CenterDialog setConfirmBtn(String confirm, IOnConfirmListener listener) {
        this.confirm = confirm;
        this.confirmListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(position.equals("Bottom")){
            //底部dialog
            setContentView(R.layout.utils_item_bottom_dialog);
            //设置宽度
            WindowManager manager = getWindow().getWindowManager();
            Display display = manager.getDefaultDisplay();
            WindowManager.LayoutParams params = getWindow().getAttributes();
            Point size = new Point();
            display.getSize(size);
            params.width = (int) (size.x);//设置dialog的宽度为当前手机屏幕的宽度
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setAttributes(params);

            titleTextView=findViewById(R.id.title_textView);
            cancelBtn=findViewById(R.id.cancel_textView);
            confirmBtn=findViewById(R.id.confirm_textView);
            constraintLayout=findViewById(R.id.message_layout);
            //给控件填内容
            if (!TextUtils.isEmpty(title)) {
                titleTextView.setText(title);
            }
            if (!TextUtils.isEmpty(cancel)) {
                cancelBtn.setText(cancel);
            }
            if (!TextUtils.isEmpty(confirm)) {
                confirmBtn.setText(confirm);
            }
            if (view!=null) {
                constraintLayout.addView(view, new WindowManager.LayoutParams());
            }
            cancelBtn.setOnClickListener(this);
            confirmBtn.setOnClickListener(this);
        }else{
            //中部dialog
            setContentView(R.layout.utils_item_center_dialog);
            //设置宽度
            WindowManager manager = getWindow().getWindowManager();
            Display display = manager.getDefaultDisplay();
            WindowManager.LayoutParams params = getWindow().getAttributes();
            Point size = new Point();
            display.getSize(size);
            params.width = (int) (size.x * 0.8);//设置dialog的宽度为当前手机屏幕的宽度
            getWindow().setAttributes(params);

            titleTextView=findViewById(R.id.title_textView);
            cancelBtn=findViewById(R.id.cancel_textView);
            confirmBtn=findViewById(R.id.confirm_textView);
            constraintLayout=findViewById(R.id.message_layout);
            //给控件填内容
            if (!TextUtils.isEmpty(title)) {
                titleTextView.setText(title);
            }
            if (!TextUtils.isEmpty(cancel)) {
                cancelBtn.setText(cancel);
            }
            if (!TextUtils.isEmpty(confirm)) {
                confirmBtn.setText(confirm);
            }
            if (view!=null) {
                constraintLayout.addView(view, new WindowManager.LayoutParams());
            }
            cancelBtn.setOnClickListener(this);
            confirmBtn.setOnClickListener(this);
        }


    }

    //执行监听器事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_textView:
                if (cancelListener != null)
                    cancelListener.OnCancel(this);
                break;
            case R.id.confirm_textView:
                if (confirmListener != null)
                    confirmListener.OnConfirm(this);
                break;
        }
    }

    //监听器接口
    public interface IOnCancelListener {
        void OnCancel(CenterDialog dialog);
    }
    public interface IOnConfirmListener {
        void OnConfirm(CenterDialog dialog);
    }
}
