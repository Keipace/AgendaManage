package com.privateproject.agendamanage.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.privateproject.agendamanage.db.bean.DayTarget;

import java.sql.SQLException;
import java.util.List;

public class DayTargetDao {
    // 使用ormlite框架的dao类来对数据库进行操作。第一个泛型代表要操作的表对应的实体类，第二个泛型代表实体类id字段的类型
    private Dao<DayTarget, Integer> dao;

    // 创建对象时需要传入Activity的对象
    public DayTargetDao(Context context) {
        try {
            // 使用DatabaseHelper来获取dao对象，避免重复创建多个dao对象而浪费资源
            dao = DatabaseHelper.getDatabaseHelper(context).getDao(DayTarget.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 新添加一个dayTarget
    public void addDayTarget(DayTarget dayTarget) {
        try {
            dao.create(dayTarget);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过dayTarget对象的id属性值来删除dayTarget数据
    public void deleteDayTargetById(DayTarget dayTarget) {
        try {
            dao.delete(dayTarget);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过id值来删除datTarget数据
    public void deleteDayTargetById(Integer id) {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据dayTarget对象的id值来修改对应行的数据
    public void updateDayTarget(DayTarget dayTarget) {
        try {
            dao.update(dayTarget);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库，若记录已经存在则修改，若没有查询到（或null、0或其他default value）时会插入新的数据
    public void updateOrAdd(DayTarget dayTarget) {
        try {
            dao.createOrUpdate(dayTarget);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库中的一条记录，若没有查询到时返回null
    public DayTarget selectById(Integer id) {
        DayTarget dayTarget = null;
        try {
            dayTarget = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dayTarget;
    }

    // 查询数据库中的所有记录，没有查询到时返回null
    public List<DayTarget> selectAll() {
        List<DayTarget> dayTargets = null;
        try {
            dayTargets = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dayTargets;
    }
}
