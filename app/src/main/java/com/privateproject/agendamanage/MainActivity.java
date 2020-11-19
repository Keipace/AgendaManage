package com.privateproject.agendamanage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.privateproject.agendamanage.activity.DayTimeSelectActivity;
import com.privateproject.agendamanage.activity.WeekTimeEditChoiseActivity;
import com.privateproject.agendamanage.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.adapter.WeekTimeAdapter;
import com.privateproject.agendamanage.databinding.ActivityMainBinding;
import com.privateproject.agendamanage.fragment.FragmentTest;
import com.privateproject.agendamanage.fragment.GoalListFragment;
import com.privateproject.agendamanage.fragment.WeekTimeFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding pageXml;
    private GoalListFragment goalListFragment;
    private FragmentTest fragmentTest;
    private WeekTimeFragment weekTimeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 初始化页面
        super.onCreate(savedInstanceState);
        pageXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(pageXml.getRoot());
        setListener();
        pageXml.mianActivityQingdanBtn.transitionToEnd();
        selectTab(0);//默认选中第一个Tab，即清单
    }

    private void setListener() {
        OnClick onClick = new OnClick();
        pageXml.mianActivityQingdanBtn.setOnClickListener(onClick);
        pageXml.mianActivityRichengBtn.setOnClickListener(onClick);
        pageXml.mianActivityTongjiBtn.setOnClickListener(onClick);
        pageXml.mianActivityMeBtn.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
                    selectTab(2);
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
                if (weekTimeFragment == null) { //第一次显示第二个选项卡
                    weekTimeFragment = new WeekTimeFragment();
                    transaction.add(R.id.mianActivity_container_contrainlayout, weekTimeFragment).commitAllowingStateLoss();
                } else { //返回第二个选项卡
                    if(DayTimeSelectRecycleAdapter.isChange == true){
                        DayTimeSelectRecycleAdapter.isChange = false;
                    }
                    weekTimeFragment.refresh();
                    transaction.show(weekTimeFragment).commitAllowingStateLoss();
                }
                break;
            case 2: //显示第二个选项卡
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
        if (weekTimeFragment != null) {
            transaction.hide(weekTimeFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==WeekTimeAdapter.REQUESTCODE_WEEKTIMEEDIT && resultCode==WeekTimeEditChoiseActivity.RESULTCODE_WEEKTIMEEDIT) {
            weekTimeFragment.refresh();
        }
    }
}