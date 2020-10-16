package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.utils.NumberUtils;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private boolean isAddDialogShow;
    /* 将数据填充到每个 groupItem 中 */
    public void setGroupItemContent(int groupPosition, String[] groups) {
        headHolder.groupView.setText(groups[groupPosition]);

        headHolder.addBtn.setTag(groupPosition);
        headHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAddDialogShow) {
                    isAddDialogShow = true;
                    int index= (int) v.getTag();
                    if(index==0) {
                        targetView();
                    } else if (index==1) {
                        daytargetView();
                    }
                }
            }
        });

    }

    private ItemMainAddtargetBinding targetPage;
    //弹出target对话框
    private void targetView() {
        // 加载target.xml界面布局文件
        targetPage = ItemMainAddtargetBinding.inflate(LayoutInflater.from(context));
        ConstraintLayout targetForm = targetPage.targetParentConstraintLayout;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                // 设置对话框的标题
                .setTitle("制定目标")
                // 设置对话框显示的View对象
                .setView(targetForm)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", null)
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", (dialog, which) -> {
                    // 取消登录，不做任何事情
                    isAddDialogShow = false;
                })
                // 创建并显示对话框
                .create();
        alertDialog.show();
        Button tempBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempBtn.setFocusable(true);
                tempBtn.setFocusableInTouchMode(true);
                tempBtn.requestFocus();
                try {
                    boolean success = convertTargetMessage();
                    if(success) {
                        refresh(0);
                        alertDialog.dismiss();
                    } else {
                        ToastUtil.newToast(context, "请输入完整信息");
                    }
                } catch (ParseException e) {
                    ToastUtil.newToast(context, "添加失败");
                    alertDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isAddDialogShow = false;
            }
        });

        //分别设置监听器显示出日历
        targetPage.targetTimePlanOverEditText.setOnTouchListener(getOnTouchListener(targetPage.targetTimePlanOverEditText, 1));
        targetPage.targetTimeDeadLineEditText.setOnTouchListener(getOnTouchListener(targetPage.targetTimeDeadLineEditText, 2));
        targetPage.targetTimePreDoEditText.setOnTouchListener(getOnTouchListener(targetPage.targetTimePreDoEditText, 3));
        targetPage.targetNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                targetPage.targetNameEditText.setText(StringUtils.moveSpaceString(targetPage.targetNameEditText.getText().toString()));
            }
        });
        targetPage.targetDecorationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    targetPage.targetDecorationEditText.setText(StringUtils.moveSpaceString(targetPage.targetDecorationEditText.getText().toString()));
                }
            }
        });

        targetPage.targetTimeNeedEditText.addTextChangedListener(getTextWatcher(targetPage.targetTimeNeedEditText));
        targetPage.targetTimeNeedEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(targetPage.targetTimeNeedEditText));

    }

    public View.OnFocusChangeListener getNotZeroFocusChangeListener(EditText editText) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(editText.getText().toString().equals("0")) {
                    editText.setText("");
                }
            }
        };
    }

    private TextWatcher getTextWatcher(EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = NumberUtils.moveStartZero(s.toString());
                temp = NumberUtils.onlyOneDecimal(temp);
                if(!temp.equals(s.toString())) {
                    editText.setText(temp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private boolean convertTargetMessage() throws ParseException {
        if(targetPage.targetNameEditText.getText().toString().equals("")
        || targetPage.targetTimeNeedEditText.getText().toString().equals("")
        || targetPage.targetTimePlanOverEditText.getText().toString().equals("")
        || targetPage.targetTimeDeadLineEditText.getText().toString().equals("")
        || targetPage.targetTimePreDoEditText.getText().toString().equals("")) {
            return false;
        }

        int importan = Target.IMPORTANCE_MIDDLE;
        if (targetPage.targetImportanceRadioGroup.getCheckedRadioButtonId()==R.id.target_high_radioButton)
            importan = Target.IMPORTANCE_HIGH;
        else if(targetPage.targetImportanceRadioGroup.getCheckedRadioButtonId()==R.id.target_low_radioButton)
            importan = Target.IMPORTANCE_LOW;

        Target target = new Target(targetPage.targetNameEditText.getText().toString(),
                targetPage.targetDecorationEditText.getText().toString(),
                Double.parseDouble(targetPage.targetTimeNeedEditText.getText().toString()),
                sdf.parse(targetPage.targetTimePlanOverEditText.getText().toString()),
                sdf.parse(targetPage.targetTimeDeadLineEditText.getText().toString()),
                sdf.parse("0000-00-00"),
                importan,
                sdf.parse(targetPage.targetTimePreDoEditText.getText().toString()));

        targetDao.addTarget(target);
        return true;
    }

    /*通过传递的position来判断是哪个日期
    * 1：planOverDate
    * 2：deadLineDate
    * 3：preDoDa*/
    private View.OnTouchListener getOnTouchListener(EditText editText, int position){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //将选择后的日期显示在文本上
                    Calendar current = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            Calendar temp = Calendar.getInstance();
                            temp.set(year, monthOfYear, dayOfMonth);
                            /*temp早于current，返回负数；temp等于current，返回0；temp晚于current，返回正数*/
                            if(temp.compareTo(current)<0) {
                                ToastUtil.newToast(context, "时间不应早于当前时间");
                                return;
                            }
                            String input = year + "-" + (monthOfYear+1 )+ "-" + dayOfMonth;
                            String deadLineDate = targetPage.targetTimeDeadLineEditText.getText().toString();
                            String preDoDate = targetPage.targetTimePreDoEditText.getText().toString();
                            String planOverDate = targetPage.targetTimePlanOverEditText.getText().toString();
                            try {
                                if(position == 1) {
                                    if(!deadLineDate.equals("") && sdf.parse(input).getTime()>sdf.parse(deadLineDate).getTime()) {
                                        ToastUtil.newToast(context, "期待完成时间不应晚于最晚完成时间");
                                        return;
                                    }
                                    if(!preDoDate.equals("") && sdf.parse(input).getTime()<sdf.parse(preDoDate).getTime()) {
                                        ToastUtil.newToast(context, "期待完成时间不应早于预实施时间");
                                        return;
                                    }
                                } else if(position == 2) {
                                    if(!planOverDate.equals("") && sdf.parse(input).getTime()<sdf.parse(planOverDate).getTime()) {
                                        ToastUtil.newToast(context, "最晚完成时间不应早于期待完成时间");
                                        return;
                                    }
                                    if(!preDoDate.equals("") && sdf.parse(input).getTime()<sdf.parse(preDoDate).getTime()) {
                                        ToastUtil.newToast(context, "最晚完成时间不应早于预实施时间");
                                        return;
                                    }
                                } else if(position == 3) {
                                    if(!planOverDate.equals("") && sdf.parse(input).getTime()>sdf.parse(planOverDate).getTime()) {
                                        ToastUtil.newToast(context, "预实施时间不应晚于期待完成时间");
                                        return;
                                    }
                                    if(!deadLineDate.equals("") && sdf.parse(input).getTime()>sdf.parse(deadLineDate).getTime()) {
                                        ToastUtil.newToast(context, "预实施时间不应晚于最晚完成时间");
                                        return;
                                    }
                                }
                            } catch (ParseException e) {
                                ToastUtil.newToast(context, "日期输入错误");
                                return;
                            }
                            editText.setText(input);
                        }
                    }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                    return true;
                }
                return false;
            }
        };
    }

    private ItemMainAdddaytargetBinding dayTargetPage;
    //弹出daytarget对话框
    private void daytargetView() {
        // 加载target.xml界面布局文件
        dayTargetPage = ItemMainAdddaytargetBinding.inflate(LayoutInflater.from(context));
        ConstraintLayout targetForm = dayTargetPage.daytargetParentConstraintLayout;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                // 设置对话框的标题
                .setTitle("打卡")
                // 设置对话框显示的View对象
                .setView(targetForm)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", null)
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", (dialog, which) -> {
                })
                // 创建并显示对话框
                .create();
        alertDialog.show();
        Button tempBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempBtn.setFocusableInTouchMode(true);
                tempBtn.setFocusable(true);
                tempBtn.requestFocus();
                try {
                    boolean success = convertDayTargetMessage();
                    if(success) {
                        refresh(1);
                        alertDialog.dismiss();
                        isAddDialogShow = false;
                    } else {
                        ToastUtil.newToast(context, "请输入完成信息");
                    }
                } catch (ParseException e) {
                    ToastUtil.newToast(context, "添加失败");
                    alertDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isAddDialogShow = false;
            }
        });

        dayTargetPage.daytargetDayDecorationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    dayTargetPage.daytargetDayDecorationEditText.setText(StringUtils.moveSpaceString(dayTargetPage.daytargetDayDecorationEditText.getText().toString()));
                }
            }
        });
        dayTargetPage.daytargetPlanCountsEditText.addTextChangedListener(getTextWatcher(dayTargetPage.daytargetPlanCountsEditText));
        dayTargetPage.daytargetFrequcecyEditText.addTextChangedListener(getTextWatcher(dayTargetPage.daytargetFrequcecyEditText));
        EditText tempNameText = dayTargetPage.daytargetDayNameEditText;
        tempNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tempNameText.setText(StringUtils.moveSpaceString(tempNameText.getText().toString()));
            }
        });
        dayTargetPage.daytargetTimeFragmentStartEditText.setOnTouchListener(getOnTimeTouchListener(dayTargetPage.daytargetTimeFragmentStartEditText, true));
        dayTargetPage.daytargetTimeFragmentEndEditText.setOnTouchListener(getOnTimeTouchListener(dayTargetPage.daytargetTimeFragmentEndEditText, false));

        dayTargetPage.daytargetFrequcecyEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(dayTargetPage.daytargetFrequcecyEditText));
        dayTargetPage.daytargetPlanCountsEditText.setOnFocusChangeListener(getNotZeroFocusChangeListener(dayTargetPage.daytargetPlanCountsEditText));
    }

    private View.OnTouchListener getOnTimeTouchListener(EditText editText, boolean isStart) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //将选择后的日期显示在文本上
                    Calendar calendar = Calendar.getInstance();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            if(isStart) {
                                if (!dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().equals("")) {
                                    String[] temp = dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().split(":");
                                    int endHour = Integer.parseInt(temp[0]);
                                    int endMinute = Integer.parseInt(temp[1]);
                                    if(hourOfDay>endHour || (hourOfDay==endHour&&minute>=endMinute) ) {
                                        ToastUtil.newToast(context, "开始时间必须早于结束时间");
                                        return;
                                    }
                                }
                                editText.setText(formatTimeText(hourOfDay, minute));
                            } else {
                                if(!dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().equals("")) {
                                    String[] temp = dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().split(":");
                                    int startHour = Integer.parseInt(temp[0]);
                                    int startMinute = Integer.parseInt(temp[1]);
                                    if(hourOfDay<startHour || (hourOfDay==startHour&&minute<=startMinute)) {
                                        ToastUtil.newToast(context, "结束时间必须晚于开始时间");
                                        return;
                                    }
                                }
                                editText.setText(formatTimeText(hourOfDay, minute));
                            }

                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                    return true;
                }
                return false;
            }
        };
    }

    public String formatTimeText(int hourOfDay, int minute) {
        String temp = "";
        if(hourOfDay==0) {
            temp += "00";
        } else if(hourOfDay<=9) {
            temp += "0"+hourOfDay;
        } else {
            temp += hourOfDay;
        }
        temp += ":";
        if(minute==0) {
            temp += "00";
        } else if(minute<=9) {
            temp += "0"+minute;
        } else {
            temp += minute;
        }
        return temp;
    }

    private boolean convertDayTargetMessage() throws ParseException{

        if(dayTargetPage.daytargetDayNameEditText.getText().toString().equals("")
        || dayTargetPage.daytargetFrequcecyEditText.getText().toString().equals("")
        || dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString().equals("")
        || dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString().equals("")
        || dayTargetPage.daytargetPlanCountsEditText.getText().toString().equals(""))
            return false;

        DayTarget dayTarget = new DayTarget(dayTargetPage.daytargetDayNameEditText.getText().toString(),
                dayTargetPage.daytargetDayDecorationEditText.getText().toString(),
                Integer.parseInt(dayTargetPage.daytargetFrequcecyEditText.getText().toString()),
                dayTargetPage.daytargetTimeFragmentStartEditText.getText().toString(),
                dayTargetPage.daytargetTimeFragmentEndEditText.getText().toString(),
                Integer.parseInt(dayTargetPage.daytargetPlanCountsEditText.getText().toString()),
                0);

        dayTargetDao.addDayTarget(dayTarget);

        return true;

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
                ToastUtil.newToast(context, "删除成功");
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
                ToastUtil.newToast(context, "删除成功");
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
