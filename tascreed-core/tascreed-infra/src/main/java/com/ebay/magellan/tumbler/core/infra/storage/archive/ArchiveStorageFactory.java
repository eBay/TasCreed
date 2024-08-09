package com.ebay.magellan.tumbler.core.infra.storage.archive;

import com.ebay.magellan.tumbler.core.infra.constant.TumblerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ArchiveStorageFactory {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TumblerConstants tumblerConstants;

    private final ArchiveStorageList archiveStorage = new ArchiveStorageList();

    public ArchiveStorage getArchiveStorage() {
        return archiveStorage;
    }

    public void init() {
        for (String str : tumblerConstants.getStorageArchives()) {
            ArchiveStorageType st = ArchiveStorageType.getArchiveStorageType(str);
            archiveStorage.addArchiveStorage(findArchiveStorageByType(st));
        }
    }

    private ArchiveStorage findArchiveStorageByType(ArchiveStorageType st) {
        if (ArchiveStorageType.ES == st) {
            return context.getBean(EsArchiveStorage.class);
        } else if (ArchiveStorageType.ETCD == st) {
            return context.getBean(EtcdArchiveStorage.class);
        } else if (ArchiveStorageType.NONE == st) {
            return context.getBean(NoneArchiveStorage.class);
        }
        return null;
    }

}
