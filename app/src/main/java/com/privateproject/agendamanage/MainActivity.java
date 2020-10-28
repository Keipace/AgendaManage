package com.privateproject.agendamanage;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.privateproject.agendamanage.adapter.MainExpandableListViewAdapter;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityMainBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.fragment.GoalListFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding pageXml;
    private GoalListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 初始化页面
        super.onCreate(savedInstanceState);
        pageXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(pageXml.getRoot());

        fragment = new GoalListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container_layout,fragment).commitAllowingStateLoss();
    }

}