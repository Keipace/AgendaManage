package com.privateproject.agendamanage.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlipDialog extends Dialog implements View.OnClickListener {
    private ImageButton setTime;
    private ImageButton back;
    private ImageButton cancelBtn,confirmBtn;
    private PickerView startHour,startMinute,endHour,endMinute;
    private String startHourStr="12",startMinuteStr="30",endHourStr="12",endMinuteStr="30";
    private Time startTime,endTime;
    private MaterialEditText taskName;
    private EditText taskDeco;
    private ConstraintLayout container;
    private ConstraintLayout nameContainer;
    private ConstraintLayout timeContainer;

    private Context context;

    //接口回调传递参数
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    private int centerX;
    private int centerY;
    private int depthZ = 700;//修改此处可以改变距离来达到你满意的效果
    private int duration = 300;//动画时间
    private Rotate3dAnimation openAnimation;
    private Rotate3dAnimation closeAnimation;

    private boolean isOpen = false;

    public FlipDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    public FlipDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
    }

    public FlipDialog setCancelBtn(IOnCancelListener listener) {
        this.cancelListener = listener;
        return this;
    }

    public FlipDialog setConfirmBtn( IOnConfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewschedule_dialog_task);

        setTime=findViewById(R.id.setTaskDialog_setTimeNeeded_button);
        back=findViewById(R.id.setTaskDialog_back_button);
        container=findViewById(R.id.item_taskSet_container_layout);
        nameContainer=findViewById(R.id.setTaskDialog_setTaskInfo_container);
        timeContainer=findViewById(R.id.setTaskDialog_setTime_constraintLayout);
        cancelBtn=findViewById(R.id.setTaskDialog_cancel_button);
        confirmBtn=findViewById(R.id.setTaskDialog_confirm_button);
        startHour=findViewById(R.id.setTaskDialog_startHour_editText);
        startMinute=findViewById(R.id.setTaskDialog_startMinute_editText);
        endHour=findViewById(R.id.setTaskDialog_endHour_editText);
        endMinute=findViewById(R.id.setTaskDialog_endMinute_editText);
        taskName=findViewById(R.id.setTaskDialog_name_MaterialEditText);
        taskDeco=findViewById(R.id.setTaskDialog_decoration_editText);

        setTime.setOnClickListener(this);
        back.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        List<String> data = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        for (int i = 0; i < 24; i++)
        {
            data.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++)
        {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        startHour.setData(data);
        startMinute.setData(minutes);
        endHour.setData(data);
        endMinute.setData(minutes);



        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.6); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setTaskDialog_setTimeNeeded_button:
                startAnimation();
                startHour.setOnSelectListener(new PickerView.onSelectListener()
                {
                    @Override
                    public void onSelect(String text)
                    {
                        startHourStr=text;
                    }
                });
                startMinute.setOnSelectListener(new PickerView.onSelectListener()
                {
                    @Override
                    public void onSelect(String text)
                    {
                        startMinuteStr=text;
                    }
                });
                endHour.setOnSelectListener(new PickerView.onSelectListener()
                {
                    @Override
                    public void onSelect(String text)
                    {
                        endHourStr=text;
                    }
                });
                endMinute.setOnSelectListener(new PickerView.onSelectListener()
                {
                    @Override
                    public void onSelect(String text)
                    {
                        endMinuteStr=text;
                    }
                });


                break;
            case R.id.setTaskDialog_back_button:
                startAnimation();
                break;
            case R.id.setTaskDialog_cancel_button:
                if (cancelListener != null)
                    cancelListener.OnCancel(this);
                break;
            case R.id.setTaskDialog_confirm_button:
                startTime=new Time();
                if (startHourStr!=null&&startMinuteStr!=null){
                    startTime.setHour(Integer.parseInt(startHourStr));
                    startTime.setMinute(Integer.parseInt(startMinuteStr));
                }

                endTime=new Time();
                if (endHourStr!=null&&endMinuteStr!=null){
                    endTime.setHour(Integer.parseInt(endHourStr));
                    endTime.setMinute(Integer.parseInt(endMinuteStr));
                }
                if (confirmListener != null)
                    confirmListener.OnConfirm(this, taskName.getText().toString(),taskDeco.getText().toString(),startTime,endTime);
                break;
        }
    }

    //监听器接口
    public interface IOnCancelListener {
        void OnCancel(FlipDialog dialog);
    }
    public interface IOnConfirmListener {
        void OnConfirm(FlipDialog dialog, String taskName, String taskDesc, Time startTime, Time endTime );
    }

/*_____________________________________________________________________________________*/

    private void startAnimation() {
        //接口回调传递参数
        centerX = container.getWidth() / 2;
        centerY = container.getHeight() / 2;
        if (openAnimation == null) {
            initOpenAnim();
            initCloseAnim();
        }

        //用作判断当前点击事件发生时动画是否正在执行
        if (openAnimation.hasStarted() && !openAnimation.hasEnded()) {
            return;
        }
        if (closeAnimation.hasStarted() && !closeAnimation.hasEnded()) {
            return;
        }

        //判断动画执行
        if (isOpen) {

            container.startAnimation(openAnimation);

        } else {

            container.startAnimation(closeAnimation);

        }
        isOpen = !isOpen;
    }

    /**
     *注意旋转角度
     */
    private void initOpenAnim() {
        //从0到90度，顺时针旋转视图，此时reverse参数为true，达到90度时动画结束时视图变得不可见，
        openAnimation = new Rotate3dAnimation(0, 90, centerX, centerY, depthZ, true);
        openAnimation.setDuration(duration);
        openAnimation.setFillAfter(true);
        openAnimation.setInterpolator(new AccelerateInterpolator());
        openAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timeContainer.setVisibility(View.GONE);
                nameContainer.setVisibility(View.VISIBLE);
                //从270到360度，顺时针旋转视图，此时reverse参数为false，达到360度动画结束时视图变得可见
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(270, 360, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                container.startAnimation(rotateAnimation);
            }
        });
    }

    private void initCloseAnim() {
        closeAnimation = new Rotate3dAnimation(360, 270, centerX, centerY, depthZ, true);
        closeAnimation.setDuration(duration);
        closeAnimation.setFillAfter(true);
        closeAnimation.setInterpolator(new AccelerateInterpolator());
        closeAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timeContainer.setVisibility(View.VISIBLE);
                nameContainer.setVisibility(View.GONE);
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(90, 0, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                container.startAnimation(rotateAnimation);
            }
        });
    }

}

