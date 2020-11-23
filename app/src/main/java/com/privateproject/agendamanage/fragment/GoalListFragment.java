package com.privateproject.agendamanage.fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.diegodobelo.expandingview.ExpandingList;
import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.DayTimeSelectActivity;
import com.privateproject.agendamanage.activity.WeekTimeActivity;
import com.privateproject.agendamanage.activity.WeekTimeEditChoiseActivity;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.EverydayTotalTimeServer;
import com.privateproject.agendamanage.server.GoalListServer;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.List;

public class GoalListFragment extends Fragment {
    private ExpandingList expandingList;
    private GoalListServer listServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 加载expandingList
        expandingList=view.findViewById(R.id.fragmentGoalList_listContainer_expandingList);
        //创建两个item
        listServer=new GoalListServer(getContext());
        listServer.createTargetItem(expandingList);
        listServer.createDayTargetItem(expandingList);
        Button selectTimeBotton = view.findViewById(R.id.fragmentGoalList_dayTimeSelect_btn);
        selectTimeBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WeekTimeActivity.class);
                startActivity(intent);
            }
        });

        Button emergencyButton = view.findViewById(R.id.fragmentGoalList_emergencySelect_btn);
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.fragment_goal_list_emergency, null);
                EditText studyTimeEt = view1.findViewById(R.id.emergency_studyTime_et);
                EditText emergencyEt  = view1.findViewById(R.id.emergency_emergency_et);
                String studyStr = studyTimeEt.getText().toString();
                String emergencyStr = emergencyEt.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("设置应急时间量")
                        .setView(view1)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(studyStr==null || "".equals(studyStr)||emergencyStr==null|"".equals(emergencyStr)){
                                    ToastUtil.newToast(getContext(),"请您填完整信息哦！！！");
                                }else {
                                    EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(getContext());
                                    int studyInt = Integer.parseInt(studyTimeEt.getText().toString());
                                    int emergencyInt = Integer.parseInt(emergencyEt.getText().toString());
                                    everydayTotalTimeServer.setStudyTime(studyInt);
                                    everydayTotalTimeServer.setEmergencyTime(emergencyInt);
                                }

                            }

                        })
                        .create().show();
            }
        });
    }

}