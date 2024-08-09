package com.ebay.magellan.tascreed.depend.ext.es.help;

import com.ebay.magellan.tascreed.depend.ext.es.doc.DocType;
import com.ebay.magellan.tascreed.depend.ext.es.doc.DocKey;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class UriBuilder {

    private static String ES_JOB_INDEX_PREFIX = "tascreed_job_";
    private static String ES_TASK_INDEX_PREFIX = "tascreed_task_";

    public static String base64Encode(String val) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(val.getBytes("utf-8"));
    }

//    public static String buildEsIndexOfJob(String namespace) {
//        return ES_JOB_INDEX_PREFIX + namespace.replace("/", "");
//    }
//
//    public static String buildEsIndexOfTask(String namespace) {
//        return ES_TASK_INDEX_PREFIX + namespace.replace("/", "");
//    }
//
//    public static String buildUriByJobKey(
//            String namespace, String jobKey) throws UnsupportedEncodingException {
//        return String.format("%s/_doc/%s", buildEsIndexOfJob(namespace), base64Encode(jobKey));
//    }
//
//    public static String buildUriByTaskKey(
//            String namespace, String taskKey) throws UnsupportedEncodingException {
//        return String.format("%s/_doc/%s", buildEsIndexOfTask(namespace), base64Encode(taskKey));
//    }

    // -----

    public static String buildEsIndex(DocKey docKey) throws UnsupportedEncodingException {
        if (docKey != null) {
            if (docKey.getType() == DocType.JOB) {
                return ES_JOB_INDEX_PREFIX + docKey.getNamespace().replace("/", "");
            } else if (docKey.getType() == DocType.TASK) {
                return ES_TASK_INDEX_PREFIX + docKey.getNamespace().replace("/", "");
            }
        }
        return null;
    }

    public static String buildEsUri(DocKey docKey) throws UnsupportedEncodingException {
        if (docKey != null) {
            String index = buildEsIndex(docKey);
            if (index != null) {
                return String.format("%s/_doc/%s", index, base64Encode(docKey.getKey()));
            }
        }
        return null;
    }

}
