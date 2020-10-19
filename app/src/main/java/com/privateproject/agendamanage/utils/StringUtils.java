package com.privateproject.agendamanage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**/
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

}
