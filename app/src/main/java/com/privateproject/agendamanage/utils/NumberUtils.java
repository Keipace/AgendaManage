package com.privateproject.agendamanage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

    // 去除开头的0（但不去除0.和0）
    public static String moveStartZero(String number) {
        // 只有字符时不作任何处理
        if(number.length()==1) {
            return number;
        }
        // 如果有两个或以上的字符，并且开始两个字符是'0.'则不作任何处理
        if (number.length()>=2 && number.charAt(0)=='0' && number.charAt(1)=='.') {
            return number;
        }
        // 将开头的几个0去除掉
        Pattern pattern = Pattern.compile("^0+");
        Matcher matcher = pattern.matcher(number);
        return matcher.replaceAll("");
    }

    /*只允许最后有一位小数*/
    public static String onlyOneDecimal(String number) {
        // 如果不包含小数点则不作任何处理
        if(number.contains(".")) {
            // 如果小数点的位置在第一位，则直接去掉小数点
            if(number.indexOf(".")==0) {
                return "";
            }
            // 从第一个小数点之后的字符数如果大于等于2的话，只保留小数点后的一位
            String[] temp = number.split("\\.");
            if(temp.length>=2 && temp[1].length()>=2) {
                return temp[0]+"."+temp[1].charAt(0);
            }
        }
        return number;
    }

    // 如果最后一位是小数点，则删除
    public static String deleteEndDecimal(String number) {
        // 如果传入的是空字符串，直接返回
        if (number.equals(""))
            return number;
        // 至少有一位字符时才能判断最后一位
        if(number.charAt(number.length()-1)=='.') {
            number = number.replaceFirst(".$", "");
        }
        return number;
    }

}
