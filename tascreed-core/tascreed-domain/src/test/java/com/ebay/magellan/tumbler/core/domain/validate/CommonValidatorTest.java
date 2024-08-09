package com.ebay.magellan.tumbler.core.domain.validate;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommonValidatorTest {

    @Test
    public void validName() {
        assertTrue(CommonValidator.validName("abc"));
        assertTrue(CommonValidator.validName("ABC"));
        assertTrue(CommonValidator.validName("a123"));
        assertTrue(CommonValidator.validName("job1"));
        assertTrue(CommonValidator.validName("job-1"));
        assertTrue(CommonValidator.validName("job_1"));
        assertTrue(CommonValidator.validName("jSDFo-b_1134ax"));
        assertTrue(CommonValidator.validName("_"));

        assertFalse(CommonValidator.validName(""));
        assertFalse(CommonValidator.validName("?"));
        assertFalse(CommonValidator.validName("*"));
        assertFalse(CommonValidator.validName("&^"));
        assertFalse(CommonValidator.validName("---"));
        assertFalse(CommonValidator.validName("123"));
    }

    @Test
    public void validTrigger() {
        assertTrue(CommonValidator.validTrigger("abc"));
        assertTrue(CommonValidator.validTrigger("ABC"));
        assertTrue(CommonValidator.validTrigger("a123"));
        assertTrue(CommonValidator.validTrigger("job1"));
        assertTrue(CommonValidator.validTrigger("job-1"));
        assertTrue(CommonValidator.validTrigger("job_1"));
        assertTrue(CommonValidator.validTrigger("jSDFo-b_1134ax"));
        assertTrue(CommonValidator.validTrigger("_"));
        assertTrue(CommonValidator.validTrigger("123"));

        assertFalse(CommonValidator.validTrigger(""));
        assertFalse(CommonValidator.validTrigger("?"));
        assertFalse(CommonValidator.validTrigger("*"));
        assertFalse(CommonValidator.validTrigger("&^"));
        assertFalse(CommonValidator.validTrigger("---"));
    }

    List<String> buildList(String... strs) {
        List<String> list = new ArrayList<>();
        for (String s : strs) {
            list.add(s);
        }
        return list;
    }

    @Test
    public void validUniqueNames() {
        assertTrue(CommonValidator.validUniqueNames(
                "test", buildList("s1", "s2", "s3", "s4", "s5")).isValid());
        assertTrue(CommonValidator.validUniqueNames(
                "test", buildList("s1", "s2", "s3", "s4", "s5", "null", null)).isValid());
        assertFalse(CommonValidator.validUniqueNames(
                "test", buildList("s1", "s2", "s3", "s4", "s1")).isValid());
        assertFalse(CommonValidator.validUniqueNames(
                "test", buildList("s1", "s2", "s3", "s4", null, null)).isValid());
    }
}
