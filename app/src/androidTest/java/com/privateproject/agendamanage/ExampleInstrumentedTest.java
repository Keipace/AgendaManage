package com.privateproject.agendamanage;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.privateproject.agendamanage.db.bean.PlanNode;
import com.privateproject.agendamanage.db.bean.Target;
import com.privateproject.agendamanage.db.dao.CourseDao;
import com.privateproject.agendamanage.db.dao.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.dao.PlanNodeDao;
import com.privateproject.agendamanage.db.dao.TargetDao;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.privateproject.agendamanage", appContext.getPackageName());

        TargetDao targetDao = new TargetDao(appContext);
        Target target = targetDao.selectAll().get(0);
        List<PlanNode> tmp = targetDao.selectLastPlanNode(target, appContext);
        System.out.println(tmp.size());
    }
}