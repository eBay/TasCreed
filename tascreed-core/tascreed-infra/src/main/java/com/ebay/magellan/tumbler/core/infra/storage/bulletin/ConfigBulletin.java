package com.ebay.magellan.tumbler.core.infra.storage.bulletin;

import java.util.Map;

public interface ConfigBulletin extends BaseBulletin {

    String readConfig(String key);
    String readConfig(String key, String defaultValue);

    Map<String, String> readConfigs(String prefix) throws Exception;

    // -----

    void updateConfig(String key, String value) throws Exception;

}
