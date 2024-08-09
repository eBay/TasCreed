package com.ebay.magellan.tascreed.depend.ext.es.util;

import com.ebay.magellan.tascreed.depend.common.exception.TumblerException;
import com.ebay.magellan.tascreed.depend.ext.es.doc.DocKey;
import com.ebay.magellan.tascreed.depend.ext.es.doc.DocValue;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface EsUtil {

    /**
     * get doc value from es
     */
    DocValue getDocValue(DocKey docKey) throws TumblerException, UnsupportedEncodingException;

    /**
     * put a doc into es
     */
    void syncPutDoc(DocKey docKey, DocValue docValue) throws TumblerException, UnsupportedEncodingException;

    /**
     * put docs in other threads, async with main thread
     * write es in async way means it is acceptable that fail to write es
     */
    void asyncPutDocs(Map<DocKey, DocValue> docKvs, int sizePerGroup) throws UnsupportedEncodingException;
}
