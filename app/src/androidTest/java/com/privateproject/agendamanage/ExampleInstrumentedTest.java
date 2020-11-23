package com.privateproject.agendamanage;

import android.content.Context;
import android.util.Log;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        DayTimeFragmentDao dayTimeFragmentDao = new DayTimeFragmentDao(appContext);
        CourseDao courseDao = new CourseDao(appContext);

        EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer(appContext);
        List<Integer> list = everydayTotalTimeServer.totalTime();
        for (int i = 0; i < list.size() ; i++) {
            Log.d("eeee","星期"+(i+1)+"有"+list.get(i)+"分钟可以支配");
        }

        EverydayTotalTimeServer everydayTotalTimeServer1 = new EverydayTotalTimeServer(appContext);
        everydayTotalTimeServer1.setEmergencyTime(3);
        everydayTotalTimeServer1.setStudyTime(7);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
//            date1 =  format.parse("2020-11-16");
//            date2 = format.parse("2020-11-22");
            date1 =  format.parse("2020-11-16");
            date2 = format.parse("2020-11-29");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<Integer,Integer> map1 = everydayTotalTimeServer1.emergencyTime(date1,date2);


    }
}