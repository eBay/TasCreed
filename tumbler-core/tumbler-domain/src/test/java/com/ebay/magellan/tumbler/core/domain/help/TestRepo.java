package com.ebay.magellan.tumbler.core.domain.help;

import com.ebay.magellan.tumbler.core.domain.define.JobDefine;
import com.ebay.magellan.tumbler.core.domain.request.JobRequest;
import com.ebay.magellan.tumbler.depend.common.util.JsonParseUtil;

public class TestRepo {

    public static JobDefine jobDefine = buildJobDefine();
    public static JobRequest jobRequest1 = buildJobRequest1();
    public static JobRequest jobRequest2 = buildJobRequest2();
    public static JobRequest jobRequestPattern = buildJobRequestPattern();

    private static JobDefine buildJobDefine() {
        String str = "{\"jobName\":\"sample\",\"version\":1,\"traits\": [\"canIgnore\",\"canFail\",\"deleted\",\"ARCHIVE\",\"unknown\"],\"priority\":1,\"params\":{\"p1\":\"0\"},\"steps\":[{\"stepName\":\"prep\",\"traits\": [\"canIgnore\",\"canFail\",\"deleted\",\"ARCHIVE\",\"unknown\"],\"stepType\":\"SHARD\",\"shardConf\":{\"shard\":4,\"maxTaskCount\":2}},{\"stepName\":\"calc\",\"stepType\":\"PACK\",\"packConf\":{\"size\":100,\"start\":0,\"end\":1005,\"maxTaskCount\":6},\"dependentStep\":\"prep\"},{\"stepName\":\"aggr-1\",\"dependentStep\":\"calc\"},{\"stepName\":\"aggr-2\",\"dependentStep\":\"calc\",\"ignorable\":true,\"params\":{\"p1\":\"2\",\"p2\":\"4\"}}]}";
        try {
            return JobDefine.fromJson(str);
        } catch (Exception e) {
            return null;
        }
    }

    private static JobRequest buildJobRequest1() {
        String str = "{\"jobName\":\"sample\",\"trigger\":\"20191020\",\"priority\":2,\"params\":{\"p1\":\"1\",\"p2\":\"2\"},\"steps\":[{\"stepName\":\"prep\",\"shardConf\":{\"shard\":6},\"packConf\":{\"size\":1000}}]}";
        try {
            return JsonParseUtil.getObjectMapper().readValue(str, JobRequest.class);
        } catch (Exception e) {
            return null;
        }
    }
    private static JobRequest buildJobRequest2() {
        String str = "{\"jobName\":\"sample\",\"trigger\":\"20191020\",\"traits\":{\"enable\":[\"canIgnore\",\"deleted\"]},\"params\":{\"p1\":\"1\",\"p2\":\"2\"},\"steps\":[{\"stepName\":\"prep\",\"traits\":{\"disable\":[\"canIgnore\",\"canFail\"]},\"shardConf\":{\"maxTaskCount\":20}},{\"stepName\":\"aggr-2\",\"traits\":{\"enable\":[\"canIgnore\",\"ARCHIVE\"]},\"ignore\":true}]}";
        try {
            return JsonParseUtil.getObjectMapper().readValue(str, JobRequest.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static JobRequest buildJobRequestPattern() {
        String str = "{\"jobName\":\"sample\",\"trigger\":\"kick\",\"traits\":{\"enable\":[\"canIgnore\",\"deleted\"]},\"params\":{\"p1\":\"abc${k3}&${k2}\",\"p2\":\"${k1}-${k3}\"},\"steps\":[{\"stepName\":\"prep\",\"traits\":{\"disable\":[\"canIgnore\",\"canFail\"]},\"shardConf\":{\"maxTaskCount\":20}},{\"stepName\":\"aggr-2\",\"traits\":{\"enable\":[\"canIgnore\",\"ARCHIVE\"]},\"ignore\":true}]}";
        try {
            return JsonParseUtil.getObjectMapper().readValue(str, JobRequest.class);
        } catch (Exception e) {
            return null;
        }
    }

}
