package com.privateproject.agendamanage.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.privateproject.agendamanage.db.bean.Task;

import java.sql.SQLException;
import java.util.List;

public class TaskDao {
    // 使用ormlite框架的dao类来对数据库进行操作。第一个泛型代表要操作的表对应的实体类，第二个泛型代表实体类id字段的类型
    private Dao<Task, Integer> dao;

    // 创建对象时需要传入Activity的对象
    public TaskDao(Context context) {
        try {
            // 使用DatabaseHelper来获取dao对象，避免重复创建多个dao对象而浪费资源
            dao = DatabaseHelper.getDatabaseHelper(context).getDao(Task.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 新添加一个task
    public void addTask(Task task) {
        try {
            dao.create(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过task对象的id属性值来删除task数据
    public void deleteTaskById(Task task) {
        try {
            dao.delete(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过id值来删除task数据
    public void deleteTaskById(Integer id) {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据task对象的id值来修改对应行的数据
    public void updateTask(Task task) {
        try {
            dao.update(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库，若记录已经存在则修改，若没有查询到（或null、0或其他default value）时会插入新的数据
    public void updateOrAdd(Task task) {
        try {
            dao.createOrUpdate(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库中的一条记录，若没有查询到时返回null
    public Task selectById(Integer id) {
        Task task = null;
        try {
            task = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    // 查询数据库中的所有记录，没有查询到时返回null
    public List<Task> selectAll() {
        List<Task> tasks = null;
        try {
            tasks = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
