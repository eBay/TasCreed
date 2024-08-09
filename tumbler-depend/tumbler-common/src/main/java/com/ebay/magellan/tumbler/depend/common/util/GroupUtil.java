package com.ebay.magellan.tumbler.depend.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupUtil {

    public static final long SECOND = 1000L;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;

    public static <T> List<List<T>> groupListByGroupCount(List<T> list, int groupCount) {
        List<List<T>> subLists = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) return subLists;
        if (groupCount <= 0) {
            subLists.add(list);
        } else {
            int totalSize = list.size();
            int sizePerSubList =
                    totalSize % groupCount == 0
                            ? totalSize / groupCount
                            : totalSize / groupCount + 1;
            for (int i = 0; i < groupCount; i++) {
                int startIndex = i * sizePerSubList;
                if (startIndex >= totalSize) break;
                List<T> subList = list.subList(startIndex, Math.min(startIndex + sizePerSubList, totalSize));
                subLists.add(subList);
            }
        }

        return subLists;
    }

    public static <T> List<List<T>> groupListBySizePerGroup(List<T> list, int sizePerGroup) {
        List<List<T>> groups = new ArrayList<>();
        if (sizePerGroup <= 0) {
            groups.add(list);
        } else {
            int len = list.size();
            int idx = 0;
            while (idx < len) {
                int from = idx;
                int end = Math.min(idx + sizePerGroup, len);
                groups.add(list.subList(from, end));
                idx = end;
            }
        }
        return groups;
    }

    /**
     * @param startDate
     * @param endDate
     * @param splitCount split to this count
     * @return a list of split date ranges,
     * Pair.Left is startDate of one split, including
     * Pair.Right is endDate of one split, excluding
     */
    public static List<Pair<Date, Date>> splitDateRangeByCount(Date startDate, Date endDate, int splitCount) {
        List<Pair<Date, Date>> list = new ArrayList<>();
        if (startDate == null || endDate == null || splitCount <= 0) return list;
        if (startDate.after(endDate)) {
            Date tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        long timePerSplit = (endTime - startTime) % splitCount == 0
                ? (endTime - startTime) / splitCount
                : (endTime - startTime) / splitCount + 1;

        for (int i = 0; i < splitCount; i++) {
            long start = startTime + i * timePerSplit;
            long end = Math.min(start + timePerSplit, endTime);
            if (start >= endTime) break;
            Pair<Date, Date> pair = new ImmutablePair<>(new Date(start), new Date(end));

            list.add(pair);
        }

        return list;
    }

    /**
     * @param startDate
     * @param endDate
     * @param timeIntervalInMs split by timeRangeIntervalInMs
     * @return a list of split date ranges,
     * Pair.Left is startDate of one split, including
     * Pair.Right is endDate of one split, excluding
     */
    public static List<Pair<Date, Date>> splitDateRangeByTimeInterval(Date startDate, Date endDate, long timeIntervalInMs) {
        List<Pair<Date, Date>> list = new ArrayList<>();
        if (startDate == null || endDate == null) return list;
        if (timeIntervalInMs <= 0) return list;
        if (startDate.after(endDate)) {
            Date tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }

        long st = startDate.getTime();
        long et = endDate.getTime();
        while (st < et) {
            long t = st + timeIntervalInMs;
            t = t < et ? t : et;
            Pair<Date, Date> pair = new ImmutablePair<>(new Date(st), new Date(t));
            list.add(pair);
            st = t;
        }

        return list;
    }

}
