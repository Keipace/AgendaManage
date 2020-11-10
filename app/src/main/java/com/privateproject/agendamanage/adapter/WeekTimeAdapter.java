package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.bean.Task;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.viewHolder.WeekTimeViewHolder;

import java.util.ArrayList;
import java.util.List;

public class WeekTimeAdapter extends RecyclerView.Adapter {
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

    private Context context;
    private Object[][] datas;
    private DayTimeFragmentDao dayTimeFragmentDao;

    public WeekTimeAdapter(Context context) {
        this.context = context;
        dayTimeFragmentDao = new DayTimeFragmentDao(context);
        // 查询数据库所有数据
        List<DayTimeFragment> dayTimeList = dayTimeFragmentDao.selectAll();
        // 将一天的时间点连接成时间段字符串
        String[] fragment = new String[dayTimeList.size()-1];
        for (int i = 0; i < fragment.length; i++) {
            fragment[i] = dayTimeList.get(i).getTimePoint()+"-"+dayTimeList.get(i+1).getTimePoint();
        }
        // 初始化数据矩阵，并赋予其第一行和第一列的数据
        this.datas = new Object[dayTimeList.size()][8];
        this.datas[0] = new Object[]{"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 1; i < this.datas.length; i++) {
            this.datas[i][0] = fragment[i-1];
        }

        // 测试时显示
        Task task = new Task("1", 0, null, null);
        for (int i = 1; i < this.datas.length; i++) {
            for (int j = 1; j < this.datas[0].length; j++) {
                if (Math.random()>0.5) {
                    List<Task> tmp = new ArrayList<Task>();
                    tmp.add(task);
                    this.datas[i][j] = tmp;
                }
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View root;
        if (viewType==HEAD) {
            //第一行的viewHolder
            root = layoutInflater.inflate(R.layout.item_week_time_head, parent, false);
            return new WeekTimeViewHolder.WeekTimeHeadViewHoder(root);
        } else if (viewType==LEFT) {
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
        if (getItemViewType(position)==HEAD) {
            // 为第一行的视图设置数据
            WeekTimeViewHolder.WeekTimeHeadViewHoder headViewHoder = (WeekTimeViewHolder.WeekTimeHeadViewHoder)holder;
            headViewHoder.textViewHead.setText(this.datas[0][position].toString());
        } else if (getItemViewType(position)==LEFT) {
            // 为第一列的视图设置数据（包括最左上角的）
            int col = position/8;
            WeekTimeViewHolder.WeekTimeLeftViewHoder leftViewHoder = (WeekTimeViewHolder.WeekTimeLeftViewHoder)holder;
            leftViewHoder.textViewLeft.setText(this.datas[col][0].toString());
        } else {
            // 为数据区的视图设置数据
            int col = position%8;
            int row = position/8;
            Object tmpObejct = datas[row][col];
            if (tmpObejct!=null) {
                WeekTimeViewHolder.WeekTimeCellViewHolder cellViewHolder = (WeekTimeViewHolder.WeekTimeCellViewHolder)holder;
                List<Task> taskList = (ArrayList<Task>)tmpObejct;
                String tmp = "";
                if (taskList.size()!=0){
                    for (int j = 0; j < taskList.size()-1; j++) {
                        tmp += taskList.get(j).getName()+",\n";
                    }
                    tmp += taskList.get(taskList.size()-1).getName();
                }
                randomColor(cellViewHolder.btnCell,tmp);
            }
        }

    }

    @Override
    public int getItemCount() {
        return datas[0].length*datas.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (position%8==0) {
            return LEFT; //第一列，viewType为1
        }
        if (position/8==0) {
            return HEAD; //第一行，viewType为0
        }
        return CELL; //数据区，viewType为2
    }

    private int colorCount = 0;
    public void randomColor(Button view, String str){
        String regEX="[\n, ]+";
        String aa = "";
        String newString = str.replaceAll(regEX,aa);
        if (newString==null||newString.equals("")||newString.equals(" ")){
            view.setBackgroundColor(Color.rgb(255,255,255));

        }else {
            view.setBackgroundColor(COLORSOPPORTED[colorCount%COLORSOPPORTED.length]);
            colorCount++;
        }
        view.setText(str);
    }
}
