package com.privateproject.agendamanage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.privateproject.agendamanage.adapter.MainExpandableListViewAdapter;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityMainBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding pageXml;
    private MainExpandableListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageXml = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(pageXml.getRoot());

        adapter = new MainExpandableListViewAdapter(this, pageXml.mainTargetListExpandableListView);
        pageXml.mainTargetListExpandableListView.setAdapter(adapter);
    }

}