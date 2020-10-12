package com.privateproject.agendamanage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.privateproject.agendamanage.adapter.MainExpandableListViewAdapter;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding pageXml;
    private MainExpandableListViewAdapter adapter;
    private List<Target> targets;
    private List<DayTarget> dayTargets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(pageXml.getRoot());

        try {
            targets = new ArrayList<Target>();
            dayTargets = new ArrayList<DayTarget>();
            /*String name, Long timeNeed, Date timePlanOver, Date timeDeadLine, Date timeRealOver, int importance, Date timePreDo*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            targets.add(new Target("一次性目标", 10L, sdf.parse("2020-10-13"), sdf.parse("2020-10-14"), sdf.parse("2020-10-12"), Target.IMPORTANCE_HIGH, sdf.parse("2020-10-12")));
            /*String name, Date timeFragmentStart, Date timeFragmentEnd, int planCounts, int doneCounts*/
            dayTargets.add(new DayTarget("日常性目标", sdf.parse("2020-10-12"), sdf.parse("2020-10-14"), 10, 5));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter = new MainExpandableListViewAdapter(this, targets, dayTargets);
        pageXml.mainTargetListExpandableListView.setAdapter(adapter);
    }
}