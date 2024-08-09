package com.ebay.magellan.tumbler.depend.ext.etcd.lock;

import io.etcd.jetcd.ByteSequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EtcdLock {
    private long leaseId;
    private ByteSequence key;
}
