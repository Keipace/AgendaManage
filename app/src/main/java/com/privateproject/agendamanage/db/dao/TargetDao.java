package com.privateproject.agendamanage.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TargetDao {
    // 使用ormlite框架的dao类来对数据库进行操作。第一个泛型代表要操作的表对应的实体类，第二个泛型代表实体类id字段的类型
    private Dao<Target, Integer> dao;

    // 创建对象时需要传入Activity的对象
    public TargetDao(Context context) {
        try {
            // 使用DatabaseHelper来获取dao对象，避免重复创建多个dao对象而浪费资源
            dao = DatabaseHelper.getDatabaseHelper(context).getDao(Target.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 新添加一个target
    public void addTarget(Target target) {
        try {
            dao.create(target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过target对象的id属性值来删除target数据
    public void deleteTargetById(Target target) {
        try {
            dao.delete(target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过id值来删除target数据
    public void deleteTargetById(Integer id) {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据target对象的id值来修改对应行的数据
    public void updateTarget(Target target) {
        try {
            dao.update(target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库，若记录已经存在则修改，若没有查询到（或null、0或其他default value）时会插入新的数据
    public void updateOrAdd(Target target) {
        try {
            dao.createOrUpdate(target);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库中的一条记录，若没有查询到时返回null
    public Target selectById(Integer id) {
        Target target = null;
        try {
            target = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return target;
    }

    // 查询数据库中的所有记录，没有查询到时返回null
    public List<Target> selectAll() {
        List<Target> targets = null;
        try {
            targets = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return targets;
    }

    // 利用 深度优先遍历 获取叶节点，返回的PlanNode是按照时间顺序排列的
    public List<PlanNode> selectLastPlanNode(Target target, Context context) {
        if (target.getPlanNodes()==null || target.getPlanNodes().size()==0) {
            return null;
        }
        PlanNodeDao planNodeDao = new PlanNodeDao(context);
        List<PlanNode> firstLevel = new ArrayList<>(target.getPlanNodes());
        List<PlanNode> saved = new ArrayList<PlanNode>();
        dfsOfSelectLast(firstLevel, saved, planNodeDao);
        return saved;
    }

    private void dfsOfSelectLast(List<PlanNode> parents, List<PlanNode> saved, PlanNodeDao planNodeDao) {
        parents = PlanNodeDao.sortPlanNodeList(parents);
        for (int i = 0; i < parents.size(); i++) {
            parents.set(i, planNodeDao.initPlanNode(parents.get(i)));
            if (parents.get(i).isHasChildren()) {
                dfsOfSelectLast(parents.get(i).getChildren(), saved, planNodeDao);
            } else {
                saved.add(parents.get(i));
            }
        }
    }

}
