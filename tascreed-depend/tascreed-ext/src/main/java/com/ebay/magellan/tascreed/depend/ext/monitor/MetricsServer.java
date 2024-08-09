package com.ebay.magellan.tascreed.depend.ext.monitor;

import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import io.prometheus.client.exporter.HTTPServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MetricsServer implements InitializingBean {

    private static final String THIS_CLASS_NAME = MetricsServer.class.getSimpleName();

    @Value("${tumbler.metrics.server.enable:true}")
    private boolean serverEnable;
    @Value("${tumbler.metrics.server.port:9091}")
    private int port;

    private HTTPServer server;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() {
        if (serverEnable) {
            try {
                server = new HTTPServer(port);
                logger.info(THIS_CLASS_NAME, String.format("Metrics server is started on port %s", port));
            } catch (Exception e) {
                logger.error(THIS_CLASS_NAME, String.format("Metrics server not started on port %s: %s", port, e.getMessage()));
            }
        } else {
            logger.warn(THIS_CLASS_NAME, "Metrics server is disabled");
        }
    }

}
