package com.privateproject.agendamanage.module_planTarget.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.module_planTarget.adapter.PlanNodeAdapter;
import com.privateproject.agendamanage.db.bean.Target;

public class PlanTargetFragment extends Fragment {
    private PlanNodeAdapter adapter;
    private Target topTarget;
    private Context context;
    private OnFragmentExit onFragmentExit;

    public PlanTargetFragment(Target topTarget, Context context, OnFragmentExit onFragmentExit) {
        this.topTarget = topTarget;
        this.context = context;
        this.onFragmentExit = onFragmentExit;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plantarget_fragment_plan_target, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView path = view.findViewById(R.id.planTargetFragment_path_textView);
        TextView back = view.findViewById(R.id.planTargetFragment_back_textView);
        TextView exit = view.findViewById(R.id.planTargetFragment_exit_textView);
        this.adapter = new PlanNodeAdapter(topTarget, context, path);
        RecyclerView recyclerView = view.findViewById(R.id.planTargetFragment_list_recyclerView);
        recyclerView.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.back();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentExit.exit();
            }
        });
    }

    public static interface OnFragmentExit {
        void exit();
    }
}
