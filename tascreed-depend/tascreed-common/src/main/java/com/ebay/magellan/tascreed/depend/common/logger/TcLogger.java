package com.ebay.magellan.tascreed.depend.common.logger;

public interface TcLogger {

    String TUMBLER_INFO = "TUMBLER_INFO";
    String TUMBLER_WARN = "TUMBLER_WARN";
    String TUMBLER_ERROR = "TUMBLER_ERROR";

    // ------------ log ------------

    default void info(String name, String data) {
        log(TUMBLER_INFO, name, data, TUMBLER_INFO);
    }

    default void warn(String name, String data) {
        log(TUMBLER_WARN, String.format("WARN_%s", name), data, TUMBLER_WARN);
    }

    default void error(String name, String data) {
        log(TUMBLER_ERROR, String.format("ERROR_%s", name), data, TUMBLER_ERROR);
    }

    void log(String type, String name, String data, String status);

}
