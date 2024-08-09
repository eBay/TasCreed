package com.ebay.magellan.tumbler.core.domain.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class SortUtil {

    public static <T extends Comparable<T>> T getFirstCandidate(List<T> candidates) {
        T first = null;
        if (CollectionUtils.isNotEmpty(candidates)) {
            first = Collections.max(candidates);
        }
        return first;
    }

}
