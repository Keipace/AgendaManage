package com.privateproject.agendamanage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.WeekTimeEditChoiseActivity;
import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.customDialog.CenterDialog;
import com.privateproject.agendamanage.databinding.ItemWeekTimeAddLayoutBinding;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.viewHolder.WeekTimeViewHolder;

import java.util.ArrayList;
import java.util.List;

public class WeekTimeAdapter extends RecyclerView.Adapter {
    public static final int REQUESTCODE_WEEKTIMEEDIT = 1;

    public static final int HEAD = 0;
    public static final int LEFT = 1;
    public static final int CELL = 2;
    public static final int[] COLORSOPPORTED = new int[]{
            Color.rgb(90, 191, 108),
            Color.rgb(247, 196, 144),
            Color.rgb(99, 192, 189),
            Color.rgb(237, 131, 127),
            Color.rgb(245, 185, 78),
            Color.rgb(202, 148, 131),
            Color.rgb(49, 166, 231),
            Color.rgb(139, 199, 61),
            Color.rgb(135, 166, 198),
            Color.rgb(109, 182, 156),
            Color.rgb(223, 121, 153),
            Color.rgb(214, 168, 88),
    };

    private Activity context;
    // 表格对应的数据
    private Object[][] datas;
    private DayTimeFragmentDao dayTimeFragmentDao;
    private CourseDao courseDao;
    // 所有的课程
    private List<Course> courseList;
    private WeekTimeAdapter adapter;

