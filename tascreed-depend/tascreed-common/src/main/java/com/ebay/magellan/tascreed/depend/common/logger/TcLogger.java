package com.ebay.magellan.tascreed.depend.common.logger;

public interface TcLogger {

    String TC_INFO = "TC_INFO";
    String TC_WARN = "TC_WARN";
    String TC_ERROR = "TC_ERROR";

    // ------------ log ------------

    default void info(String name, String data) {
        log(TC_INFO, name, data, TC_INFO);
    }

    default void warn(String name, String data) {
        log(TC_WARN, String.format("WARN_%s", name), data, TC_WARN);
    }

    default void error(String name, String data) {
        log(TC_ERROR, String.format("ERROR_%s", name), data, TC_ERROR);
    }

    void log(String type, String name, String data, String status);

}
