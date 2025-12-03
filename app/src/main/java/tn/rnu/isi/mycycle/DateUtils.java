package tn.rnu.isi.mycycle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date operations
 */
public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Get current date as string (yyyy-MM-dd)
     */
    public static String getCurrentDate() {
        return dateFormatter.format(new Date());
    }

    /**
     * Format date to string
     */
    public static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    /**
     * Format date to string
     */
    public static String formatDate(Calendar calendar) {
        return dateFormatter.format(calendar.getTime());
    }

    /**
     * Parse date string to Date object
     */
    public static Date parseDate(String dateString) {
        try {
            return dateFormatter.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Add days to a date string
     */
    public static String addDays(String dateString, int days) {
        try {
            Date date = dateFormatter.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return dateFormatter.format(calendar.getTime());
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Calculate days between two dates
     */
    public static int daysBetween(String startDate, String endDate) {
        try {
            Date start = dateFormatter.parse(startDate);
            Date end = dateFormatter.parse(endDate);
            long diff = end.getTime() - start.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get date string for display (e.g., "Today, June 18" or "June 18, 2025")
     */
    public static String getDisplayDate(String dateString) {
        try {
            Date date = dateFormatter.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            
            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            
            SimpleDateFormat displayFormat;
            if (isSameDay(calendar, today)) {
                return "Today, " + new SimpleDateFormat("MMMM d", Locale.getDefault()).format(date);
            } else if (isSameDay(calendar, tomorrow)) {
                return "Tomorrow, " + new SimpleDateFormat("MMMM d", Locale.getDefault()).format(date);
            } else {
                return new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(date);
            }
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Check if two calendars represent the same day
     */
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Get start of month date string
     */
    public static String getStartOfMonth(String dateString) {
        try {
            Date date = dateFormatter.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return dateFormatter.format(calendar.getTime());
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Get end of month date string
     */
    public static String getEndOfMonth(String dateString) {
        try {
            Date date = dateFormatter.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return dateFormatter.format(calendar.getTime());
        } catch (Exception e) {
            return dateString;
        }
    }
}

