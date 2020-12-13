package com.privateproject.agendamanage.module_weekTime.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.module_weekTime.adapter.WeekTimeAdapter;

public class WeekTimeFragment extends Fragment {
    private WeekTimeAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter = new WeekTimeAdapter(getActivity());
        this.adapter.setAdapter(this.adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 设置要显示的页面
        return inflater.inflate(R.layout.weektime_fragment_week_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 设置页面的数据
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.fragmentWeekTime_list_recyclerView);
        recyclerView.setAdapter(this.adapter);

    }

    public void refresh() {
        // 刷新数据
        adapter.initFormData();
        adapter.refreshFormData();
        // 刷新视图
        adapter.notifyDataSetChanged();
    }

}
