package com.ebay.magellan.tumbler.depend.common.util;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import org.joda.time.DateTime;

import java.util.Date;

import static com.ebay.magellan.tumbler.depend.common.util.Date2Util.DatePattern;

public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_NO_TIME = "yyyyMMdd";
    public static final String TIME_FORMAT_IN_SECOND = "yyyyMMddHHmmss";

    public static final int LONG_LONG_AGO_YEAR = 1995;

    public static final long DAY_TO_MS = 24 * 60 * 60 * 1000L;

    // -----

    public static Date parseWithoutTimeZone(String date) throws TumblerException {
        return Date2Util.parseDate(date);
    }

    public static String formatWithoutTimeZone(Date date) {
        return Date2Util.formatDate(date);
    }

    public static Date parseWithoutTime(String date) throws TumblerException {
        return Date2Util.parseDate(date, DatePattern.TIGHT_DATE_UTC);
    }
    public static String formatWithoutTime(Date date) {
        return Date2Util.formatDate(date, DatePattern.TIGHT_DATE_UTC);
    }

    public static Date parseTightTimeUTC(String date) throws TumblerException {
        return Date2Util.parseDate(date, DatePattern.TIGHT_TIME_UTC);
    }
    public static String formatTightTimeUTC(Date date) {
        return Date2Util.formatDate(date, DatePattern.TIGHT_TIME_UTC);
    }

    public static Date parseWithUTC(String date) throws TumblerException {
        return Date2Util.parseDate(date, DatePattern.UTC);
    }

    public static String formatWithUTC(Date date) {
        return Date2Util.formatDate(date, DatePattern.UTC);
    }

    // -----

    public static Date parseCetDateTime(String str) throws TumblerException {
        return Date2Util.parseDate(str, DatePattern.REPORT_DATE_CET);
    }

    public static Date oneDayBefore(Date date) {
        return new DateTime(date).minusDays(1).toDate();
    }
    public static Date oneDayAfter(Date date) {
        return new DateTime(date).plusDays(1).toDate();
    }

    public static Date daysBefore(Date date, int days) {
        return new DateTime(date).minusDays(days).toDate();
    }
    public static Date hoursBefore(Date date, int hours) {
        return new DateTime(date).minusHours(hours).toDate();
    }

    public static String formatReportDate(Date date) {
        return Date2Util.formatDate(date, DatePattern.REPORT_DATE_CET);
    }

    public static String formatReportFolderNameDate(Date date) {
        return Date2Util.formatDate(date, DatePattern.REPORT_FOLDER_NAME_DATE_CET);
    }

    public static String formatReportFileNameDate(Date date) {
        return Date2Util.formatDate(date, DatePattern.REPORT_FILE_NAME_DATE_CET);
    }

    public static String formatReportFileMetaDate(Date date) {
        return Date2Util.formatDate(date, DatePattern.REPORT_FILE_META_DATE_CET);
    }

    public static Date getLongLongAgoDate() {
        DateTime longLongAgo = DateTime.now().withYear(LONG_LONG_AGO_YEAR);
        return longLongAgo.toDate();
    }

    public static String toUTCDateString(String date) throws TumblerException {
        Date d = Date2Util.parseDate(date, DatePattern.TIME_MST);
        return Date2Util.formatDate(d, DatePattern.UTC);
    }

    public static Date string2Date(String date) throws TumblerException {
        return Date2Util.parseDate(date, DatePattern.TIME_MST);
    }

    // -----

    // current time in milliseconds reaches given date or not
    public static boolean reachDate(long curTime, Date date) {
        if (date == null) return true;
        return curTime >= date.getTime();
    }

}
