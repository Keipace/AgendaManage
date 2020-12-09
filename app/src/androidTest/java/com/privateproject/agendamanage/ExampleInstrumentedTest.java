package com.privateproject.agendamanage;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.privateproject.agendamanage.activity.TimeLineChartActivity;
import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.PlanNode;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.databinding.ActivityTimeLineChartBinding;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.PlanNodeDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.server.EverydayTotalTimeServer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        Target target = new Target("测试");
        targetDao.addTarget(target);

        PlanNodeDao planNodeDao = new PlanNodeDao(appContext);
        PlanNode planNode = new PlanNode("计划1", "2020-12-4", "2020-12-5", target);
        planNodeDao.addPlanNode(planNode);
        planNodeDao.addChild(planNode, new PlanNode("子计划1", "2020-12-4", "2020-12-4"));
        planNodeDao.updatePlanNode(planNode);

        List<PlanNode> planNodes = planNodeDao.selectAll();
        target = targetDao.selectById(target.getId());
    }
}