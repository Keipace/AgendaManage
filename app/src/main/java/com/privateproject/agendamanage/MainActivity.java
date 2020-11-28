package com.privateproject.agendamanage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.privateproject.agendamanage.databinding.ActivityMainBinding;
import com.privateproject.agendamanage.fragment.FragmentTest;
import com.privateproject.agendamanage.fragment.GoalListFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding pageXml;

    // 第一个页面
    private GoalListFragment goalListFragment;
    // 第二个页面
    private FragmentTest fragmentTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 初始化页面
        super.onCreate(savedInstanceState);
        pageXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(pageXml.getRoot());
        // 设置 tag 切换页面的监听器
        setListener();
        // 默认打开时显示第一个页面
        pageXml.mianActivityQingdanBtn.transitionToEnd();
        selectTab(0);//默认选中第一个Tab，即清单
    }

    private void setListener() {
        // 分别给四个tag（motionLayout）设置监听器
        setBtnOnClickListener(pageXml.mianActivityQingdanBtn);
        setBtnOnClickListener(pageXml.mianActivityMeBtn);
        setBtnOnClickListener(pageXml.mianActivityRichengBtn);
        setBtnOnClickListener(pageXml.mianActivityTongjiBtn);
    }

    private void setBtnOnClickListener(MotionLayout motionLayout) {
        motionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    // 切换时先显示动画，然后切换页面
                    case R.id.mianActivity_qingdan_btn:
                        pageXml.mianActivityQingdanBtn.transitionToEnd();
                        pageXml.mianActivityTongjiBtn.transitionToStart();
                        pageXml.mianActivityRichengBtn.transitionToStart();
                        pageXml.mianActivityMeBtn.transitionToStart();
                        selectTab(0);
                        break;
                    case R.id.mianActivity_richeng_btn:
                        pageXml.mianActivityRichengBtn.transitionToEnd();
                        pageXml.mianActivityTongjiBtn.transitionToStart();
                        pageXml.mianActivityQingdanBtn.transitionToStart();
                        pageXml.mianActivityMeBtn.transitionToStart();
                        selectTab(1);
                        break;
                    case R.id.mianActivity_tongji_btn:
                        pageXml.mianActivityTongjiBtn.transitionToEnd();
                        pageXml.mianActivityQingdanBtn.transitionToStart();
                        pageXml.mianActivityRichengBtn.transitionToStart();
                        pageXml.mianActivityMeBtn.transitionToStart();
                        break;
                    case R.id.mianActivity_me_btn:
                        //我 页面
                        pageXml.mianActivityMeBtn.transitionToEnd();
                        pageXml.mianActivityQingdanBtn.transitionToStart();
                        pageXml.mianActivityRichengBtn.transitionToStart();
                        pageXml.mianActivityTongjiBtn.transitionToStart();
                        break;
                }
            }
        });
    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentTransaction对象
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            case 0: //显示第一个选项卡
                if (goalListFragment == null) { //第一次显示第一个选项卡
                    goalListFragment = new GoalListFragment();
                    transaction.add(R.id.mianActivity_container_contrainlayout, goalListFragment).commitAllowingStateLoss();
                } else { //返回第一个选项卡
                    transaction.show(goalListFragment).commitAllowingStateLoss();
                }
                break;
            case 1: //显示第二个选项卡
                if (fragmentTest == null) { //第一次显示第二个选项卡
                    fragmentTest = new FragmentTest();
                    transaction.add(R.id.mianActivity_container_contrainlayout, fragmentTest).commitAllowingStateLoss();
                } else { //返回第二个选项卡
                    transaction.show(fragmentTest).commitAllowingStateLoss();
                }
                break;
        }
    }

    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (fragmentTest != null) {
            transaction.hide(fragmentTest);
        }
        if (goalListFragment != null) {
            transaction.hide(goalListFragment);
        }
    }

}