package com.ebay.magellan.tascreed.depend.common.util;

import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class Date2Util {

    // -----

    public static Date parseDate(String s) throws TcException {
        return parseDate(s, DatePattern.UTC);
    }
    public static Date parseDate(String s, DatePattern datePattern) throws TcException {
        if (datePattern == null) return null;
        try {
            return datePattern.getFormatter().parseDateTime(s).toDate();
        } catch (IllegalArgumentException e) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_FATAL_VALIDATION_EXCEPTION, e.getMessage());
            return null;
        }
    }

    public static String formatDate(Date date) {
        return formatDate(date, DatePattern.UTC);
    }
    public static String formatDate(Date date, DatePattern datePattern) {
        if (datePattern == null) return null;
        if (date == null) return null;
        DateTime dt = new DateTime(date);
        return dt.toString(datePattern.getFormatter());
    }

    // -----

    public static String nowStr() {
        return DateTime.now().toString(DatePattern.UTC.getFormatter());
    }

    // -----

    public static enum DatePattern {
        UTC(genDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", 0, 0)),
        TIGHT_DATE_UTC(genDateTimeFormatter("yyyyMMdd", 0, 0)),
        TIGHT_TIME_UTC(genDateTimeFormatter("yyyyMMddHHmmss", 0, 0)),
        TIME_UTC(genDateTimeFormatter("yyyy-MM-dd HH:mm:ss", 0, 0)),
        TIME_CET(genDateTimeFormatter("yyyy-MM-dd HH:mm:ss", 1, 0)),
        TIME_MST(genDateTimeFormatter("yyyy-MM-dd HH:mm:ss", -7, 0)),
        REPORT_DATE_CET(genDateTimeFormatter("yyyy-MM-dd", 1, 0)),
        REPORT_FOLDER_NAME_DATE_CET(genDateTimeFormatter("yyyyMMdd", 1, 0)),
        REPORT_FILE_NAME_DATE_CET(genDateTimeFormatter("yyyy_MM_dd", 1, 0)),
        REPORT_FILE_META_DATE_CET(genDateTimeFormatter("yyyy-MM-dd 23:59:59", 1, 0)),
        ;

        private DateTimeFormatter formatter;

        DatePattern(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        public DateTimeFormatter getFormatter() {
            return this.formatter;
        }

        private static DateTimeFormatter genDateTimeFormatter(
                String pattern, int offsetHour, int offsetMin) {
            return DateTimeFormat.forPattern(pattern)
                    .withZone(DateTimeZone.forOffsetHoursMinutes(offsetHour, offsetMin));
        }
    }
}
