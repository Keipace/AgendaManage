package com.privateproject.agendamanage;

import android.content.Context;

import com.privateproject.agendamanage.server.EverydayTotalTimeServer;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.privateproject.agendamanage.server.EverydayTotalTimeServer.dayForWeek;
import static com.privateproject.agendamanage.server.EverydayTotalTimeServer.differentDays;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void tryDifferent(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 =  format.parse("2020-12-15");
            Date date2 = format.parse("2020-12-18");
            int d = differentDays(date1,date2);
            System.out.println(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void trydayForWeek(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 =  format.parse("2020-12-15");
            Date date2 = format.parse("2020-12-18");
            int e = dayForWeek(date1);
            int d = dayForWeek(date2);
            System.out.println(e);
            System.out.println(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tryemergencyTime() throws ParseException {
//        EverydayTotalTimeServer everydayTotalTimeServer = new EverydayTotalTimeServer();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 =  format.parse("2020-12-21");
        Date date2 = format.parse("2020-12-27");

//        Map<Integer,Integer> emergMap = emergencyTime(date1,date2);
    }


}