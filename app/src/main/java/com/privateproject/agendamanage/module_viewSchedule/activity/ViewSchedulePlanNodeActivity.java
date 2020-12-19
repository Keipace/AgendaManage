package com.privateproject.agendamanage.module_viewSchedule.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.module_viewSchedule.adapter.ViewSchedulePlanNodeAdapter;

public class ViewSchedulePlanNodeActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewschedule_activity_plannode);

        ViewPager viewPager = findViewById(R.id.viewSchedule_container);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.viewSchedule_tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }

    public static class ScheduleFragment extends Fragment{
        private static final String ARG_SECTION_NUMER = "section_number";
        private RecyclerView recyclerView;
        private ViewSchedulePlanNodeAdapter viewSchedulePlanNodeAdapter;
        public static ScheduleFragment newInstance(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMER,sectionNumber);
            ScheduleFragment fragment = new ScheduleFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView  = inflater.inflate(R.layout.viewschedule_fragment_plannode,container,false);
            return rootView ;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            recyclerView = view.findViewById(R.id.viewSchedule_fragment_planNode_recyclerView);
            viewSchedulePlanNodeAdapter = new ViewSchedulePlanNodeAdapter(getContext(),getArguments().getInt(ARG_SECTION_NUMER));
            recyclerView.setAdapter(viewSchedulePlanNodeAdapter);
        }

        @Override
        public void onResume() {
            super.onResume();
            viewSchedulePlanNodeAdapter.refreshPlanNodeAdapter();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        SectionsPagerAdapter(FragmentManager fm){super(fm);}
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return ScheduleFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}