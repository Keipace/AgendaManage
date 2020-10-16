package com.privateproject.agendamanage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

    public static String moveStartZero(String number) {
        if(number.length()==1) {
            return number;
        }
        if (number.length()>=2 && number.charAt(0)=='0' && number.charAt(1)=='.') {
            return number;
        }
        Pattern pattern = Pattern.compile("^0+");
        Matcher matcher = pattern.matcher(number);
        return matcher.replaceAll("");
    }

    /*只允许*/
    public static String onlyOneDecimal(String number) {
        if(number.contains(".")) {
            if(number.indexOf(".")==0) {
                return "";
            }
            String[] temp = number.split("\\.");
            if(temp.length>=2 && temp[1].length()>=2) {
                return temp[0]+"."+temp[1].charAt(0);
            }
        }
        return number;
    }

}
