package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    private static final String DEFAULT_DATE_FORMAT = "dd MMMM yyyy";

    public static String formatDate(Date date, String format, Locale locale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return dateFormat.format(date);
    }

    public static String formatDate(Date date, Locale locale) {
        return formatDate(date, DEFAULT_DATE_FORMAT, locale);
    }

    public static Date parseDate(String dateString, String format, Locale locale) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return dateFormat.parse(dateString);
    }

    public static Date parseDate(String dateString, Locale locale) throws ParseException {
        return parseDate(dateString, DEFAULT_DATE_FORMAT, locale);
    }
}
