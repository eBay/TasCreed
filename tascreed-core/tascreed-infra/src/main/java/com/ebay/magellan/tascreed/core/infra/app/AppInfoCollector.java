package com.ebay.magellan.tascreed.core.infra.app;

import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.util.HostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppInfoCollector {
    private static final String THIS_CLASS_NAME = AppInfoCollector.class.getSimpleName();

    @Autowired
    private TcConstants tcConstants;

    @Value("${app.version:unknown}")
    private String appVersion;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    // -----

    public String curTcVersion() {
        return tcConstants.getTcVersion();
    }

    // -----

    public String curAppVersion() {
        return appVersion;
    }

    // -----

    public String curHostName() {
        return HostUtil.getHostName();
    }

}
