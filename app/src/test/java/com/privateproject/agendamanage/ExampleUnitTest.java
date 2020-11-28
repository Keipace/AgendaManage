package com.privateproject.agendamanage;

import com.privateproject.agendamanage.server.EverydayTotalTimeServer;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public void tryWeek() {
        Date date = new Date();
        try {
           int dayint =  dayForWeek(date);
           System.out.println(dayint);
        } catch (Exception e) {
            e.printStackTrace();
        }
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



}