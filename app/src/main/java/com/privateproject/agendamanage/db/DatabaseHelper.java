package com.privateproject.agendamanage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.privateproject.agendamanage.bean.Course;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.DayTimeFragment;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.bean.Task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // 标明数据库的名字
    private static final String DATABASE_NAME = "agenda_manage";
    // 使用一个Map集合来存储已经创建的Dao类对象，避免重复Dao类对象
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    // 使该类为一个单例类，使用 getDatabaseHelper(context) 方法来获取该类的对象
    private static DatabaseHelper databaseHelper = null;
    private DatabaseHelper(Context context) {
        // 第4个参数表明当前数据库的版本，如果修改数据库版本后，与手机上当前数据库版本不同的话会自动调用onUpgrade方法
        super(context, DATABASE_NAME, null, 1);
    }
    public static DatabaseHelper getDatabaseHelper(Context context) {
        context = context.getApplicationContext();
        if(databaseHelper == null) {
            synchronized (DatabaseHelper.class) {
                if (databaseHelper == null)
                    databaseHelper = new DatabaseHelper(context);
            }
        }
        return databaseHelper;
    }

    // 传入要获取的Dao类的类型，返回相应的Dao对象（第一次使用时创建，之后会直接从daos的Map集合中获取）
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if(daos.containsKey(className)) {
            dao =daos.get(className);
        }
        if(dao == null) {
            dao = super.getDao(clazz);
            daos.put(className,dao);
        }
        return dao;
    }

    // 创建数据库的时候调用的方法
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DayTarget.class);
            TableUtils.createTable(connectionSource, Target.class);
            TableUtils.createTable(connectionSource, DayTimeFragment.class);
            TableUtils.createTable(connectionSource, Task.class);
            TableUtils.createTable(connectionSource, Course.class);

//            Dao<DayTimeFragment, Integer> dayTimeFragmentIntegerDao = getDao(DayTimeFragment.class);
//            dayTimeFragmentIntegerDao.create(new DayTimeFragment(0, "08:00", "10:00"));
//            dayTimeFragmentIntegerDao.create(new DayTimeFragment(1, "11:00", "12:00"));
//            dayTimeFragmentIntegerDao.create(new DayTimeFragment(2, "14:00", "16:00"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 数据库版本变化时调用的方法
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //注意：此时删除之前的表再重新创建，之前存储的数据会直接丢失
        try {
            TableUtils.dropTable(connectionSource, Target.class, true);
            TableUtils.dropTable(connectionSource, DayTarget.class, true);
            TableUtils.dropTable(connectionSource, DayTimeFragment.class, true);
            TableUtils.dropTable(connectionSource, Task.class, true);
            TableUtils.dropTable(connectionSource, Course.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 释放资源
    @Override
    public void close() {
        super.close();
        for(String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
