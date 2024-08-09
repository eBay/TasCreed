package com.ebay.magellan.tascreed.depend.ext.es.doc;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class DocKey {
    private DocType type;
    private String namespace;
    private String key;

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", type, namespace, key);
    }

    public String uniqueKey() {
        return String.format("%s/%s", namespace, key);
    }
}
