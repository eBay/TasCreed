package com.ebay.magellan.tumbler.core.domain.schedule;

import com.ebay.magellan.tumbler.core.domain.help.TestRepo;
import com.ebay.magellan.tumbler.core.domain.schedule.conf.ScheduleConf;
import com.ebay.magellan.tumbler.core.domain.schedule.conf.ScheduleCronConf;
import com.ebay.magellan.tumbler.core.domain.schedule.conf.SchedulePeriodConf;
import com.ebay.magellan.tumbler.core.domain.schedule.var.ConstVar;
import com.ebay.magellan.tumbler.core.domain.schedule.var.CountVar;
import com.ebay.magellan.tumbler.core.domain.schedule.var.TimeVar;
import com.ebay.magellan.tumbler.core.domain.schedule.var.Var;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ScheduleTest {

    Map<String, Var> genVars() {
        Map<String, Var> vars = new HashMap<>();

        ConstVar v1 = new ConstVar();
        v1.setValue("t1");
        vars.put("k1", v1);

        TimeVar v2 = new TimeVar();
        v2.setPattern("yyyy-MM-dd-HH-mm");
        v2.setZone("CTT");
        v2.setDeltaMs(24 * 60 * 60 * 1000L);
        vars.put("k2", v2);

        CountVar v3 = new CountVar();
        v3.setNext(4L);
        vars.put("k3", v3);

        return vars;
    }

    ScheduleConf genTimeConf() {
        SchedulePeriodConf conf = new SchedulePeriodConf();
        conf.setIntervalMs(2 * 60 * 1000L);
        return conf;
    }
    ScheduleConf genCronConf() {
        ScheduleCronConf conf = new ScheduleCronConf();
        conf.setCron("0 0/2 * * * ?");
        return conf;
    }

    @Test
    public void jsonParse() throws Exception {
        Schedule s = new Schedule();
        s.setScheduleName("test");
        s.setJobRequest(TestRepo.jobRequestPattern);
        s.setConf(genCronConf());
        s.setVariables(genVars());

        String str = s.toJson();
        System.out.println(str);

        Schedule s1 = Schedule.fromJson(str);
        Trigger trigger = s1.trigger(new Date());
        System.out.println(trigger);

        assertTrue(s1.validate() == null);
        System.out.println(System.currentTimeMillis());
        System.out.println(s.getConf().nextTriggerTimestamps(
                System.currentTimeMillis(), 5 * 60 * 1000L));
    }

}
