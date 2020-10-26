package com.privateproject.agendamanage.server;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.privateproject.agendamanage.activity.DayTargetInfoActivity;
import com.privateproject.agendamanage.activity.TargetInfoActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.utils.NumberUtils;
import com.privateproject.agendamanage.utils.StringUtils;
import com.privateproject.agendamanage.utils.ToastUtil;
import com.privateproject.agendamanage.viewHolder.MainExpandableListViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainExpandableListFillDataServer {
    private Context context;

    public MainExpandableListFillDataServer(Context context) {
        this.context = context;
    }

    // 使用boolean来防止弹出多个添加对话框
    public static boolean isAddDialogShow = false;
    /* 将数据填充到每个 group视图项 中 */
    public void setGroupItemContent(int groupPosition, String[] groups, MainExpandableListViewHolder.HeadHolder headHolder) {
        // 视图中的textView设置
        headHolder.groupView.setText(groups[groupPosition]);
        // 视图中的添加按钮设置
        headHolder.addBtn.setTag(groupPosition); //将groupPosition存储到按钮中
        headHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果已经有对话框显示，则无动作；如果没有则弹窗
                if(!isAddDialogShow) {
                    isAddDialogShow = true;
                    int index= (int) v.getTag(); // 将之前存储的groupPosition取出
                    if(index==0) {
                        // 如果点击目标区的添加按钮，弹出target添加对话框
                        new TargetInputPageServer(context).targetView();
                    } else if (index==1) {
                        // 如果点击打卡区的添加按钮，弹出dayTarget添加对话框
                        new DayTargetInputPageServer(context).daytargetView();
                    }
                }
            }
        });

    }

    /* 将数据填充到目标区的每个 child项 中 */
    public void setTargetChildItemContent(int childPosition, List<Target> targets, MainExpandableListViewHolder.ContentHolder contentholder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 将数据填充到 名称、计划完成时间、重要性 的textView中
        contentholder.targetName.setText(targets.get(childPosition).getName());
//        contentholder.planOver.setText(sdf.format(targets.get(childPosition).getTimePlanOver()));
//        contentholder.importance.setText(targets.get(childPosition).getImportance());

        // “提前结束”按钮，按钮中存储着该项数据的id，按钮点击时删除该项
        contentholder.behindOver.setTag(targets.get(childPosition).getId());
        contentholder.behindOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 将存储的id获取到
                Integer id = (Integer) view.getTag();
                // 数据库中删除该项
                MainExpandableListDBServer dbServer = new MainExpandableListDBServer(context);
                dbServer.deleteTarget(id);
                // 刷新列表
                MainExpandableListDBServer.refresh(0);
                ToastUtil.newToast(context, "删除成功");
            }
        });

        // 点击child的时候跳转到详情页
        contentholder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定从MainActivity跳转到详情页
                Intent intent = new Intent(context, TargetInfoActivity.class);
                // 跳转的时候将该child对应的id值传送过去
                intent.putExtra("id", (int)contentholder.behindOver.getTag());
                context.startActivity(intent);
            }
        });

    }

    /* 将数据填充到打卡区的每个 child项 中 */
    public void setDayTargetChildItemContent(int childPosition, List<DayTarget> dayTargets, MainExpandableListViewHolder.ContentHolderDayTarget contentHolderDayTarget) {
        // 填充每一项的 名称 TextView
        contentHolderDayTarget.targetName.setText(dayTargets.get(childPosition).getName());
        // 获取“开始时间”和“结束时间”，变成“hh:mm——hh:mm”格式，并填充到每一项的 时间 textView中
        String startTime = dayTargets.get(childPosition).getTimeFragmentStart();
        String endTime = dayTargets.get(childPosition).getTimeFragmentEnd();
        contentHolderDayTarget.timeFragment.setText(startTime+"——"+endTime);
        // 获取“计划完成次数”和“已完成次数”，计算出剩余次数并填充到每一项的 剩余次数 textView中
        int allCounts = dayTargets.get(childPosition).getPlanCounts();
        int doneCounts = dayTargets.get(childPosition).getDoneCounts();
        contentHolderDayTarget.remainCounts.setText(allCounts-doneCounts+"");
        // “提前结束”按钮，按钮中存储着该项数据的id，按钮点击时删除该项
        contentHolderDayTarget.behindOver.setTag(dayTargets.get(childPosition).getId());
        contentHolderDayTarget.behindOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 将存储的id获取到
                Integer id = (Integer) view.getTag();
                // 数据库中删除该项
                MainExpandableListDBServer dbServer = new MainExpandableListDBServer(context);
                dbServer.deleteDayTarget(id);
                // 刷新列表
                MainExpandableListDBServer.refresh(1);
                ToastUtil.newToast(context, "删除成功");
            }
        });

        // 点击child的时候跳转到详情页
        contentHolderDayTarget.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 指定从MainActivity跳转到详情页
                Intent intent = new Intent(context, DayTargetInfoActivity.class);
                // 跳转的时候将该child对应的id值传送过去
                intent.putExtra("id", (int)contentHolderDayTarget.behindOver.getTag());
                context.startActivity(intent);
            }
        });
    }

}
