package com.privateproject.agendamanage.module_viewSchedule.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.necer.calendar.BaseCalendar;
import com.necer.calendar.Miui10Calendar;
import com.necer.enumeration.CalendarState;
import com.necer.enumeration.CheckModel;
import com.necer.enumeration.DateChangeBehavior;
import com.necer.listener.OnCalendarChangedListener;
import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.module_viewSchedule.adapter.ViewScheduleAdapter;
import com.privateproject.agendamanage.utils.FlipDialog;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.utils.ToastUtil;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViewScheduleFragment extends Fragment {
    private ViewScheduleAdapter adapter;
    private RecyclerView recyclerView;
    private String dateStr;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewschedule_fragment_schedule,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = sdf.format(date);

        Miui10Calendar miui10Calendar=view.findViewById(R.id.viewScheduleFragment_Calendar);
        ImageButton foldBtn=view.findViewById(R.id.viewScheduleFragment_foldBtn);
        Button jumpBtn=view.findViewById(R.id.jumpBtn);
        Button todayBtn=view.findViewById(R.id.jumpTodayBtn);
        TextView tv_result=view.findViewById(R.id.tv_result);

        adapter=new ViewScheduleAdapter(getActivity(),dateStr);
        recyclerView=view.findViewById(R.id.calendar_recycleView);
        recyclerView.setAdapter(adapter);

        miui10Calendar.setCheckMode(CheckModel.SINGLE_DEFAULT_CHECKED);


        //跳转日期
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            private int Year;
            private int Month;
            private int Day;
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Year = year;
                        Month = month+1;
                        Day = dayOfMonth;
                        miui10Calendar.jumpDate(Year,Month,Day);
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }

        });

        //返回今日
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateStr = sdf.format(date);
                miui10Calendar.jumpDate(dateStr);
            }
        });

        //日历点击日期监听器
        miui10Calendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {

            @Override
            public void onCalendarChange(BaseCalendar baseCalendar, int year, int month, LocalDate localDate, DateChangeBehavior dateChangeBehavior) {

                tv_result.setText(localDate+"");

                if (localDate != null) {
                    dateStr = localDate.toString("yyyy-MM-dd");
                } else {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateStr = sdf.format(date);
                }
                adapter.setDateStr(dateStr);
                adapter.setCourseAndTaskList(adapter.init());
                adapter.notifyDataSetChanged();
            }

        });

        foldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miui10Calendar.getCalendarState() == CalendarState.WEEK) {
                    miui10Calendar.toMonth();
                    foldBtn.setImageResource(R.drawable.down);
                } else {
                    miui10Calendar.toWeek();
                    foldBtn.setImageResource(R.drawable.up);
                }
            }
        });

        ImageView button=view.findViewById(R.id.edit_schedule);
        FlipDialog flipDialog=new FlipDialog(getContext(),R.style.DayTargetDialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipDialog.setCancelBtn(new FlipDialog.IOnCancelListener() {
                    @Override
                    public void OnCancel(FlipDialog dialog) {
                        flipDialog.dismiss();
                    }
                }).setConfirmBtn(new FlipDialog.IOnConfirmListener() {
                    @Override
                    public void OnConfirm(FlipDialog dialog, String taskName, String taskDesc, Time startTime, Time endTime) {
                        if (startTime.before(endTime)){
                            ToastUtil.newToast(getContext(),taskName+taskDesc+startTime.toString()+endTime.toString());
                        }else{
                            ToastUtil.newToast(getContext(),"结束时间不得晚于开始时间");
                        }
                    }
                }).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setDateStr(dateStr);
        adapter.setCourseAndTaskList(adapter.init());
        adapter.notifyDataSetChanged();
    }
}

