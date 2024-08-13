package com.ebay.magellan.tascreed.depend.ext.es.util;

import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.es.doc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * mock EsUtil, using memory as storage, for testing purpose at local
 */
@Component
@ConditionalOnProperty(prefix = "tascreed", name = "es", havingValue = "disk", matchIfMissing = true)
public class MemEsUtil implements EsUtil {

    private static final String THIS_CLASS_NAME = MemEsUtil.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    // -----

    private final Map<String, String> memStore = new HashMap<>();

    // ----- read -----

    public DocValue getDocValue(DocKey docKey) throws TcException {
        String key = docKey.uniqueKey();
        DocValue ret = null;
        try {
            String value = memStore.get(key);
            if (value != null) {
                ret = DocValue.fromJson(value);
            }
        } catch (Exception e) {
            String errMsg = String.format("getDocValue failed, key: %s, error: %s", key, e.getMessage());
            logger.error(THIS_CLASS_NAME, errMsg);
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION, errMsg);
        }
        return ret;
    }

    // ----- write -----

    public void syncPutDoc(DocKey docKey, DocValue docValue) throws TcException {
        String key = docKey.uniqueKey();
        try {
            String value = docValue.toJson();
            memStore.put(key, value);
        } catch (Exception e) {
            String errMsg = String.format("syncPutDoc failed, key: %s, value: %s, error: %s", key, docValue, e.getMessage());
            logger.error(THIS_CLASS_NAME, errMsg);
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION, errMsg);
        }
    }

    // -----

    // mock impl, actually not async put, not guaranteed persistence
    public void asyncPutDocs(Map<DocKey, DocValue> docKvs, int sizePerGroup) {
        for (Map.Entry<DocKey, DocValue> entry : docKvs.entrySet()) {
            try {
                syncPutDoc(entry.getKey(), entry.getValue());
            } catch (TcException e) {
                String errMsg = String.format("asyncPutDocs failed, key: %s, value: %s, error: %s", entry.getKey(), entry.getValue(), e.getMessage());
                logger.error(THIS_CLASS_NAME, errMsg);
                break;
            }
        }
    }

}
