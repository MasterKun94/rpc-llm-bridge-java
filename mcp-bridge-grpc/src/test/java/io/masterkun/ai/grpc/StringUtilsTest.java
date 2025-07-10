package io.masterkun.ai.grpc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {

    @Test
    public void testToDescStr() {
        // Test null and empty string
        assertNull(StringUtils.toDescStr(null));
        assertEquals("", StringUtils.toDescStr(""));

        // Test CamelCase
        assertEquals("camel Case", StringUtils.toDescStr("camelCase"));
        assertEquals("Camel Case", StringUtils.toDescStr("CamelCase"));
        assertEquals("camel Case Test", StringUtils.toDescStr("camelCaseTest"));

        // Test underscore-separated
        assertEquals("under score", StringUtils.toDescStr("under_score"));
        assertEquals("under score test", StringUtils.toDescStr("under_score_test"));

        // Test hyphen-separated
        assertEquals("hyphen separated", StringUtils.toDescStr("hyphen-separated"));
        assertEquals("hyphen separated test", StringUtils.toDescStr("hyphen-separated-test"));

        // Test mixed formats
        assertEquals("mixed Case with under score and hyphen",
                StringUtils.toDescStr("mixedCase_with_under-score-and-hyphen"));
    }
}
