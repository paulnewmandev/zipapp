package com.zipnoticiasec.ap.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andmari on 4/12/2018.
 */

public class FormUtils {

    public static Date yyyyMMddTHHmmssToDate(String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        try {
            return formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date yyyyMMddToDate(String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
        try {
            return formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String currentDate(){
        return dateToStringyyyyMMdd(new Date());
    }

    public static String dateToStringddMMMyyyy(Date date){
        SimpleDateFormat mFormat =
                new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
        return mFormat.format(date);
    }

    public static String dateToStringyyyyMMdd(Date date){
        SimpleDateFormat mFormat =
                new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
        return mFormat.format(date);
    }

    public static String dateToStringyyyyMMddTHHmmss(Date date){
        SimpleDateFormat mFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        return mFormat.format(date);
    }

    public static String dateToStringLongDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        sdf.applyPattern("EEEE, dd MMM yyyy");
        return sdf.format(date);
    }
}
