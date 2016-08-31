package org.jahia.modules.jdbcDatabaseProvider.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The type Jdbc utils.
 */
public class JDBCUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");

    /**
     * Display moving time string.
     *
     * @param moving_time the moving time
     * @return the string
     */
    public static String displayMovingTime(String moving_time) {
        int totalSeconds = Integer.parseInt(moving_time);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        String moving_time_display;
        if (hours != 0) {
            moving_time_display = hours + ":" + displayNumberTwoDigits(minutes) + ":" + displayNumberTwoDigits(seconds);
        } else {
            if (minutes != 0) {
                moving_time_display = minutes + ":" + displayNumberTwoDigits(seconds);
            } else {
                moving_time_display = "" + seconds;
            }
        }
        return moving_time_display;
    }

    /**
     * Gets moving time.
     *
     * @param moving_time the moving time
     * @return the moving time
     */
    public static String getMovingTime(String moving_time) {

        String[] parts = moving_time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        int temp = seconds + (60 * minutes) + (3600 * hours);

        return temp + "";
    }


    /**
     * Display start date string.
     *
     * @param start_date the start date
     * @return the string
     * @throws ParseException the parse exception
     */
    public static String displayStartDate(String start_date) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-M-dd").parse(start_date);
        String start_date_formatted = new SimpleDateFormat("E dd/MM/yyyy").format(date);
        return start_date_formatted;
    }

    /**
     * Display distance string.
     *
     * @param distance the distance
     * @return the string
     */
    public static String displayDistance(String distance) {
        return DECIMAL_FORMAT.format(Double.parseDouble(distance) / 1000);
    }

    /**
     * Display number two digits string.
     *
     * @param number the number
     * @return the string
     */
    public static String displayNumberTwoDigits(int number) {
        if (number <= 9) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

}
