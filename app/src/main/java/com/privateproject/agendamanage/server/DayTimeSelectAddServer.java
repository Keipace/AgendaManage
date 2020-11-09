package com.privateproject.agendamanage.server;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.privateproject.agendamanage.adapter.DayTimeSelectRecycleAdapter;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.utils.Time;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DayTimeSelectAddServer {

    private Context context;
    private List<Integer> temp;

    public DayTimeSelectAddServer(Context context){
        this.context = context;
        this.temp = new ArrayList<Integer>();
    }

    //判断是否时间段有冲突
    public boolean AppearConflict(String[] times, List<String> datas){
        this.temp = new ArrayList<Integer>();
        int length = datas.size();
        for (int i=0; i<length; i++ ){
            //第一个大于开始时间的数据的下标保存到列表中
            if (!Time.parseTime(times[0]).after(Time.parseTime(datas.get(i)))){
                temp.add(i);
                if (!Time.parseTime(times[1]).after(Time.parseTime(datas.get(i)))){
                    temp.add(i);
                    //如果结束时间同样小于此数据，跳出循环
                    return false;
                }else {
                    //继续循环知道找到第一个大于结束时间的数据的下标
                    for (int j=i+1; j<length; j++){
                        if (!Time.parseTime(times[1]).after(Time.parseTime(datas.get(j)))){
                            temp.add(j);
                            break;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    //修改时间段列表timelist
    public void updateTimeList(@NotNull String[] times, List<String> datas, DayTimeFragmentDao dao){
        // 开始时间和某一时间重复
        if ((datas.get(temp.get(0))).equals(times[0])) {
            // 结束时间和某一时间重复
            if ((datas.get(temp.get(1))).equals(times[1])) {
                return;
            // 结束时间不和某一时间重复
            } else {
                datas.add(temp.get(0)+1, times[1]);
            }
        // 开始时间不和某一时间重复
        } else {
            // 结束时间和某一时间重复
            if ((datas.get(temp.get(1))).equals(times[1])) {
                datas.add(temp.get(0), times[0]);
                // 结束时间不和某一时间重复
            } else {
                datas.add(temp.get(0), times[0]);
                datas.add(temp.get(0)+1, times[1]);
            }
        }
    }

    public void ConflictAlerDialog(String[] times, List<String> datas, DayTimeFragmentDao dao, DayTimeSelectRecycleAdapter adapter){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("记录时间与之前的记录有重叠部分，保存将覆盖之前记录，是否继续？")
                .setPositiveButton("确定", (dialog, which) -> {
                    //点击确定按钮，修改数据库
                    if (!datas.get(temp.get(0)).equals(times[0])) {
                        temp.set(1, temp.get(1)-1);
                        datas.remove(temp.get(0));
                    }
                    for (int i=temp.get(0)+1; i<temp.get(1); i++){
                        temp.set(1, temp.get(1)-1);
                        datas.remove(temp.get(0)+1);
                    }
                    //向列表中添加数据
                    updateTimeList(times,datas,dao);
                    //刷新页面
                    adapter.refresh();
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }
}
