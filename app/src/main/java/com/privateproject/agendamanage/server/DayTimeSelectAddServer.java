package com.privateproject.agendamanage.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.privateproject.agendamanage.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.databinding.ItemDaytimeAddLayoutBinding;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.utils.ComponentUtil;
import com.privateproject.agendamanage.utils.Time;
import com.privateproject.agendamanage.utils.TimeUtil;
import com.privateproject.agendamanage.utils.ToastUtil;

import java.util.List;

public class DayTimeSelectAddServer {

    public static void TimeSelectAlerDialog(Context context, DayTimeFragmentDao dao, DayTimeSelectRecycleAdapter adapter, int position){
        ItemDaytimeAddLayoutBinding addTimePageXml = ItemDaytimeAddLayoutBinding.inflate(LayoutInflater.from(context));
        // 时间段的选择
        TimeUtil.setTimeStartToEnd(context, addTimePageXml.daytimeAddBegintimeEdit, addTimePageXml.daytimeAddEndtimeEdit, false);
        ComponentUtil.EditTextEnable(false, addTimePageXml.daytimeAddBegintimeEdit);
        ComponentUtil.EditTextEnable(false, addTimePageXml.daytimeAddEndtimeEdit);
        // 显示弹出框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.setTitle("设置时间段")
                .setView(addTimePageXml.getRoot())
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", null)
                .create();
        alertDialog.show();
        // 设置弹出框的确定按钮
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] times = {addTimePageXml.daytimeAddBegintimeEdit.getText().toString(), addTimePageXml.daytimeAddEndtimeEdit.getText().toString()};
                if (times[0].equals("")||times[1].equals("")){
                    ToastUtil.newToast(context,"数据不能为空");
                }else {
                    List<DayTimeFragment> dayTimeFragmentList = dao.selectAll();
                    //判断是否是修改时间段
                    if(position!=-1){
                        dayTimeFragmentList.remove(position);
                    }
                    //判断选择的时间段与已有的时间段是否冲突
                    if (AppearConflict(dayTimeFragmentList,times)){
                        //冲突出现时弹出的提示框
                        ConflictAlerDialog(context);
                        alertDialog.dismiss();
                    }else {
                        //数据改变，刷新页面
                        AddDatas(dayTimeFragmentList,dao,times);
                        adapter.refresh();
                    }
                    alertDialog.dismiss();
                }
            }
        });
    }

    private static void ConflictAlerDialog(Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("记录时间与之前的记录有重叠部分，请重新设置时间段！")
                .setNegativeButton("确定", null);
        builder.create().show();
    }

    private static boolean AppearConflict(List<DayTimeFragment> dayTimeFragmentList, String[] times){
        int temp = dayTimeFragmentList.size();
        if (dayTimeFragmentList == null||dayTimeFragmentList.size() == 0)
            // 没有时间段的时候不可能有冲突
            return false;
        else {
            //如果开始时间和结束时间均大于最晚时间段的最晚时间，则返回false，不发生冲突
            if (Time.parseTime(times[0]).after(Time.parseTime(dayTimeFragmentList.get(temp-1).getEnd()))){
                return false;
            }
            //冲0开始查找第一个大于用户输入的时间段的数据
            for (int i = 0; i<temp; i++){
                //如果用户输入的开始时间和结束时间小于某一时间段的结束时间
                if (Time.parseTime(times[0]).before(Time.parseTime(dayTimeFragmentList.get(i).getEnd()))){
                    if (Time.parseTime(times[1]).before(Time.parseTime(dayTimeFragmentList.get(i).getEnd()))) {
                        //那么它也需要小于这一时间段的开始时间才可以不发生冲突
                        if ((Time.parseTime(times[0]).before(Time.parseTime(dayTimeFragmentList.get(i).getStart())) &&
                                Time.parseTime(times[1]).before(Time.parseTime(dayTimeFragmentList.get(i).getStart())))) {
                            return false;
                        }else {
                            return true;
                        }
                    }else {
                        return true;
                    }
                }
            }
            return true;
        }
    }

    private static void AddDatas(List<DayTimeFragment> dayTimeFragmentList, DayTimeFragmentDao dao, String[] times){
        int temp = dayTimeFragmentList.size();
        // 清空数据库
        dao.clearTable();
        //如果数据库为空，直接添加
        if (dayTimeFragmentList == null || temp==0){
            DayTimeFragment dayTimeFragment = new DayTimeFragment(times[0],times[1]);
            dao.addDayTimeFragment(dayTimeFragment);
        }else {
            //数据库不为空，先把数据按顺序存入到List中
            for (int i = 0; i<temp; i++){
                //如果用户输入的开始时间时间小于数据库中的某一开始时间，则把用户输入的数据插入到该数据项的前面
                if (Time.parseTime(times[0]).before(Time.parseTime(dayTimeFragmentList.get(i).getStart()))) {
                    DayTimeFragment dayTimeFragment = new DayTimeFragment(times[0], times[1]);
                    dayTimeFragmentList.add(i, dayTimeFragment);
                    break;
                }
            }
            //如果List修改后的长度和原本的长度相等，说明用户户输入的时间段排在最后，因此把此数据添加到末尾
            if (dayTimeFragmentList.size() == temp){
                DayTimeFragment dayTimeFragment = new DayTimeFragment(times[0], times[1]);
                dayTimeFragmentList.add(dayTimeFragment);
            }
        }
        //修改数据库
        for (int i = 0; i<dayTimeFragmentList.size(); i++){
            dao.addDayTimeFragment(dayTimeFragmentList.get(i));
        }
    }
}