    public WeekTimeAdapter(Activity context) {
        this.context = context;
        dayTimeFragmentDao = new DayTimeFragmentDao(context);
        courseDao = new CourseDao(context);

        initFormData();
        refreshFormData();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View root;
        if (viewType == HEAD) {
            //第一行的viewHolder
            root = layoutInflater.inflate(R.layout.item_week_time_head, parent, false);
            return new WeekTimeViewHolder.WeekTimeHeadViewHoder(root);
        } else if (viewType == LEFT) {
            //第一列的viewHolder
            root = layoutInflater.inflate(R.layout.item_week_time_left, parent, false);
            return new WeekTimeViewHolder.WeekTimeLeftViewHoder(root);
        } else {
            //数据区的viewHolder
            root = layoutInflater.inflate(R.layout.item_week_time, parent, false);
            return new WeekTimeViewHolder.WeekTimeCellViewHolder(root);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEAD) {
            // 为第一行的视图设置数据
            WeekTimeViewHolder.WeekTimeHeadViewHoder headViewHoder = (WeekTimeViewHolder.WeekTimeHeadViewHoder) holder;
            headViewHoder.textViewHead.setText(this.datas[0][position].toString());
        } else if (getItemViewType(position) == LEFT) {
            // 为第一列的视图设置数据（包括最左上角的）
            int row = position / 8;
            WeekTimeViewHolder.WeekTimeLeftViewHoder leftViewHoder = (WeekTimeViewHolder.WeekTimeLeftViewHoder) holder;
            leftViewHoder.textViewLeft.setText(this.datas[row][0].toString());
        } else {
            int row = position/8;
            int col = position%8;
            //显示课程
            WeekTimeViewHolder.WeekTimeCellViewHolder cellViewHolder = (WeekTimeViewHolder.WeekTimeCellViewHolder) holder;

            //添加监听器
            cellViewHolder.btnCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasCourse(row, col)) {
                        Intent intent = new Intent(context, WeekTimeEditChoiseActivity.class);
                        intent.putExtra("row", row);
                        intent.putExtra("col", col);
                        context.startActivityForResult(intent, REQUESTCODE_WEEKTIMEEDIT);

                    } else {
                        ItemWeekTimeAddLayoutBinding dialogBinding = ItemWeekTimeAddLayoutBinding.inflate(context.getLayoutInflater());
                        //添加每周事件
                        CenterDialog builder = new CenterDialog(context,R.style.DayTargetDialog);
                        builder.setTitle("添加每周事件")
                                .setView(dialogBinding.getRoot())
                                .setCancelBtn("取消", new CenterDialog.IOnCancelListener() {
                                    @Override
                                    public void OnCancel(CenterDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .setConfirmBtn("确定", new CenterDialog.IOnConfirmListener() {
                                    @Override
                                    public void OnConfirm(CenterDialog dialog) {
                                        //将新的Course添加至数据库
                                        String name = dialogBinding.itemWeekTimeAddCourseNameEt.getText().toString();
                                        String address = dialogBinding.itemWeekTimeAddAddressEt.getText().toString();
                                        courseDao.addCourse(new Course(name, address, row, col));
                                        //更新button内容
                                        refreshFormData();
                                        adapter.notifyDataSetChanged();
                                    }
                                }).show();
                    }
                }

            });
            setCourse(cellViewHolder.btnCell, row, col);
            //以为复用会混乱，所以将复用暂停
            holder.setIsRecyclable(false);
        }

    }

    @Override
    public int getItemCount() {
        return datas[0].length * datas.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 8 == 0) {
            return LEFT; //第一列，viewType为1
        }
        if (position / 8 == 0) {
            return HEAD; //第一行，viewType为0
        }
        return CELL; //数据区，viewType为2
    }

    public void initFormData() {
        // 查询DayTimeFragment数据库所有数据
        List<DayTimeFragment> dayTimeList = dayTimeFragmentDao.selectAll();
        this.datas = new Object[dayTimeList.size()+1][8];
        this.datas[0] = new Object[]{"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        if(dayTimeList.size()!=0) {
            // 将一天的时间点连接成时间段字符串
            String[] fragment = new String[dayTimeList.size()];
            for (int i = 0; i < dayTimeList.size(); i++) {
                fragment[i] = dayTimeList.get(i).toString();
            }
            // 初始化数据矩阵，并赋予其第一行和第一列的数据
            for (int i = 0; i < this.datas.length-1; i++) {
                this.datas[i+1][0] = fragment[i];
            }
        }
    }

    public void refreshFormData() {
        courseList = courseDao.selectAll();
        for (int i = 0; i < courseList.size(); i++) {
            Course course = courseList.get(i);
            if (this.datas[course.getRow()][course.getCol()]==null) {
                List<Course> courses = new ArrayList<Course>();
                courses.add(course);
                this.datas[course.getRow()][course.getCol()] = courses;
            } else {
                List<Course> courses = (ArrayList<Course>)this.datas[course.getRow()][course.getCol()];
                boolean isInCourse = false;
                for (int j = 0; j < courses.size(); j++) {
                    if (course.getId() == courses.get(j).getId()){
                        isInCourse = true;
                        break;
                    }
                }
                if (!isInCourse){
                    courses.add(course);
                    this.datas[course.getRow()][course.getCol()] = courses;
                }
            }
        }
    }

    private int count = 0;
    //显示出Button有字的设置的颜色
    private int colorCount = 0;
    private void setCourse(Button button, int row, int col) {
        // 获取对应位置上的数据
        String tmp = "";
        if (this.datas[row][col]==null) {
            // 没有课程时默认显示
            return;
        }
        // 将多个课程拼接成一个字符串
        List<Course> courses = (ArrayList<Course>)this.datas[row][col];
        for (int i = 0; i < courses.size(); i++) {
            tmp += courses.get(i).toString();
        }
        // 根据字符串来设置button的背景颜色
        String newString = tmp.replaceAll("[\n, ]+", "");
        if (newString != null && !newString.equals("")) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);//形状
            gradientDrawable.setCornerRadius(20f);//设置圆角Radius
            gradientDrawable.setColor(COLORSOPPORTED[colorCount%COLORSOPPORTED.length]);//颜色
            button.setBackground(gradientDrawable);//设置为background

            colorCount++;
            // button显示课程的名称
            button.setText(tmp);
            button.setTag(count);
            Log.d("create", count+"");
            count++;
        }

    }

    private boolean hasCourse(int row, int col) {
        if (this.datas[row][col]==null || ((ArrayList<Course>)this.datas[row][col]).size()==0) {
            return false;
        }
        return true;
    }

    public void setAdapter(WeekTimeAdapter adapter) {
        this.adapter = adapter;
    }
}
