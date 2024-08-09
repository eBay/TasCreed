package com.ebay.magellan.tascreed.depend.ext.etcd.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EtcdConstants {

    @Value("${etcd.timeout.in.seconds:60}")
    private long etcdTimeoutInSeconds;

    @Value("${etcd.lock.lease.seconds:60}")
    private int lockLeaseSeconds;

    @Value("${etcd.occupy.lease.seconds:60}")
    private int occupyLeaseSeconds;

    @Value("${etcd.heartbeat.period.seconds:15}")
    private int heartbeatPeriodSeconds;

}