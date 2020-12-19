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

}

class DateFragment {
    public static final int[] MIN = {10, 15, 30, 45, 60};
    private EverydayTotalTimeServer everydayTotalTimeServer;

    private Date startDate,endDate;
    private int dayNum;
    private List<DayNode> dayNodes;
    private List<PlanNode> planNodes;

    public DateFragment(Date startDate, Date endDate, EverydayTotalTimeServer everydayTotalTimeServer) {
        this.everydayTotalTimeServer = everydayTotalTimeServer;
        setStartAndEndDate(startDate, endDate);
        List<Integer> surplusTimes = this.everydayTotalTimeServer.surplusTime(startDate, endDate);
        this.dayNodes = new ArrayList<DayNode>();
        for (int i = 0; i < this.dayNum; i++) {
            this.dayNodes.add(new DayNode(surplusTimes.get(i)));
        }
    }

    public void setStartAndEndDate(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayNum = TimeUtil.subDate(startDate, endDate)+1;
    }

    public void setPlanNodes(List<PlanNode> planNodes) {
        this.planNodes = planNodes;
        if (planNodes==null || planNodes.size()==0) {
            return;
        }
        for (int i = 0; i < planNodes.size(); i++) {
            PlanNode planNode = planNodes.get(i);
            if (planNode.isRecommended()) {
                setPassPlanNode(planNode);
            } else {
                setUnpassPlanNode(planNode);
            }
        }
    }

