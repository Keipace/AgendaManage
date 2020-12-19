package com.privateproject.agendamanage;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.privateproject.agendamanage.db.bean.DayTimeFragment;
import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.bean.Task;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.db.dao.TargetDao;
import com.privateproject.agendamanage.db.dao.TaskDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws ParseException{
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.privateproject.agendamanage", appContext.getPackageName());

        DayTimeFragmentDao dayTimeFragmentDao = new DayTimeFragmentDao(appContext);
        DayTimeFragment timeFragment = dayTimeFragmentDao.selectAll().get(0);
        TaskDao dao = new TaskDao(appContext);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Task task = new Task("1", "2020-10-1", timeFragment, null);
        Task task1 = new Task("2", "2020-10-2", timeFragment, null);
        Task task2 = new Task("4", "2020-10-4", timeFragment, null);
        Task task3 = new Task("3", "2020-10-3", timeFragment, null);
        dao.addTask(task);
        dao.addTask(task1);
        dao.addTask(task2);
        dao.addTask(task3);
        List<Task> tasks = dao.selectDuringDay(sdf.parse("2020-10-2"), sdf.parse("2020-10-3"));
        System.out.println(tasks.size());
    }
}