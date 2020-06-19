package com.spbstu.util;

import com.spbstu.dbo.Time;
import com.spbstu.exceptions.TimeFormatException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
public class StringTypeVerifier {
    private static SimpleDateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    public static Date date(String dateAsString, String errorMessage) throws Exception {
        try {
            return dFormat.parse(dateAsString);
        } catch (ParseException ex) {
            throw new Exception(errorMessage);
        }
    }

    public static Time time(String timeAsString, String errorMessage) throws Exception {
        try {
            return Time.parse(timeAsString);
        } catch (TimeFormatException ignored) {
            throw new Exception(errorMessage);
        }
    }

    public static int integer(String intAsString, String errorMessage) throws Exception {
        try {
            return Integer.parseInt(intAsString);
        } catch (NumberFormatException ex) {
            throw new Exception(errorMessage);
        }
    }

    public static void notEmptyString(String str, String errorMessage) throws Exception {
        if (str == null || str.equals("")) {
            throw new Exception(errorMessage);
        }
    }
}
