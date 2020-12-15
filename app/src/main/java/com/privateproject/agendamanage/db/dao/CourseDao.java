package com.privateproject.agendamanage.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.privateproject.agendamanage.db.bean.Course;
import java.sql.SQLException;
import java.util.List;

public class CourseDao {
    // 使用ormlite框架的dao类来对数据库进行操作。第一个泛型代表要操作的表对应的实体类，第二个泛型代表实体类id字段的类型
    private Dao<Course, Integer> dao;

    // 创建对象时需要传入Activity的对象
    public CourseDao(Context context) {
        try {
            // 使用DatabaseHelper来获取dao对象，避免重复创建多个dao对象而浪费资源
            dao = DatabaseHelper.getDatabaseHelper(context).getDao(Course.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 新添加一个Course
    public void addCourse(Course course) {
        try {
            dao.create(course);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过course对象的id属性值来删除course数据
    public void deleteCourseById(Course course) {
        try {
            dao.delete(course);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 通过id值来删除course数据
    public void deleteCourseById(Integer id) {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据course对象的id值来修改对应行的数据
    public void updateCourse(Course course) {
        try {
            dao.update(course);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库，若记录已经存在则修改，若没有查询到（或null、0或其他default value）时会插入新的数据
    public void updateOrAdd(Course course) {
        try {
            dao.createOrUpdate(course);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据id值来查询数据库中的一条记录，若没有查询到时返回null
    public Course selectById(Integer id) {
        Course course = null;
        try {
            course = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    // 查询数据库中的所有记录，没有查询到时返回null
    public List<Course> selectAll() {
        List<Course> courses = null;
        try {
            courses = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    // 查询数据库中的所有记录，没有查询到时返回null
    public List<Course> selectByPosition(int row, int col) {
        List<Course> result = null;
        QueryBuilder<Course, Integer> queryBuilder = dao.queryBuilder();
        try {
            queryBuilder.where().eq("row", row).
                    and().eq("col", col);
            result = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 查询数据库中的所有记录，没有查询到时返回null
    public List<Course> selectByWeekDay(int col) {
        List<Course> result = null;
        QueryBuilder<Course, Integer> queryBuilder = dao.queryBuilder();
        try {
            queryBuilder.where().eq("col", col);
            result = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
