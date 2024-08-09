package com.ebay.magellan.tascreed.core.infra.help;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.request.JobRequest;
import com.ebay.magellan.tascreed.depend.common.util.JsonParseUtil;

public class TestRepo {

    public static JobDefine jobDefine = buildJobDefine();
    public static JobRequest jobRequest = buildJobRequest();

    private static JobDefine buildJobDefine() {
        String str = "{\"jobName\":\"sample\",\"version\":1,\"params\":{\"p1\":\"0\"},\"steps\":[{\"stepName\":\"prep\",\"stepType\":\"SHARD\",\"shardConf\":{\"shard\":4}},{\"stepName\":\"calc\",\"stepType\":\"PACK\",\"packConf\":{\"size\":100,\"start\":0,\"end\":1005,\"maxTaskCount\":6},\"dependentStep\":\"prep\"},{\"stepName\":\"aggr-1\",\"dependentStep\":\"calc\"},{\"stepName\":\"aggr-2\",\"dependentStep\":\"calc\",\"params\":{\"p1\":\"2\",\"p2\":\"4\"}}]}";
        try {
            return JobDefine.fromJson(str);
        } catch (Exception e) {
            return null;
        }
    }

    private static JobRequest buildJobRequest() {
        String str = "{\"jobName\":\"sample\",\"trigger\":\"20191020\"}";
        try {
            return JsonParseUtil.getObjectMapper().readValue(str, JobRequest.class);
        } catch (Exception e) {
            return null;
        }
    }

}
