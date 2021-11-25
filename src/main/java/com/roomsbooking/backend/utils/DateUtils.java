package com.roomsbooking.backend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Method responsible for checking if two date ranges overlap or are the same.
     *
     * @param firstStartDate  start of the first date range
     * @param firstEndDate    end of the first date range
     * @param secondStartDate start of the second date range
     * @param secondEndDate   end of the second date range
     * @return boolean that indicates if two date ranges overlap or are the same
     */
    public static boolean areDateRangesOverlapOrAreTheSame(Date firstStartDate, Date firstEndDate,
        Date secondStartDate, Date secondEndDate) {
        return areDateRangesOverlap(firstStartDate, firstEndDate, secondStartDate, secondEndDate) ||
            areDateRangesTheSame(firstStartDate, firstEndDate, secondStartDate, secondEndDate);
    }

    private static boolean areDateRangesOverlap(Date firstStartDate, Date firstEndDate,
        Date secondStartDate, Date secondEndDate) {
        return firstStartDate.before(secondEndDate) && secondStartDate.before(firstEndDate);
    }

    private static boolean areDateRangesTheSame(Date firstStartDate, Date firstEndDate,
        Date secondStartDate, Date secondEndDate) {
        return firstStartDate.equals(secondStartDate) && firstEndDate.equals(secondEndDate);
    }
}
