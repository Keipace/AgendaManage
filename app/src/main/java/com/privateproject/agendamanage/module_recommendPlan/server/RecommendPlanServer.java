package com.privateproject.agendamanage.module_recommendPlan.server;

import android.content.Context;

import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.module_weekTime.server.EverydayTotalTimeServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPlanServer {
    public static final String KEY_ISALLPASS = "isAllPass";
    public static final String KEY_UNPASSLIST = "unpassList";
    public static final String KEY_PLANNODE = "planNode";
    public static final String KEY_ISPASS = "isPass";

    private Context context;
    private Target target;
    private TargetDao targetDao;
    private List<PlanNode> lastPlanNodeList;
    private EverydayTotalTimeServer everydayTotalTimeServer;

    public RecommendPlanServer(Context context,Target target){
        this.target = target;
        this.context = context;
        this.targetDao = new TargetDao(context);
        //拿到目标target的所有最后节点
        this.lastPlanNodeList = this.targetDao.selectLastPlanNode(target, context);
        this.everydayTotalTimeServer = new EverydayTotalTimeServer(context);
    }

    /*
    * 测试叶节点是否合理
    * 返回Map集合，Map中存储的元素有：
    *   1. key=KEY_ISALLPASS，value表示是否通过测试
    *   2. key=KEY_UNPASSLIST， value为List，List元素为Map,该Map中包括两个key-value
    *       key=KEY_PLANNODE， value为一个叶节点
    *       key=KEY_ISPASS， value表示当前叶节点是否通过*/
    public Map planNodeErrorType(){
        //初始化返回的map集合
        Map<String, Object> result = new HashMap<String, Object>();
        //所有叶节点是否通过标识符
        boolean isAllPass = true;
        //初始化map的value值
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //依次遍历叶子节点，判断此节点是否通过
        boolean ischange = true;
        for (int i = 0; i < this.lastPlanNodeList.size(); i++) {
            Map<String, Object> mapOfCurrent = new HashMap<String, Object>();
            if (passTime(this.lastPlanNodeList.get(i))) {
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
            mapOfCurrent.put(KEY_PLANNODE, this.lastPlanNodeList.get(i));
            list.add(mapOfCurrent);
        }
        //向最外层map中填入数据
        result.put(KEY_ISALLPASS, isAllPass);
        result.put(KEY_UNPASSLIST, list);
        return result;
    }

    private boolean passTime(PlanNode planNode) {
        List<Integer> surplusTimeList = everydayTotalTimeServer.surplusTime(planNode.getStartTime(), planNode.getEndTime());
        //此时间段的剩余总时间
        int allSurplusTime = 0;
        for (int j = 0; j < surplusTimeList.size(); j++) {
            allSurplusTime = allSurplusTime + surplusTimeList.get(j);
        }
        if (allSurplusTime <= planNode.getTimeNeeded())
            return false;
        else return true;
    }
}
