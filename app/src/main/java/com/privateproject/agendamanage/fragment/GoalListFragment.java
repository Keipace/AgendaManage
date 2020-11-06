package com.privateproject.agendamanage.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.adapter.MainExpandableListViewAdapter;

public class GoalListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 设置adapter，加载list数据并显示到list中
        ExpandableListView expandableListView = view.findViewById(R.id.main_targetList_expandableListView);
        MainExpandableListViewAdapter adapter = new MainExpandableListViewAdapter(getContext(), expandableListView);
        expandableListView.setAdapter(adapter);
    }
}