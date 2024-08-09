package com.ebay.magellan.tascreed.core.domain.util;

import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RangeUtil {

    // -----

    // sample: [1,5];[7,12];[34,350]
    private static final String DELIMITER_BETWEEN_RANGE = ";";
    private static final Pattern rangePattern = Pattern.compile("\\[(\\d+),(\\d+)\\]");

    public static RangeSet<Long> rangeSetFromString(String s) {
        TreeRangeSet rs = TreeRangeSet.create();
        if (StringUtils.isNotBlank(s)) {
            String[] arr = StringUtils.split(s, DELIMITER_BETWEEN_RANGE);
            if (arr != null) {
                for (String r : arr) {
                    Range<Long> range = rangeFromString(r);
                    if (range != null) {
                        rs.add(range);
                    }
                }
            }
        }
        return rs;
    }

    private static Range<Long> rangeFromString(String r) {
        if (StringUtils.isBlank(r)) return null;
        Matcher matcher = rangePattern.matcher(r);
        if (matcher.matches()) {
            long start = Long.valueOf(matcher.group(1));
            long end = Long.valueOf(matcher.group(2));
            return Range.closed(start, end);
        } else {
            return null;
        }
    }

    public static String rangeSetToString(RangeSet<Long> rangeSet) {
        if (rangeSet == null || rangeSet.isEmpty()) return null;
        return StringUtils.join(rangeSet.asRanges().stream()
                .map(r -> rangeToString(r))
                .filter(r -> r != null)
                .collect(Collectors.toList()), DELIMITER_BETWEEN_RANGE);
    }

    private static String rangeToString(Range<Long> r) {
        if (r == null || r.isEmpty()) return null;
        Range<Long> range = r;
        // normalize to closed range
        if (r.lowerBoundType() == BoundType.CLOSED && r.upperBoundType() == BoundType.OPEN) {
            range = Range.closed(r.lowerEndpoint(), r.upperEndpoint() - 1);
        } else if (r.lowerBoundType() == BoundType.OPEN && r.upperBoundType() == BoundType.CLOSED) {
            range = Range.closed(r.lowerEndpoint() + 1, r.upperEndpoint());
        } else if (r.lowerBoundType() == BoundType.OPEN && r.upperBoundType() == BoundType.OPEN) {
            range = Range.closed(r.lowerEndpoint() + 1, r.upperEndpoint() - 1);
        }
        return String.format("[%d,%d]", range.lowerEndpoint(), range.upperEndpoint());
    }

    public static void addRange(RangeSet<Long> rs, Range<Long> r) {
        if (rs == null) return;
        if (r == null || r.isEmpty()) return;
        Range<Long> range = r;
        // normalize to open range to add into current range set
        if (r.lowerBoundType() == BoundType.CLOSED && r.upperBoundType() == BoundType.CLOSED) {
            range = Range.open(r.lowerEndpoint() - 1, r.upperEndpoint() + 1);
        } else if (r.lowerBoundType() == BoundType.CLOSED && r.upperBoundType() == BoundType.OPEN) {
            range = Range.open(r.lowerEndpoint() - 1, r.upperEndpoint());
        } else if (r.lowerBoundType() == BoundType.OPEN && r.upperBoundType() == BoundType.CLOSED) {
            range = Range.open(r.lowerEndpoint(), r.upperEndpoint() + 1);
        }
        rs.add(range);
    }

    // -----

    public static Long findWatermark(RangeSet<Long> rs, Long startPoint) {
        if (rs == null) return null;
        // find min range, min range should have the min upper bound
        Range<Long> minRange = null;
        for (Range<Long> r : rs.asRanges()) {
            if (minRange == null || (getUpperBoundOfRange(r) < getUpperBoundOfRange(minRange))) {
                minRange = r;
            }
        }
        return findWatermarkByMinRange(minRange, startPoint);
    }

    private static Long findWatermarkByMinRange(Range<Long> minRange, Long startPoint) {
        if (minRange == null) return null;
        Long upperBound = getUpperBoundOfRange(minRange);
        // ignore start point check if null
        if (startPoint == null) return upperBound;

        // compare lower bound with start point
        Long lowerBound = getLowerBoundOfRange(minRange);
        if (lowerBound <= startPoint) {
            // start point is covered, the watermark should be the upper bound of min range
            return upperBound;
        } else {
            return startPoint - 1;
        }
    }

    private static Long getLowerBoundOfRange(Range<Long> r) {
        if (r == null) return null;
        // normalize to closed lower bound
        if (r.lowerBoundType() == BoundType.OPEN) {
            return r.lowerEndpoint() + 1;
        } else {
            return r.lowerEndpoint();
        }
    }
    private static Long getUpperBoundOfRange(Range<Long> r) {
        if (r == null) return null;
        // normalize to closed upper bound
        if (r.upperBoundType() == BoundType.OPEN) {
            return r.upperEndpoint() - 1;
        } else {
            return r.upperEndpoint();
        }
    }
}
