package com.privateproject.agendamanage.server;

import android.content.Context;
import android.widget.ExpandableListView;

import com.privateproject.agendamanage.R;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ItemMainAdddaytargetBinding;
import com.privateproject.agendamanage.databinding.ItemMainAddtargetBinding;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainExpandableListDBServer {
    /*参数相关*/
    private static TargetDao targetDao;
    private static DayTargetDao dayTargetDao;
    private static SimpleDateFormat sdf;
    private static ExpandableListView expandableListView;
    // 数据库查询的数据保存到list中
    private static List<Target> targets;
    private static List<DayTarget> dayTargets;

    public MainExpandableListDBServer(Context context) {
        // 初始化参数
        this.targetDao = new TargetDao(context);
        this.dayTargetDao = new DayTargetDao(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 加载数据
        this.targets = targetDao.selectAll();
        this.dayTargets = dayTargetDao.selectAll();
    }

    public MainExpandableListDBServer(Context context, ExpandableListView expandableListView) {
        this(context);
        this.expandableListView = expandableListView;
    }

    // 获取target表的数据
    public List<Target> getTargets() {
        return this.targets;
    }

    // 获取dayTarget表的数据
    public List<DayTarget> getDayTargets() {
        return this.dayTargets;
    }

    // 获取target的数量
    public int getTargetSize() {
        return this.targets.size();
    }

    // 获取dayTarget的数量
    public int getDayTargetSize() {
        return this.dayTargets.size();
    }

    // 将target添加页面中用户输入的信息存到数据库
    public void addTarget(ItemMainAddtargetBinding targetPage) throws ParseException {
        String decoration = targetPage.targetDecorationEditText.getText().toString();
        if(decoration.equals("")){
            decoration = Target.DEFAULT_DECORATION;
        }
        Target target = new Target(targetPage.targetNameEditText.getText().toString(),
                decoration);
        // 将Target对象插入表中
        targetDao.addTarget(target);
    }

    // 将dayTarget添加页面中用户输入的信息存到数据库
    public void addDayTarget(ItemMainAdddaytargetBinding dayTargetPage) throws ParseException {
        // 将用户输入的信息包装成DayTarget对象
        DayTarget dayTarget = new DayTarget(dayTargetPage.daytargetDayNameEditText.getText().toString(),
                dayTargetPage.daytargetDayDecorationEditText.getText().toString(),
                DayTarget.DEFAULT_FREQUENCY,
                DayTarget.DEFAULT_TIMEFRAGMENTSTART,
                DayTarget.DEFAULT_TIMEFRAGMENTEND,
                DayTarget.DEFAULT_PLANCOUNTS,
                DayTarget.DEFAULT_DONECOUNTS);
        // 将DayTarget对象插入表中
        dayTargetDao.addDayTarget(dayTarget);
    }

    public void deleteTarget(int id) {
        targetDao.deleteTargetById(id);
    }
    public void deleteDayTarget(int id) {
        dayTargetDao.deleteDayTargetById(id);
    }

    public static void refresh(int groupPosition) {
        targets = targetDao.selectAll();
        dayTargets = dayTargetDao.selectAll();
        expandableListView.collapseGroup(groupPosition);
        expandableListView.expandGroup(groupPosition);
    }
}