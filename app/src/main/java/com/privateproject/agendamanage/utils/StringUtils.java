package com.privateproject.agendamanage.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
/*    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");*/

    public static String moveSpaceString(String string) {
        String result;
        // 如果字符串是有空白字符（换行、空格、tab）组成，则直接替换成 ""
        Pattern pattern = Pattern.compile("^\\s$");
        Matcher matcher = pattern.matcher(string);
        result = matcher.replaceAll("");
        if(result.equals("")) {
            return "";
        }
        // 如果字符串两边有空白字符，则去除两边的空白字符
        string = string.trim();
        return string;
    }

/*    *//*将yyyy-MM-dd格式的字符串转换成Date类型的数据*//*
    public static Date getDateFromString(String date) {
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    *//*将date类型的数据转换成yyyy-MM-dd格式的字符串*//*
    public static String getDateToString(Date date) {
        return sdf.format(date);
    }*/
}
