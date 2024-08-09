package com.ebay.magellan.tumbler.depend.common.util;

import org.junit.Test;

public class HostUtilTest {
    @Test
    public void getDcNameTest() {
        String str = HostUtil.getDcName("1");
        System.out.println(str);
        assert (str.contains("UNKNOWN"));
        str = HostUtil.getDcName("lvs.1");
        assert (str.equals("lvs"));
        str = HostUtil.getDcName("slc.2");
        assert (str.equals("slc"));
        str = HostUtil.getDcName("rno.3");
        assert (str.equals("rno"));
        str = HostUtil.getDcName("1.lvs");
        assert (str.equals("lvs"));
        str = HostUtil.getDcName("2.slc");
        assert (str.equals("slc"));
        str = HostUtil.getDcName("3.rno");
        assert (str.equals("rno"));
    }

    @Test
    public void getHostNameTest(){
        String hostName =  HostUtil.getHostName();
        assert (hostName.length() > 0);
    }

    @Test
    public void getDcName1Test() {
        String Dcname = HostUtil.getDcName();
        assert (Dcname.equals("UNKNOWN"));
    }
}
