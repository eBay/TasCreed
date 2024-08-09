package com.ebay.magellan.tascreed.depend.common.logger;

import com.ebay.magellan.tascreed.depend.common.util.Date2Util;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "tumbler", name = "logger", havingValue = "std", matchIfMissing = true)
public class TcStdLogger implements TcLogger {

    public void log(String type, String name, String data, String status) {
        if (type == null || name == null || data == null || status == null) return;

        System.out.println(String.format("[LOG] %s [%s] [%s] : %s", Date2Util.nowStr(), type, name, data));
    }

}
