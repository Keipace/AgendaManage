package com.privateproject.agendamanage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.privateproject.agendamanage.databinding.ActivityMainBinding;
import com.privateproject.agendamanage.fragment.FragmentTest;
import com.privateproject.agendamanage.fragment.GoalListFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding pageXml;
    private GoalListFragment goalListFragment;
    private FragmentTest fragmentTest;

    private Button qingdanButton;
    private Button richengButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 初始化页面
        super.onCreate(savedInstanceState);
        pageXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(pageXml.getRoot());


        selectTab(0);//默认选中第一个Tab
        this.goalListFragment = new GoalListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mianActivity_container_contrainlayout, this.goalListFragment, "goalList").commitAllowingStateLoss();
    }



    public void onClick(View v) {
        //先将四个ImageButton置为灰色
//        resetImgs();
        switch (v.getId()) {
            case R.id.mianActivity_richeng_btn:
                selectTab(0);//当点击的是微信的Tab就选中微信的Tab
                break;
            case R.id.mianActivity_qingdan_btn:
                selectTab(1);
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
//                qingdanButton.setText("beidianl");
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (fragmentTest == null) {
                    fragmentTest = new FragmentTest();
                    transaction.add(R.id.mianActivity_container_contrainlayout, fragmentTest);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(fragmentTest);
                }
                break;
            case 1:
//                richengButton.setText("wobeidianl");
                if (goalListFragment == null) {
                    goalListFragment = new GoalListFragment();
                    transaction.add(R.id.mianActivity_container_contrainlayout, goalListFragment);
                } else {
                    transaction.show(goalListFragment);
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