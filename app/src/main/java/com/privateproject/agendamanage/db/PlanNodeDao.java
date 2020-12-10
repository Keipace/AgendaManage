package com.privateproject.agendamanage.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.privateproject.agendamanage.bean.PlanNode;
import com.privateproject.agendamanage.utils.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanNodeDao {
    // 使用ormlite框架的dao类来对数据库进行操作。第一个泛型代表要操作的表对应的实体类，第二个泛型代表实体类id字段的类型
    private Dao<PlanNode, Integer> dao;

    // 创建对象时需要传入Activity的对象
    public PlanNodeDao(Context context) {
        try {
            // 使用DatabaseHelper来获取dao对象，避免重复创建多个dao对象而浪费资源
            dao = DatabaseHelper.getDatabaseHelper(context).getDao(PlanNode.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 新添加一个Course
    public void addPlanNode(PlanNode planNode) {
        try {
            dao.create(planNode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过course对象的id属性值来删除course数据
    public void deletePlanNodeById(PlanNode planNode) {
        try {
            dao.delete(planNode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过id值来删除course数据
    public void deletePlanNodeById(Integer id) {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据course对象的id值来修改对应行的数据
    public void updatePlanNode(PlanNode planNode) {
        try {
            dao.update(planNode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库，若记录已经存在则修改，若没有查询到（或null、0或其他default value）时会插入新的数据
    public void updateOrAdd(PlanNode planNode) {
        try {
            dao.createOrUpdate(planNode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库中的一条记录，若没有查询到时返回null
    public PlanNode selectById(Integer id) {
        PlanNode planNode = null;
        try {
            planNode = dao.queryForId(id);
            initPlanNode(planNode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planNode;
    }

    // 查询数据库中的所有记录，没有查询到时返回null
    public List<PlanNode> selectAll() {
        List<PlanNode> planNodes = null;
        try {
            planNodes = dao.queryForAll();
            for (int i = 0; i < planNodes.size(); i++) {
                planNodes.set(i, initPlanNode(planNodes.get(i)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planNodes;
    }

    // 查询planNode的父节点
    public PlanNode selectParent(PlanNode child) {
        PlanNode parent = null;
        try {
            QueryBuilder<PlanNode, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("hasChildren", true)
                                .and().like("childrenIds", "%"+child.getId()+"%");
            parent = queryBuilder.query().get(0);
            initPlanNode(parent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parent;
    }

    public PlanNode initPlanNode(PlanNode planNode) {
        if (planNode.isHasChildren()) {
            Integer[] ids = StringUtils.splitIds(planNode.getChildrenIds());
            if (ids==null)
                return planNode;
            List<PlanNode> planNodes = new ArrayList<PlanNode>();
            for (int i = 0; i < ids.length; i++) {
                planNodes.add(selectById(ids[i]));
            }
            planNode.setChildren(true, planNodes);
        }
        return planNode;
    }

    public PlanNode addChild(PlanNode parent, PlanNode child) {
        if (child!=null) {
            if (!parent.isHasChildren() || parent.getChildren()==null) {
                parent.setChildren(true, new ArrayList<PlanNode>());
            }
            addPlanNode(child);
            List<PlanNode> planNodes = parent.getChildren();
            planNodes.add(child);
            parent.setChildren(true, planNodes);
            updatePlanNode(parent);
        }
        return parent;
    }

}
