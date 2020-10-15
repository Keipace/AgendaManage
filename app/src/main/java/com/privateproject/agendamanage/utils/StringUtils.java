package com.privateproject.agendamanage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /*
    * 去除字符串两端的空白字符
    * 若字符串是换行、空格、tab等空白字符组成时，返回"" */
    public static String moveSpaceString(String string) {
        String result;
        Pattern pattern = Pattern.compile("^\\s$");
        Matcher matcher = pattern.matcher(string);
        result = matcher.replaceAll("");
        if(result.equals("")) {
            return "";
        }
        string = string.trim();
        return string;
    }

}
