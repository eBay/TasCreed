package com.ebay.magellan.tascreed.depend.ext.es.util;

import com.ebay.magellan.tascreed.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.common.exception.TumblerExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.ext.es.doc.*;
import com.ebay.magellan.tascreed.depend.ext.es.help.UriBuilder;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * mock EsUtil, using local RocksDB as storage, for testing purpose at local
 */
@Component
@ConditionalOnProperty(prefix = "tumbler", name = "es", havingValue = "disk", matchIfMissing = true)
public class DiskEsUtil implements EsUtil {

    private static final String THIS_CLASS_NAME = DiskEsUtil.class.getSimpleName();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TumblerLogger logger;

    // -----

    static private final String dbPath = "rocks_data";
    static private RocksDB rocksDB = null;
    static private RocksDB getRocksDB() {
        if (rocksDB == null) {
            try {
                RocksDB.loadLibrary();
                Options options = new Options();
                options.setCreateIfMissing(true);
                rocksDB = RocksDB.open(options, dbPath);
            } catch (RocksDBException e) {
                e.printStackTrace();
            }
        }
        return rocksDB;
    }

    // ----- read -----

    public DocValue getDocValue(DocKey docKey) throws TumblerException, UnsupportedEncodingException {
        String key = UriBuilder.base64Encode(docKey.uniqueKey());
        DocValue ret = null;
        try {
            byte[] value = getRocksDB().get(key.getBytes());
            if (value != null) {
                ret = DocValue.fromJson(new String(value));
            }
        } catch (Exception e) {
            String errMsg = String.format("getDocValue failed, key: %s, error: %s", key, e.getMessage());
            logger.error(THIS_CLASS_NAME, errMsg);
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, errMsg);
        }
        return ret;
    }

    // ----- write -----

    public void syncPutDoc(DocKey docKey, DocValue docValue) throws TumblerException, UnsupportedEncodingException {
        String key = UriBuilder.base64Encode(docKey.uniqueKey());
        try {
            String value = docValue.toJson();
            getRocksDB().put(key.getBytes(), value.getBytes());
        } catch (Exception e) {
            String errMsg = String.format("syncPutDoc failed, key: %s, value: %s, error: %s", key, docValue, e.getMessage());
            logger.error(THIS_CLASS_NAME, errMsg);
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_NON_RETRY_EXCEPTION, errMsg);
        }
    }

    // -----

    // mock impl, actually not async put, not guaranteed persistence
    public void asyncPutDocs(Map<DocKey, DocValue> docKvs, int sizePerGroup) throws UnsupportedEncodingException {
        for (Map.Entry<DocKey, DocValue> entry : docKvs.entrySet()) {
            try {
                syncPutDoc(entry.getKey(), entry.getValue());
            } catch (TumblerException e) {
                String errMsg = String.format("asyncPutDocs failed, key: %s, value: %s, error: %s", entry.getKey(), entry.getValue(), e.getMessage());
                logger.error(THIS_CLASS_NAME, errMsg);
                break;
            }
        }
    }

}
