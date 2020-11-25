package com.privateproject.agendamanage.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.activity.DayTimeSelectActivity;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.databinding.ActivityDayTimeSelectBinding;
import com.privateproject.agendamanage.databinding.ItemDaytimeAddLayoutBinding;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.server.DayTimeSelectAddServer;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.privateproject.agendamanage.viewHolder.DayTimeSelectViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DayTimeSelectRecycleAdapter extends RecyclerView.Adapter<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder> {
    private Context context;
    private DayTimeFragmentDao dao;
    private List<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder> items;
    private List<DayTimeFragment> dayTimeFragmentList;
    private ActivityDayTimeSelectBinding binding;
    private List<String> temp;

    public DayTimeSelectRecycleAdapter(Context context,ActivityDayTimeSelectBinding binding) {
        this.context = context;
        this.dao = new DayTimeFragmentDao(context);
        this.dayTimeFragmentList = dao.selectAll();
        this.items = new ArrayList<DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder>();
        this.binding = binding;
        this.temp = new ArrayList<String>();
    }

    @NonNull
    @Override
    public DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载ViewType为TextView的ViewHolder
        return new DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder
                (LayoutInflater.from(context).inflate(R.layout.item_daytime_select_textview, parent, false));
    }

    private boolean isRestart = false;
    @Override
    public void onBindViewHolder(@NonNull DayTimeSelectViewHolder.DayTimeSelectTextViewRecycleViewHolder holder, int position) {
        if (isRestart) {
            this.items.clear();
            this.isRestart = false;
        }
        items.add(position, holder);
        //设置初试时间段
        holder.RecycleTextView.setText(dayTimeFragmentList.get(position).toString());
        //设置文本的点击监听器
        holder.RecycleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳出时间选择器，用户修改时间段
                DayTimeSelectAddServer.TimeSelectAlerDialog(context, dao, DayTimeSelectRecycleAdapter.this, position);
            }
        });
        holder.daytimeSelectContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                temp.clear();
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置不可选，CheckBox可见
                    items.get(i).RecycleTextView.setEnabled(false);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.VISIBLE);
                }
                //显示有关的组件
                isVisible(true);
                return true;
            }
        });
        //复选框监听器
        holder.daytimeSelectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //被选到，把下标放入temp中
                    temp.add(holder.RecycleTextView.getText().toString());
                }else {
                    //取消选择，把存入temp中对应的下标删除
                    temp.remove(holder.RecycleTextView.getText().toString());
                }
            }
        });
        //取消按钮监听器
        binding.daytimeSelectBackBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置可选，CheckBox不可见
                    items.get(i).daytimeSelectCheckBox.setChecked(false);
                    items.get(i).RecycleTextView.setEnabled(true);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.GONE);
                }
                //隐藏有关的组件
                isVisible(false);
                DayTimeSelectRecycleAdapter.this.temp.clear();
            }
        });
        //删除按钮监听器
        binding.daytimeSelectDeleteBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < items.size(); i++) {
                    //为所有TextView设置可选，CheckBox不可见
                    items.get(i).RecycleTextView.setEnabled(true);
                    items.get(i).daytimeSelectCheckBox.setChecked(false);
                    items.get(i).daytimeSelectCheckBox.setVisibility(View.GONE);
                }
                isVisible(false);
                //删除dayTimeFragmentList中对应下标的数据
                for (int i = 0; i<temp.size(); i++){
                    for (int j = 0; j<dayTimeFragmentList.size(); j++){
                        if (dayTimeFragmentList.get(j).toString().equals(temp.get(i))){
                            dayTimeFragmentList.remove(j);
                        }
                    }
                }
                DayTimeSelectRecycleAdapter.this.temp.clear();
                //清空数据库
                dao.clearTable();
                //把数据存入数据库
                for (int i = 0; i<dayTimeFragmentList.size(); i++){
                    dao.addDayTimeFragment(dayTimeFragmentList.get(i));
                }
                //刷新
                refresh();
            }
        });

    }
    @Override
    public int getItemCount() {
        //返回Item的数量
        return dayTimeFragmentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void refresh() {
        isRestart = true;
        this.dayTimeFragmentList = dao.selectAll();
        notifyDataSetChanged();
    }
    public void isVisible(boolean isvisible){
        if (isvisible) {
            binding.daytimeSelectAddBotton.setVisibility(View.GONE);
            binding.daytimeSelectBackBotton.setVisibility(View.VISIBLE);
            binding.daytimeSelectDeleteBotton.setVisibility(View.VISIBLE);
        }else {
            binding.daytimeSelectAddBotton.setVisibility(View.VISIBLE);
            binding.daytimeSelectBackBotton.setVisibility(View.GONE);
            binding.daytimeSelectDeleteBotton.setVisibility(View.GONE);
        }

    }

}