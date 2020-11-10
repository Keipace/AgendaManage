package com.privateproject.agendamanage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

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

        selectTab(0);//默认选中第一个Tab，即清单
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mianActivity_qingdan_btn:
                selectTab(0);
                break;
            case R.id.mianActivity_richeng_btn:
                selectTab(1);
                break;
            case R.id.mianActivity_tongji_btn:
                selectTab(2);
                break;
            case R.id.mianActivity_me_btn:
                //我 页面
                break;
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

}