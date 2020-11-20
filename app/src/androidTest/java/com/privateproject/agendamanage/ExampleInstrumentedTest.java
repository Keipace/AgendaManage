package com.privateproject.agendamanage;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.db.CourseDao;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.DayTimeFragmentDao;
import com.privateproject.agendamanage.db.TargetDao;
import com.privateproject.agendamanage.server.EverydayTotalTimeServer;

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
        DayTimeFragmentDao dayTimeFragmentDao = new DayTimeFragmentDao(appContext);
        CourseDao courseDao = new CourseDao(appContext);

        EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(appContext,dayTimeFragmentDao,courseDao);
        List<Integer> list = everydayTotalTimeServer.totalTime();
        for (int i = 0; i < list.size() ; i++) {
            System.out.println("星期"+i+"有"+list.get(i)+"分钟可以支配");
        }
    }
}