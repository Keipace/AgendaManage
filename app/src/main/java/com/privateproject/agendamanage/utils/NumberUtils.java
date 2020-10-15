package com.privateproject.agendamanage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

    public static String moveStartZero(String number) {
        Pattern pattern = Pattern.compile("^0+");
        Matcher matcher = pattern.matcher(number);
        return matcher.replaceAll("");
    }

}
