package com.privateproject.agendamanage;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.privateproject.agendamanage.bean.DayTarget;
import com.privateproject.agendamanage.bean.Target;
import com.privateproject.agendamanage.db.DayTargetDao;
import com.privateproject.agendamanage.db.TargetDao;

import org.junit.Test;
import org.junit.runner.RunWith;

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

        Target target = new Target("test");
        TargetDao targetDao = new TargetDao(appContext);
        targetDao.addTarget(target);
        Target target1 = targetDao.selectById(target.getId());
        System.out.println(target1);

        DayTarget dayTarget = new DayTarget("test");
        DayTargetDao dayTargetDao = new DayTargetDao(appContext);
        dayTargetDao.addDayTarget(dayTarget);
        DayTarget dayTarget1 = dayTargetDao.selectById(dayTarget.getId());
        System.out.println(dayTarget1);
    }
}