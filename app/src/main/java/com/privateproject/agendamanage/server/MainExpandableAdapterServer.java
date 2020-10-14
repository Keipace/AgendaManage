package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainExpandableAdapterServer {
    private Context context;
    private SimpleDateFormat sdf;
    private List<Target> targets;
    private List<DayTarget> dayTargets;
    private TargetDao targetDao;
    private DayTargetDao dayTargetDao;

    private ExpandableListView expandableListView;

    public MainExpandableAdapterServer(Context context, ExpandableListView expandableListView) {
        this.sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.targetDao = new TargetDao(context);
        this.dayTargetDao = new DayTargetDao(context);
        this.context = context;
        this.targets = targetDao.selectAll();
        this.dayTargets = dayTargetDao.selectAll();
        this.expandableListView = expandableListView;
    }

    static class HeadHolder {
        public ConstraintLayout constraintLayout;
        public TextView groupView;
        public Button addBtn;

        public HeadHolder(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_header_constraintLayout);
            groupView = itemView.findViewById(R.id.itemMain_header_planGroup);
            addBtn = itemView.findViewById(R.id.itemMain_header_add);
        }

    }
    static class ContentHolder {
        public ConstraintLayout constraintLayout;
        public TextView targetName,planOver,importance;
        public Button behindOver;

        public ContentHolder(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_content_constraintLayout);
            targetName = itemView.findViewById(R.id.itemMain_content_targetName);
            planOver = itemView.findViewById(R.id.itemMain_content_planOver);
            importance = itemView.findViewById(R.id.itemMain_content_importance);
            behindOver = itemView.findViewById(R.id.itemMain_content_behindOver);
        }

    }
    static class ContentHolderDayTarget {
        public ConstraintLayout constraintLayout;
        public TextView targetName,timeFragment,remainCounts;
        public Button behindOver;

        public ContentHolderDayTarget(View itemView) {
            constraintLayout = itemView.findViewById(R.id.itemMain_content_dayTarget_constraintLayout);
            targetName = itemView.findViewById(R.id.itemMain_content_dayTarget_targetName);
            timeFragment = itemView.findViewById(R.id.itemMain_content_dayTarget_timeFragment);
            remainCounts = itemView.findViewById(R.id.itemMain_content_dayTarget_remainCounts);
            behindOver = itemView.findViewById(R.id.itemMain_content_dayTarget_behindOver);
        }
    }

    private HeadHolder headHolder = null;
    /* 第一次渲染时首先从xml文件中引用控件并赋值到HeadHolder中，从而使用xml文件来控制每个GroupItem的样式
    * 第一次渲染后将引用后的HeadHolder保存到convertView中，之后直接从convertView中获取*/
    public View getGroupItem(View convertView) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_header, null);
            headHolder = new HeadHolder(convertView);
            convertView.setTag(headHolder);
        } else {
            headHolder= (HeadHolder) convertView.getTag();
        }
        return convertView;
    }

    /* 将数据填充到每个 groupItem 中 */
    public void setGroupItemContent(int groupPosition, String[] groups) {
        headHolder.groupView.setText(groups[groupPosition]);

        headHolder.addBtn.setTag(groupPosition);
        headHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index= (int) v.getTag();
                if(index==0) {
                    targetView();
                } else if (index==1) {
                    daytargetView();
                }
            }
        });

    }

    //弹出target对话框
    private void targetView() {
        // 加载target.xml界面布局文件
        View targrtV = LayoutInflater.from(context).inflate(R.layout.item_main_addtarget,null);
        LinearLayout targetForm = (LinearLayout) targrtV;
        new AlertDialog.Builder(context)
                // 设置对话框的标题
                .setTitle("制定目标")
                // 设置对话框显示的View对象
                .setView(targetForm)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", (dialog, which) -> {
                    try {
                        convertTargetMessage(targetForm);
                        refresh(0);
                    } catch (ParseException e) {
                        Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                })
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", (dialog, which) -> {
                    // 取消登录，不做任何事情
                })
                // 创建并显示对话框
                .create().show();

        EditText planOver= targrtV.findViewById(R.id.target_time_planOver_editText);
        EditText deadLine= targrtV.findViewById(R.id.target_time_deadLine_editText);
        EditText preDo= targrtV.findViewById(R.id.target_time_preDo_editText);
        //分别设置监听器显示出日历
        planOver.setOnTouchListener(getOnTouchListener(planOver));
        deadLine.setOnTouchListener(getOnTouchListener(deadLine));
        preDo.setOnTouchListener(getOnTouchListener(preDo));
    }

    private void convertTargetMessage(LinearLayout targetForm) throws ParseException {
        EditText name = targetForm.findViewById(R.id.target_name_editText);
        EditText decoration = targetForm.findViewById(R.id.target_decoration_editText);
        EditText timeNeed = targetForm.findViewById(R.id.target_time_need_editText);
        EditText timePlanOver = targetForm.findViewById(R.id.target_time_planOver_editText);
        EditText timeDeadLine = targetForm.findViewById(R.id.target_time_deadLine_editText);
        EditText timeProDo = targetForm.findViewById(R.id.target_time_preDo_editText);
        RadioGroup importance = targetForm.findViewById(R.id.target_importance_radioGroup);

        int importan = Target.IMPORTANCE_MIDDLE;
        if (importance.getCheckedRadioButtonId()==R.id.target_high_radioButton)
            importan = Target.IMPORTANCE_HIGH;
        else if(importance.getCheckedRadioButtonId()==R.id.target_low_radioButton)
            importan = Target.IMPORTANCE_LOW;
        /*String name, String decoration, Long timeNeed, Date timePlanOver, Date timeDeadLine, Date timeRealOver, int importance, Date timePreDo*/
        Target target = new Target(name.getText().toString(),
                decoration.getText().toString(),
                Long.parseLong(timeNeed.getText().toString()),
                sdf.parse(timePlanOver.getText().toString()),
                sdf.parse(timeDeadLine.getText().toString()),
                sdf.parse("0000-00-00"),
                importan,
                sdf.parse(timeProDo.getText().toString()));

        targetDao.addTarget(target);
    }

    private View.OnTouchListener getOnTouchListener(EditText editText) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //将选择后的日期显示在文本上
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            editText.setText(year + "-" + (monthOfYear+1 )+ "-" + dayOfMonth);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                    return true;
                }
                return false;
            }
        };
    }

    //弹出daytarget对话框
    private void daytargetView() {
        // 加载target.xml界面布局文件
        View daytargrtV = LayoutInflater.from(context).inflate(R.layout.item_main_adddaytarget, null);
        ConstraintLayout targetForm = (ConstraintLayout) daytargrtV;
        new AlertDialog.Builder(context)
                // 设置对话框的标题
                .setTitle("打卡")
                // 设置对话框显示的View对象
                .setView(targetForm)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", (dialog, which) -> {
                    try {
                        convertDayTargetMessage(daytargrtV);
                        refresh(1);
                    } catch (ParseException e) {
                        Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                })
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", (dialog, which) -> {
                    // 取消，不做任何事情
                })
                // 创建并显示对话框
                .create().show();

        EditText timeFragmentStart = daytargrtV.findViewById(R.id.daytarget_timeFragmentStart_editText);
        EditText timeFragmentEnd = daytargrtV.findViewById(R.id.daytarget_timeFragmentEnd_editText);

        timeFragmentEnd.setOnTouchListener(getOnTimeTouchListener(timeFragmentEnd));
        timeFragmentStart.setOnTouchListener(getOnTimeTouchListener(timeFragmentStart));
    }

    private View.OnTouchListener getOnTimeTouchListener(EditText editText) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //将选择后的日期显示在文本上
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editText.setText(hourOfDay+":"+minute);
                        }
                    }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                    return true;
                }
                return false;
            }
        };
    }

    private void convertDayTargetMessage(View daytargrtV) throws ParseException{

        EditText name = daytargrtV.findViewById(R.id.daytarget_day_name_editText);
        EditText decoration = daytargrtV.findViewById(R.id.daytarget_day_decoration_editText);
        EditText frequency = daytargrtV.findViewById(R.id.daytarget_frequcecy_editText);
        EditText timeFragmentStart = daytargrtV.findViewById(R.id.daytarget_timeFragmentStart_editText);
        EditText timeFragmentEnd = daytargrtV.findViewById(R.id.daytarget_timeFragmentEnd_editText);
        EditText planCounts = daytargrtV.findViewById(R.id.daytarget_planCounts_editText);

        /*String name, String decoration, int frequency, Date timeFragmentStart, Date timeFragmentEnd, int planCounts, int doneCounts*/
        DayTarget dayTarget = new DayTarget(name.getText().toString(),
                decoration.getText().toString(),
                Integer.parseInt(frequency.getText().toString()),
                timeFragmentStart.getText().toString(),
                timeFragmentEnd.getText().toString(),
                Integer.parseInt(planCounts.getText().toString()),
                0);

        dayTargetDao.addDayTarget(dayTarget);

    }

    private ContentHolder contentholder = null;
    public View getTargetChildItem(View convertView) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content_target, null);
            contentholder = new ContentHolder(convertView);
            convertView.setTag(contentholder);
        } else {
            contentholder= (ContentHolder) convertView.getTag();
        }
        return convertView;
    }

    public void setTargetChildItemContent(int childPosition) {
        contentholder.targetName.setText(targets.get(childPosition).getName());
        contentholder.planOver.setText(sdf.format(targets.get(childPosition).getTimePlanOver()));
        contentholder.importance.setText(targets.get(childPosition).getImportance());

        contentholder.behindOver.setTag(targets.get(childPosition).getId());
        contentholder.behindOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer id = (Integer) view.getTag();
                targetDao.deleteTargetById(id);
                refresh(0);
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private ContentHolderDayTarget contentHolderDayTarget = null;
    public View getDayTargetChildItem(View convertView) {
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_content_daytarget, null);
            contentHolderDayTarget = new ContentHolderDayTarget(convertView);
            convertView.setTag(contentHolderDayTarget);
        } else {
            contentHolderDayTarget = (ContentHolderDayTarget)convertView.getTag();
        }
        return convertView;
    }

    public void setDayTargetChildItemContent(int childPosition) {
        contentHolderDayTarget.targetName.setText(dayTargets.get(childPosition).getName());
        String startTime = dayTargets.get(childPosition).getTimeFragmentStart();
        String endTime = dayTargets.get(childPosition).getTimeFragmentEnd();
        contentHolderDayTarget.timeFragment.setText(startTime+"——"+endTime);
        int allCounts = dayTargets.get(childPosition).getPlanCounts();
        int doneCounts = dayTargets.get(childPosition).getDoneCounts();
        contentHolderDayTarget.remainCounts.setText(allCounts-doneCounts+"");
        contentHolderDayTarget.behindOver.setTag(dayTargets.get(childPosition).getId());
        contentHolderDayTarget.behindOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer id = (Integer) view.getTag();
                dayTargetDao.deleteDayTargetById(id);
                refresh(1);
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refresh(int groupPosition) {
        this.targets = targetDao.selectAll();
        this.dayTargets = dayTargetDao.selectAll();
        this.expandableListView.collapseGroup(groupPosition);
        this.expandableListView.expandGroup(groupPosition);
    }

    public int getTargetsSize() {
        return this.targets.size();
    }

    public int getDayTargetsSize() {
        return this.dayTargets.size();
    }

    public Object getTarget(int childPosition) {
        return this.targets.get(childPosition);
    }

    public Object getDayTarget(int childPosition) {
        return this.dayTargets.get(childPosition);
    }

}
