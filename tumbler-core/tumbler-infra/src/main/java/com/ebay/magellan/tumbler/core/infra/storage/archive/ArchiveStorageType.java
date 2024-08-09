package com.ebay.magellan.tumbler.core.infra.storage.archive;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

@Getter
public enum ArchiveStorageType {
    ES("ES"),
    ETCD("ETCD"),
    NONE("NONE"),
    ;

    private String name;

    ArchiveStorageType(String name) {
        this.name = name;
    }

    private static ArchiveStorageType DEFAULT = NONE;

    public static ArchiveStorageType getArchiveStorageType(String name) {
        for (ArchiveStorageType t : ArchiveStorageType.values()) {
            if (StringUtils.equalsIgnoreCase(name, t.name)) {
                return t;
            }
        }
        return DEFAULT;
    }
}