    private void setPassPlanNode(PlanNode planNode) {
        // 获取从PlanNode的startTime到endTime几天的剩余时间量比例（如果剩余时间量小于0则保存0），并计算总和
        int startIndex = getOffDay(planNode.getStartTime());
        int endIndex = getOffDay(planNode.getEndTime());
        int[] proportion = new int[startIndex-endIndex+1];
        int index = 0,total = 0;
        for (int j = startIndex; j <= endIndex; j++) {
            int tmp = this.dayNodes.get(j).getSurplusTime();
            proportion[index] = tmp<0?0:tmp;
            total += tmp;
            index++;
        }

        // 通过计算来选择该计划的最小时间单位
        int timeNeeded = planNode.getTimeNeeded();
        int tmpOfMin = timeNeeded/proportion.length;
        int indexOfMin = MIN[MIN.length-1];
        for (int j = 0; j < MIN.length; j++) {
            if (MIN[j]>tmpOfMin) {
                indexOfMin = j;
                j = j==0?j:j-1;
                break;
            }
        }

        // 初步将PlanNode的所需时间量分配到startTime和endTime的每一天
        int tmpOfSur = timeNeeded;
        int[] tmpOccupy = new int[proportion.length];
        for (int j = 0; j < tmpOccupy.length; j++) {
            int tmpOfTimeNeeded = timeNeeded*proportion[j]/total;
            if (tmpOfTimeNeeded<MIN[indexOfMin]) {
                tmpOccupy[j] = 0;
            } else {
                tmpOccupy[j] = tmpOfTimeNeeded/MIN[indexOfMin]*MIN[indexOfMin];
            }
            // 如果startTime到endTime的总剩余时间量大于总所需时间量，则初步分配的结果不可能导致某一天分配的时间量超出剩余时间量
            proportion[j] -= tmpOccupy[j];
            tmpOfSur -= tmpOccupy[j];
        }

        indexOfMin -= 1;
        // 调整剩余未分配的时间
        while (tmpOfSur>0 && indexOfMin>=0) {
            total = 0;
            int thisTimeNeeded = tmpOfSur;
            for (int i = 0; i < proportion.length; i++) {
                total += proportion[i];
            }
            for (int j = 0; j < proportion.length; j++) {
                int tmpOfTimeNeeded = thisTimeNeeded*proportion[j]/total;
                if (tmpOfTimeNeeded>=MIN[indexOfMin]) {
                    int reduce = tmpOfTimeNeeded/MIN[indexOfMin]*MIN[indexOfMin];
                    proportion[j] -= reduce;
                    tmpOccupy[j] += reduce;
                    tmpOfSur -= reduce;
                }
            }
            indexOfMin -= 1;
        }
        if (tmpOfSur>=0) {
            for (int i = 0; i < proportion.length; i++) {
                if (proportion[i]>0) {
                    if (tmpOfSur>proportion[i]) {
                        tmpOccupy[i] += proportion[i];
                        tmpOfSur -= proportion[i];
                        proportion[i] = 0;
                    } else {
                        proportion[i] -= tmpOfSur;
                        tmpOccupy[i] += tmpOfSur;
                        tmpOfSur = 0;
                        break;
                    }
                }
            }
        }

        // 将分配后的时间量保存到每天中去
        index = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            this.dayNodes.get(i).add(planNode, tmpOccupy[index]);
            index++;
        }
        this.planNodes.add(planNode);
    }

    public void setUnpassPlanNode(PlanNode planNode) {
        int startIndex = getOffDay(planNode.getStartTime());
        int endIndex = getOffDay(planNode.getEndTime());
        int indexOfMax = 0, timeOfMax = 0, timeNeeded = planNode.getTimeNeeded();
        for (int i = startIndex; i <= endIndex; i++) {
            DayNode dayNode = this.dayNodes.get(i);
            if (timeOfMax<dayNode.getSurplusTime()) {
                indexOfMax = i;
                timeOfMax = dayNode.getSurplusTime();
            }
            if (dayNode.getSurplusTime()>0) {
                timeNeeded -= dayNode.getSurplusTime();
                this.dayNodes.get(i).add(planNode, dayNode.getSurplusTime());
            }
        }
        this.dayNodes.get(indexOfMax).add(planNode, timeNeeded);
    }

    // 获取date距startDate的索引，如startDate为10.1，date为10.2，则返回1
    public int getOffDay(Date date) {
        return TimeUtil.subDate(startDate, date);
    }

    public void adjustToPass() {
        for (int i = 0; i < this.dayNodes.size(); i++) {
            DayNode dayNode = this.dayNodes.get(i);
            if (dayNode.getSurplusTime()<0) {
                PlanNode planNode = findPlanNode(i);
                int startIndex = getOffDay(planNode.getStartTime());
                int endIndex = getOffDay(planNode.getEndTime());
                if (endIndex==this.dayNodes.size()-1) {
                    // 最后一个柱子超额分配了
                }
                if (this.dayNodes.get(endIndex).isContainOthers(planNode)) {
                    // 后面紧挨着有节点（在结束日期的同一天），没有空闲时间
                } else {
                    if (this.dayNodes.get(endIndex+1).getSurplusTime()>0) {
                        if (this.dayNodes.get(endIndex+1).getSurplusTime()>(0-dayNode.getSurplusTime())) {
                            // 结束日期后一天够弥补超额分配的时间量
                            planNode.setRecommended(planNode.getRecommendStartTime(), getDate(endIndex+1));
                        } else {

                        }
                    } else {
                        // 可能结束日期后面那一天已经有计划节点占用完了，也可能那一天本身就没有剩余时间量
                    }
                }
            }
        }
    }

    // 给出需要调整的柱子的索引，返回该柱子上需要调整的PlanNode
    private PlanNode findPlanNode(int index) {
        PlanNode result = null;
        DayNode dayNode = this.dayNodes.get(index);
        List<PlanNode> planNodes = dayNode.getOccupyPlanNodes();
        for (int i = 0; i < planNodes.size(); i++) {
            if (planNodes.get(i).isRecommended()) {
                result = planNodes.get(i);
            }
        }
        return result;
    }

    // 给出索引，获得索引所对应的日期
    private Date getDate(int i) {
        Date result = TimeUtil.getOffDate(this.startDate, i);
        if (result.after(this.endDate))
            throw new RuntimeException("给出的索引越界");
        return result;
    }
}

class DayNode {
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

    public List<PlanNode> getOccupyPlanNodes() {
        return occupyPlanNodes;
    }

    // 是否包含除PlanNode以外的其他PlanNode
    public boolean isContainOthers(PlanNode planNode) {
        if (this.occupyPlanNodes!=null && this.occupyPlanNodes.size()!=0) {
            if (!contains(planNode))
                return true;
            if (this.occupyPlanNodes.size()>=2)
                return true;
        }
        return false;
    }
}