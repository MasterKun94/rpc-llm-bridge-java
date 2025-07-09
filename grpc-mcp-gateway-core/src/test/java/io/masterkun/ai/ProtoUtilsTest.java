package io.masterkun.ai;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.masterkun.mcp.proto.ExampleProto;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ProtoUtils} class.
 */
public class ProtoUtilsTest {

    /**
     * Test for {@link ProtoUtils#getJsonSchema(Descriptor)} method.
     * Verifies that the generated JSON schema matches the documentation comments.
     */
    @Test
    public void testGetJsonSchema_SimpleMessage() {

        // Generate JSON schema
        String jsonSchema = ProtoUtils.getJsonSchema(ExampleProto.ToUpperCaseReq.getDescriptor());

        // Parse the JSON schema
        DocumentContext context = JsonPath.parse(jsonSchema);

        // Verify schema structure
        assertEquals("http://json-schema.org/draft-07/schema#", context.read("$['$schema']"));
        assertEquals("ToUpperCaseReq", context.read("$.title"));
        assertEquals("object", context.read("$.type"));

        // Verify properties
        assertTrue(context.read("$.properties") instanceof Object);

        // Verify message field
        assertEquals("string", context.read("$.properties.message.type"));
        assertEquals("请求内容", context.read("$.properties.message.description"));

        // Verify required fields
        assertTrue(context.read("$.required") instanceof List);
        List<String> requiredFields = context.read("$.required");
        assertTrue(requiredFields.contains("message"));
    }

    /**
     * Test for {@link ProtoUtils#getJsonSchema(Descriptor)} method with a message containing nested fields.
     */
    @Test
    public void testGetJsonSchema_ComplexMessage() {

        ExampleProto.ZonedTimeReq req = ExampleProto.ZonedTimeReq.getDefaultInstance();
        String timezone = req.getTimezone();

        // Generate JSON schema
        String jsonSchema = ProtoUtils.getJsonSchema(ExampleProto.ZonedTimeReq.getDescriptor());

        // Parse the JSON schema
        DocumentContext context = JsonPath.parse(jsonSchema);

        // Verify schema structure
        assertEquals("http://json-schema.org/draft-07/schema#", context.read("$['$schema']"));
        assertEquals("ZonedTimeReq", context.read("$.title"));
        assertEquals("object", context.read("$.type"));

        // Verify properties
        assertTrue(context.read("$.properties") instanceof Object);

        // Verify timezone field
        assertEquals("string", context.read("$.properties.timezone.type"));
        assertEquals("时区编码，例如UTC+8", context.read("$.properties.timezone.description"));

        // This field is not required, so there should be no required array or it shouldn't contain this field
        try {
            List<String> requiredFields = context.read("$.required");
            if (requiredFields != null) {
                assertFalse(requiredFields.contains("timezone"));
            }
        } catch (Exception e) {
            // If the path doesn't exist, that's fine - it means there are no required fields
        }
    }

    /**
     * Test for {@link ProtoUtils#getJsonSchema(Descriptor)} method with null input.
     */
    @Test(expected = NullPointerException.class)
    public void testGetJsonSchema_NullMessage() {
        ProtoUtils.getJsonSchema(null);
    }
}
