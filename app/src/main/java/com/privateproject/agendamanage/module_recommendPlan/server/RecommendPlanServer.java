package com.privateproject.agendamanage.module_recommendPlan.server;

import android.content.Context;

import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;
import com.privateproject.agendamanage.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPlanServer {
    public static final String KEY_ISALLPASS = "isAllPass";
    public static final String KEY_UNPASSLIST = "unpassList";
    public static final String KEY_PLANNODE = "planNode";
    public static final String KEY_ISPASS = "isPass";

    private Context context;
    private TargetDao targetDao;
    private PlanNodeDao planNodeDao;
    private EverydayTotalTimeServer everydayTotalTimeServer;

    public RecommendPlanServer(Context context){
        this.context = context;
        this.targetDao = new TargetDao(context);
        this.planNodeDao = new PlanNodeDao(context);
        //拿到目标target的所有最后节点
        this.everydayTotalTimeServer = new EverydayTotalTimeServer(context);
    }

    /*
    * 测试叶节点是否合理
    * 返回Map集合，Map中存储的元素有：
    *   1. key=KEY_ISALLPASS，value表示是否通过测试
    *   2. key=KEY_UNPASSLIST， value为List，List元素为Map,该Map中包括两个key-value
    *       key=KEY_PLANNODE， value为一个叶节点
    *       key=KEY_ISPASS， value表示当前叶节点是否通过*/
    public Map<String, Object> planNodeErrorType(Target target){
        List<PlanNode> lastPlanNodeList = this.targetDao.selectLastPlanNode(target, this.context);
        //初始化返回的map集合
        Map<String, Object> result = new HashMap<String, Object>();
        //所有叶节点是否通过标识符
        boolean isAllPass = true;
        //初始化map的value值
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //依次遍历叶子节点，判断此节点是否通过
        boolean ischange = true;
        for (int i = 0; i < lastPlanNodeList.size(); i++) {
            Map<String, Object> mapOfCurrent = new HashMap<String, Object>();
            if (passTime(lastPlanNodeList.get(i))) {
                //通过
                mapOfCurrent.put(KEY_ISPASS, true);
            } else {
                //不通过
                mapOfCurrent.put(KEY_ISPASS, false);
                if (ischange){
                    isAllPass = false;
                    ischange = false;
                }
            }
            //向内层map填充数据
            mapOfCurrent.put(KEY_PLANNODE, lastPlanNodeList.get(i));
            list.add(mapOfCurrent);
        }
        //向最外层map中填入数据
        result.put(KEY_ISALLPASS, isAllPass);
        result.put(KEY_UNPASSLIST, list);
        return result;
    }

    // 判断当前PlanNode规划的时间是否符合剩余时间量
    private boolean passTime(PlanNode planNode) {
        if (getTotalSurplus(planNode.getStartTime(), planNode.getEndTime()) <= planNode.getTimeNeeded())
            return false;
        else
            return true;
    }

    // 计算指定开始日期到指定结束日期的剩余时间总和，返回总的剩余时间量
    private int getTotalSurplus(Date startDate, Date endDate) {
        List<Integer> surplusTimeList = everydayTotalTimeServer.surplusTime(startDate, endDate);
        //此时间段的剩余总时间
        int allSurplusTime = 0;
        for (int j = 0; j < surplusTimeList.size(); j++) {
            allSurplusTime += surplusTimeList.get(j);
        }
        return allSurplusTime;
    }

    // 判断当前target总共所需要的时间量是否符合剩余时间量，如果不符合剩余时间量，则不可能有推荐方案
    public boolean isPracticable(Target target) {
        if (getTotalSurplus(TimeUtil.getOffCurrentDate(1), target.getTimeDeadLine()) >= target.getTimeNeed())
            return true;
        else
            return false;
    }

    // 如果判断存在一种推荐方案，则计算出推算方案并推荐给用户
    public void recommend(Target target) {
        // 1. 后根遍历树：设置每个节点的所需时间量；判断每个节点是否符合剩余时间量的要求
        List<PlanNode> planNodes = new ArrayList<>(target.getPlanNodes());
        postOrderOfRecommend(planNodes);
        // 2. 先根遍历树：调整每个子节点的时间段来符合剩余时间量的要求
        Date start = TimeUtil.getOffCurrentDate(1);
        if (target.getTimeDeadLine().before(start))
            throw new RuntimeException("目标的截止日期不能早于明天的日期");
        preOrderOfRecommend(planNodes, start, target.getTimeDeadLine());
        // 3. 保存树
        saveOfRecommend(planNodes);
    }

    // 设置每个节点的所需时间量；标记每个节点是否符合剩余时间量的要求
    private void postOrderOfRecommend(List<PlanNode> planNodes) {
        planNodes = PlanNodeDao.sortPlanNodeList(planNodes);
        for (int i = 0; i < planNodes.size(); i++) {
            if (planNodes.get(i).isHasChildren()) {
                planNodes.set(i, this.planNodeDao.initPlanNode(planNodes.get(i)));
                postOrderOfRecommend(planNodes.get(i).getChildren());
                // 对非叶节点的操作：根据子节点的所需时间量来计算父节点的所需时间量
                planNodes.set(i, addTimeNeedToParent(planNodes.get(i)));
            }
            // 对所有节点的操作：标记节点是否能通过剩余时间量
            planNodes.set(i, judgePass(planNodes.get(i)));
        }
    }

    // 判断所需时间量是否能通过剩余时间量的要求，并做上标记
    private PlanNode judgePass(PlanNode planNode) {
        int timeNeeded = planNode.getTimeNeeded();
        int totalSurplus = getTotalSurplus(planNode.getStartTime(), planNode.getEndTime());
        if (timeNeeded>totalSurplus) {
            // 如果所需时间量超过剩余时间量，则标记未通过，即需要推荐方案
            planNode.setRecommended(true);
        } else {
            // 如果所需时间量未超过剩余时间量，则标记通过，即不需要推荐方案
            planNode.setRecommended(false);
        }
        return planNode;
    }

    // 通过计算子节点的所需时间量来设定父节点的所需时间量
    private PlanNode addTimeNeedToParent(PlanNode parent) {
        List<PlanNode> children = parent.getChildren();
        int total = 0;
        for (int i = 0; i < children.size(); i++) {
            total += children.get(i).getTimeNeeded();
        }
        parent.setTimeNeeded(total);
        return parent;
    }

    // 通过先根遍历来调整每个节点的时间段
    private void preOrderOfRecommend(List<PlanNode> planNodes, Date startDate, Date endDate) {
        adjustTimeFragment(planNodes, startDate, endDate);
        for (int i = 0; i < planNodes.size(); i++) {
            // 调整孩子节点的时间段
            PlanNode planNode = planNodes.get(i);
            if (planNode.isHasChildren()) {
                preOrderOfRecommend(planNode.getChildren(), planNode.getStartTime(), planNode.getEndTime());
            }
        }
    }

    private void adjustTimeFragment(List<PlanNode> planNodes, Date startDate, Date endDate) {
        /*
        * 首先startDate和endDate不可能是同一个日期：
        *   如果是同一个日期而当前安排计划的所需时间量超出了当天的剩余时间量，则当前方案是不存在可行的推荐方案的
        *   因此与本方法的大前提：当前计划存在可行的调整方案 是相互矛盾的*/
        List<Integer> surplusTimes = this.everydayTotalTimeServer.surplusTime(startDate, endDate);
        List<DayNode> dayNodes = new ArrayList<DayNode>();
        for (int i = 0; i < surplusTimes.size(); i++) {
            dayNodes.add(new DayNode(surplusTimes.get(i)));
        }
        for (int i = 0; i < planNodes.size(); i++) {
            PlanNode planNode = planNodes.get(i);
            int n = TimeUtil.subDate(planNode.getStartTime(), planNode.getEndTime());
            for (int j = 0; j < n; j++) {

            }
            if (planNode.isRecommended()) {

            }
        }
    }

    // 通过先根遍历来保存对树的修改
    private void saveOfRecommend(List<PlanNode> planNodes) {
        for (int i = 0; i < planNodes.size(); i++) {
            this.planNodeDao.updatePlanNode(planNodes.get(i));
            if (planNodes.get(i).isHasChildren()) {
                saveOfRecommend(planNodes.get(i).getChildren());
            }
        }
    }

    private class DayNode {
        public int surplusTime;
        public List<Integer> occupyTimes;
        public List<PlanNode> occupyPlanNodes;

        public DayNode(int surplusTime) {
            this.surplusTime = surplusTime;
            this.occupyPlanNodes = new ArrayList<PlanNode>();
            this.occupyTimes = new ArrayList<Integer>();
        }

        public DayNode(int surplusTime, List<PlanNode> occupyPlanNodes, List<Integer> occupyTimes) {
            this(surplusTime);
            for (int i = 0; i < occupyPlanNodes.size(); i++) {
                add(occupyPlanNodes.get(i), occupyTimes.get(i));
            }
        }

        public DayNode(int surplusTime, PlanNode planNode, Integer occupyTime) {
            this(surplusTime);
            add(planNode, occupyTime);
        }

        public void add(PlanNode planNode, Integer occupyTime) {
            if (!isEnough(occupyTime))
                throw new RuntimeException("剩余时间不够安排");
            if (contains(planNode)) {
                int index = this.occupyPlanNodes.indexOf(planNode);
                this.occupyTimes.set(index, this.occupyTimes.get(index)+occupyTime);
            } else {
                this.occupyTimes.add(occupyTime);
                this.occupyPlanNodes.add(planNode);
            }
            this.surplusTime -= occupyTime;
        }

        public void delete(PlanNode planNode) {
            if (this.occupyPlanNodes.contains(planNode)) {
                int index = this.occupyPlanNodes.indexOf(planNode);
                this.occupyPlanNodes.remove(index);
                this.surplusTime += this.occupyTimes.get(index);
                this.occupyTimes.remove(index);
            } else
                throw new RuntimeException("删除计划失败，这一天还没有安排该计划");
        }

        public boolean contains(PlanNode planNode) {
            return this.occupyPlanNodes.contains(planNode);
        }

        public boolean isEnough(Integer occupyTime) {
            return this.surplusTime-occupyTime<0?false:true;
        }

        public int getSurplusTime() {
            return surplusTime;
        }

        public Integer getOccupyTime(PlanNode planNode) {
            if (contains(planNode))
                return this.occupyTimes.get(this.occupyPlanNodes.indexOf(planNode));
            throw new RuntimeException("获取占用时间失败，这一天还没有安排该计划");
        }
    }

}
