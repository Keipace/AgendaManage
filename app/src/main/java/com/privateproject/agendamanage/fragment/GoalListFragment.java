package com.privateproject.agendamanage.fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.diegodobelo.expandingview.ExpandingList;
import com.privateproject.agendamanage.MainActivity;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.DayTimeSelectActivity;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.GoalListServer;

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
                DayTimeFragmentDao dao = new DayTimeFragmentDao(getContext());
                List<DayTimeFragment> dayTimeFragmentList = dao.selectAll();
                if (dayTimeFragmentList.size() == 0||dayTimeFragmentList == null){
                    Intent intent = new Intent(getActivity(), DayTimeSelectActivity.class);
                    getActivity().startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(),DayTimeSelectActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

}