package com.privateproject.agendamanage.module_sourceList.fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.diegodobelo.expandingview.ExpandingList;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.module_weekTime.activity.TimeLineChartActivity;
import com.privateproject.agendamanage.module_weekTime.activity.WeekTimeActivity;
import com.privateproject.agendamanage.utils.CenterDialog;
import com.privateproject.agendamanage.databinding.WeektimeFragmentGoalListEmergencyBinding;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;
import com.privateproject.agendamanage.module_sourceList.server.GoalListServer;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

public class GoalListFragment extends Fragment {
    private ExpandingList expandingList;
    private GoalListServer listServer;
    private GoalListServer.OnItemClick onItemClick;

    public GoalListFragment(GoalListServer.OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sourcelist_fragment_goal_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 加载expandingList
        expandingList=view.findViewById(R.id.fragmentGoalList_listContainer_expandingList);
        //创建两个item，分别显示目标区和打卡区
        listServer=new GoalListServer(getContext(), onItemClick);
        listServer.createTargetItem(expandingList);
        listServer.createDayTargetItem(expandingList);
        // 添加课程表 按钮
        ImageButton setCourseBtn = view.findViewById(R.id.fragmentGoalList_setCourse_btn);
        setCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接跳转到课程表页面
                Intent intent = new Intent(getContext(), WeekTimeActivity.class);
                startActivity(intent);
            }
        });
        //查看折线图按钮
        ImageButton checkTimeLineChartBtn = view.findViewById(R.id.fragmentGoalList_timelinechart_btn);
        checkTimeLineChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接跳转到折线图页面
                Intent intent = new Intent(getContext(), TimeLineChartActivity.class);
                startActivity(intent);
            }
        });
        // 设置应急时间量 按钮的监听器
        ImageButton emergencyButton = view.findViewById(R.id.fragmentGoalList_emergencySelect_btn);
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeektimeFragmentGoalListEmergencyBinding dialogBinding = WeektimeFragmentGoalListEmergencyBinding.inflate(getLayoutInflater());
                StringUtils.setMoveSPaceListener(dialogBinding.emergencyStudyTimeEt);
                StringUtils.setMoveSPaceListener(dialogBinding.emergencyEmergencyEt);
                ComponentUtil.EditTextEnable(false, dialogBinding.emergencyStartDateEt);
                TimeUtil.setOnDateTouchListener(getContext(), dialogBinding.emergencyStartDateEt, true);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(EverydayTotalTimeServer.FILENAME_PREFERENCE, Context.MODE_PRIVATE);
                int setStudyTime = sharedPreferences.getInt(EverydayTotalTimeServer.KEY_STUDY_TIME, -1);
                int setEmergencyTime = sharedPreferences.getInt(EverydayTotalTimeServer.KEY_EMERGENCY_TIME, -1);
                String setEmergencyStart = sharedPreferences.getString(EverydayTotalTimeServer.KEY_EMERGENCY_START, null);
                if (setStudyTime!=-1 && setEmergencyTime!=-1) {
                    dialogBinding.emergencyStudyTimeEt.setText(setStudyTime/60+"");
                    dialogBinding.emergencyEmergencyEt.setText(setEmergencyTime/60+"");
                }
                if (setEmergencyStart!=null) {
                    dialogBinding.emergencyStartDateEt.setText(setEmergencyStart);
                }

                CenterDialog builder = new CenterDialog(getContext(),R.style.BottomDialog,"Bottom");
                builder.setTitle("应急时间量").setView(dialogBinding.getRoot())
                        .setCancelBtn("x", new CenterDialog.IOnCancelListener() {
                            @Override
                            public void OnCancel(CenterDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setConfirmBtn("√", new CenterDialog.IOnConfirmListener() {
                            @Override
                            public void OnConfirm(CenterDialog dialog) {
                                ComponentUtil.requestFocus(dialogBinding.getRoot());
                                String studyStr = dialogBinding.emergencyStudyTimeEt.getText().toString();
                                String emergencyStr = dialogBinding.emergencyEmergencyEt.getText().toString();
                                String emergencyStartStr = dialogBinding.emergencyStartDateEt.getText().toString();
                                if(studyStr.equals("") || emergencyStr.equals("") || emergencyStartStr.equals("")){
                                    ToastUtil.newToast(getContext(),"请您填完整信息哦！！！");
                                }else {
                                    EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(getContext());
                                    int studyInt = Integer.parseInt(studyStr);
                                    int emergencyInt = Integer.parseInt(emergencyStr);
                                    everydayTotalTimeServer.setEmergency(studyInt*60, emergencyInt*60);
                                    everydayTotalTimeServer.setEmergencyStartDate(emergencyStartStr);
                                }
                                builder.dismiss();
                            }
                        }).show();
            }
        });
    }

}