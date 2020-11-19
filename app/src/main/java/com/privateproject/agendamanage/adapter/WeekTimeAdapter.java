package com.privateproject.agendamanage.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.aware.DiscoverySession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.WeekTimeEditChoiseActivity;
import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.bean.Task;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.WeekTimeAddServer;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.privateproject.agendamanage.viewHolder.WeekTimeViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

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
    private Object[][] datas;
    private DayTimeFragmentDao dayTimeFragmentDao;
    private CourseDao courseDao;
    private List<Course> coursePositionList;
    private List<Course> courseList;


    public WeekTimeAdapter(Activity context) {
        this.context = context;
        dayTimeFragmentDao = new DayTimeFragmentDao(context);
        courseDao = new CourseDao(context);


        // 查询DayTimeFragment数据库所有数据
        List<DayTimeFragment> dayTimeList = dayTimeFragmentDao.selectAll();
        // 将一天的时间点连接成时间段字符串
        String[] fragment = new String[dayTimeList.size()];
        for (int i = 0; i < dayTimeList.size(); i++) {
            fragment[i] = dayTimeList.get(i).toString();
        }
        // 初始化数据矩阵，并赋予其第一行和第一列的数据
        this.datas = new Object[dayTimeList.size()+1][8];
        if(dayTimeList==null||dayTimeList.size()==0||fragment==null||fragment.length==0){
            this.datas[0] = new Object[]{""};
        }else {
            this.datas[0] = new Object[]{"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            for (int i = 0; i < this.datas.length-1; i++) {
                this.datas[i+1][0] = fragment[i];
            }
        }

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
            int col = position / 8;
            WeekTimeViewHolder.WeekTimeLeftViewHoder leftViewHoder = (WeekTimeViewHolder.WeekTimeLeftViewHoder) holder;
            leftViewHoder.textViewLeft.setText(this.datas[col][0].toString());
        } else {
            //添加相应的Course和Task数据
            courseList = courseDao.selectAll();
            String tmp = showCourseAndTask(position,courseList);
            //显示出来
            WeekTimeViewHolder.WeekTimeCellViewHolder cellViewHolder = (WeekTimeViewHolder.WeekTimeCellViewHolder) holder;
            randomColor(cellViewHolder.btnCell, tmp);
            //此position处对应的button的courseList的长度
            final List<Course>[] coursePositionList = new List[]{courseDao.selectAll(position)};
            //添加监听器
            ((WeekTimeViewHolder.WeekTimeCellViewHolder) holder).btnCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (coursePositionList[0].size() > 0) {
                        Intent intent = new Intent(context,WeekTimeEditChoiseActivity.class);
                        intent.putExtra("posi",position);
                        context.startActivityForResult(intent, REQUESTCODE_WEEKTIMEEDIT);

                    } else {
                        //添加每周事件
                        View view = LayoutInflater.from(context).inflate(R.layout.item_week_time_add_layout, null);
                        EditText courseNameEt = view.findViewById(R.id.item_WeekTimeAdd_courseName_et);
                        EditText addressEt = view.findViewById(R.id.item_WeekTimeAdd_address_et);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("添加每周事件")
                                .setView(view)
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //获得相应的控件
                                        //将新的Course添加至数据库
                                        String courseName = courseNameEt.getText().toString();
                                        String address = addressEt.getText().toString();
                                        WeekTimeAddServer weekTimeAddServer = new WeekTimeAddServer(context);
                                        weekTimeAddServer.addCoures(courseDao, position, courseName, address);
                                        //更新button内容
                                        courseList = courseDao.selectAll();
                                        String finalTmp = showCourseAndTask(position,courseList);
                                        randomColor(cellViewHolder.btnCell, finalTmp);
                                        coursePositionList[0] = courseDao.selectAll(position);
                                    }

                                })
                                .create().show();
                    }
                }

            });

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

    //显示出Button,有字的设置的颜色
    private int colorCount = 0;

    public void randomColor(Button view, String str) {
        String regEX = "[\n, ]+";
        String aa = "";
        String newString = str.replaceAll(regEX, aa);
        if (newString == null || newString.equals("") || newString.equals(" ")) {
            view.setBackgroundColor(Color.rgb(255, 255, 255));
        } else {
            view.setBackgroundColor(COLORSOPPORTED[colorCount % COLORSOPPORTED.length]);
            colorCount++;
        }
        view.setText(str);
    }

    //根据position显示相应位置的内容
    public String showCourseAndTask(int position,List<Course> courseList) {
        //添加Task数据
        int col = position % 8;
        int row = position / 8;
        Object tmpObejct = datas[row][col];
//        List<Task> taskList = (ArrayList<Task>) tmpObejct;
        // 为数据区的视图设置数据
        String tmp = "";
        //添加course数据
        if (courseList != null && courseList.size() != 0) {
            for (int i = 0; i < courseList.size(); i++) {
                if (position == courseList.get(i).getPosition()) {
                    tmp += courseList.get(i).toString();
                }
            }
        }
//        if (taskList != null && taskList.size() != 0) {
//            for (int j = 0; j < taskList.size() - 1; j++) {
//                tmp += taskList.get(j).getName() + ",\n";
//            }
//            tmp += taskList.get(taskList.size() - 1).getName();
//        }
        return tmp;
    }

    public void refresh() {
        // 查询DayTimeFragment数据库所有数据
        List<DayTimeFragment> dayTimeList = dayTimeFragmentDao.selectAll();
        String[] fragment = new String[dayTimeList.size()];
        for (int i = 0; i < dayTimeList.size(); i++) {
            fragment[i] = dayTimeList.get(i).toString();
        }
        // 初始化数据矩阵，并赋予其第一行和第一列的数据
        this.datas = new Object[dayTimeList.size()+1][8];
        if(dayTimeList==null||dayTimeList.size()==0||fragment==null||fragment.length==0){
            this.datas[0] = new Object[]{""};
        }else {
            this.datas[0] = new Object[]{"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            for (int i = 0; i < this.datas.length-1; i++) {
                this.datas[i+1][0] = fragment[i];
            }
        }
        // 查询数据
        courseList = courseDao.selectAll();
    }

}
