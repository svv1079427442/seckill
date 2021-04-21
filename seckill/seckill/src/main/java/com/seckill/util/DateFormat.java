package com.seckill.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static void main(String[] args) {
        try {
            Date date = simpleDateFormat.parse("2019-10-31");
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
