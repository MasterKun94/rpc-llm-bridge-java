package io.masterkun.ai.grpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.protobuf.ByteString;
import com.google.protobuf.DynamicMessage;
import io.masterkun.tool.proto.ForTestProto;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ProtoUtils} class.
 */
public class ProtoUtilsTest {

    static ForTestProto.TestReq REQ;

    static {
        // Create a test message
        ForTestProto.TestReq.Builder builder = ForTestProto.TestReq.newBuilder();
        builder.setMessage("Test message");

        // Add an Elem1 to the oneof field
        ForTestProto.Elem1.Builder elem1Builder = ForTestProto.Elem1.newBuilder();
        elem1Builder.setA1(123);
        elem1Builder.setA2(true);
        elem1Builder.setA3(ByteString.copyFromUtf8("test bytes"));
        builder.setElem1(elem1Builder.build());

        // Add map entries
        builder.putMap1("key1", 100);
        builder.putMap1("key2", 200);

        builder.putMap2(100, ByteString.copyFromUtf8("map2 value1"));
        builder.putMap2(200, ByteString.copyFromUtf8("map2 value2"));

        builder.putMap3("key", ForTestProto.Elem3.newBuilder()
                .setC2(123.567f)
                .addC6(789.5)
                .addC6(987.5)
                .addAllC5(List.of(
                        ByteString.copyFromUtf8("c5 value1"),
                        ByteString.copyFromUtf8("c5 value2")
                ))
                .setC7(ForTestProto.TestEnum.BBB)
                .build());

        // Add repeated fields
        builder.addList("item1");
        builder.addList("item2");

        ForTestProto.Elem1 elem = ForTestProto.Elem1.newBuilder()
                .setA1(456)
                .setA2(false)
                .setA3(ByteString.copyFromUtf8("elem bytes"))
                .build();
        builder.addElems(elem);

        REQ = builder.build();
    }

    @Test
    public void testDynamicMessageSerialize() throws Exception {
        String json = ProtoUtils.toJson(REQ);
        DynamicMessage.Builder builder1 =
                DynamicMessage.newBuilder(ForTestProto.TestReq.getDescriptor());
        ForTestProto.TestReq.Builder builder2 = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(json, builder1);
        ProtoUtils.fromJson(json, builder2);
        var req1 = ForTestProto.TestReq.parseFrom(builder1.build().toByteArray());
        var req2 = builder2.build();
        String json1 = ProtoUtils.toJson(req1);
        String json2 = ProtoUtils.toJson(req2);
        assertEquals(req1, req2);

        DynamicMessage.Builder builder3 =
                DynamicMessage.newBuilder(ForTestProto.TestReq.getDescriptor());
        ForTestProto.TestReq.Builder builder4 = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(json1, builder4);
        ProtoUtils.fromJson(json2, builder3);
        var req3 = ForTestProto.TestReq.parseFrom(builder3.build().toByteArray());
        var req4 = builder4.build();
        assertEquals(req3, req4);
        assertEquals(req1, req3);
        assertEquals(req1, REQ);
    }

    @Test
    public void testFormatString() {
        System.out.println(ProtoUtils.formatString(REQ));
    }


    @Test
    public void testFormatStringSimple() {
        System.out.println(ProtoUtils.formatString(ForTestProto.Elem1.newBuilder()
                .setA1(1)
                .setA2(true)
                .setA3(ByteString.copyFromUtf8("test"))));
    }

    @Test
    public void testFormatSingleField() {
        System.out.println(ProtoUtils.formatString(ForTestProto.SingleField.newBuilder()
                .setField("TEST MESSAGE")
                .build()));
    }

    @Test
    public void testToJsonConformsToSchema() throws Exception {
        ForTestProto.TestReq testReq = REQ;

        // Get the JSON schema
        String jsonSchema = ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor());

        // Convert the message to JSON
        String json = ProtoUtils.toJson(testReq);

        System.out.println("Generated JSON:");
        System.out.println(json);

        // Create a modified schema that handles map fields correctly
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(jsonSchema);

        // Convert the modified schema back to a string
        String modifiedJsonSchema = mapper.writeValueAsString(schemaNode);

        // Load the schema and JSON
        JsonNode jsonNode = JsonLoader.fromString(json);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(JsonLoader.fromString(modifiedJsonSchema));

        // Validate the JSON against the schema
        ProcessingReport report = schema.validate(jsonNode);

        // Print the validation report
        System.out.println("Validation Report:");
        System.out.println(report);

        // Assert that the validation was successful
        assertTrue("JSON should conform to schema", report.isSuccess());
    }

    @Test
    public void testFromJson() throws Exception {
        // Create a JSON string
        // Note: bytes fields must be base64-encoded in JSON
        String json = """
                {
                  "message": "Test message from JSON",
                  "elem1": {
                    "a1": 456,
                    "a2": true,
                    "a3": "anNvbiBieXRlcw=="
                  },
                  "map1": {
                    "jsonKey1": 300,
                    "jsonKey2": 400
                  },
                  "map2": {
                    "1": "anNvbiBieXRlcw==",
                    "2": "ZWxlbSBqc29uIGJ5dGVz"
                  },
                  "list": ["json1", "json2", "json3"],
                  "elems": [
                    {
                      "a1": 789,
                      "a2": false,
                      "a3": "ZWxlbSBqc29uIGJ5dGVz"
                    }
                  ]
                }""";

        // Parse the JSON into a message
        ForTestProto.TestReq.Builder builder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(json, builder);
        ForTestProto.TestReq testReq = builder.build();

        // Verify the fields were set correctly
        assertEquals("Test message from JSON", testReq.getMessage());

        // Verify oneof field
        assertTrue(testReq.hasElem1());
        assertEquals(456, testReq.getElem1().getA1());
        assertTrue(testReq.getElem1().getA2());
        assertEquals("json bytes", testReq.getElem1().getA3().toStringUtf8());

        // Verify map field
        assertEquals(2, testReq.getMap1Count());
        assertEquals(300, testReq.getMap1OrThrow("jsonKey1"));
        assertEquals(400, testReq.getMap1OrThrow("jsonKey2"));
        assertEquals(2, testReq.getMap2Count());
        assertEquals("json bytes", testReq.getMap2OrThrow(1).toStringUtf8());
        assertEquals("elem json bytes", testReq.getMap2OrThrow(2).toStringUtf8());
        // Verify repeated field
        assertEquals(3, testReq.getListCount());
        List<String> expectedList = Arrays.asList("json1", "json2", "json3");
        assertEquals(expectedList, testReq.getListList());

        // Verify repeated message field
        assertEquals(1, testReq.getElemsCount());
        assertEquals(789, testReq.getElems(0).getA1());
        assertFalse(testReq.getElems(0).getA2());
        assertEquals("elem json bytes", testReq.getElems(0).getA3().toStringUtf8());
    }

    @Test
    public void testRoundTripConversion() throws Exception {
        // Create an original message
        ForTestProto.TestReq.Builder originalBuilder = ForTestProto.TestReq.newBuilder();
        originalBuilder.setMessage("Round trip message");

        // Set oneof field with Elem2 this time (different from other tests)
        ForTestProto.Elem2.Builder elem2Builder = ForTestProto.Elem2.newBuilder();
        elem2Builder.setB1(789);
        elem2Builder.setB2(true);
        elem2Builder.setB3(ByteString.copyFromUtf8("round trip bytes"));
        originalBuilder.setElem2(elem2Builder.build());

        // Add map entries
        originalBuilder.putMap1("rt1", 500);
        originalBuilder.putMap1("rt2", 600);
        originalBuilder.putMap2(123, ByteString.copyFromUtf8("map2 value"));

        // Add repeated fields
        originalBuilder.addList("rt_item1");
        originalBuilder.addList("rt_item2");

        ForTestProto.TestReq originalMessage = originalBuilder.build();

        // Convert to JSON
        String json = ProtoUtils.toJson(originalMessage);
        System.out.println("Round-trip JSON:");
        System.out.println(json);

        // Parse back to a message
        ForTestProto.TestReq.Builder parsedBuilder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(json, parsedBuilder);
        ForTestProto.TestReq parsedMessage = parsedBuilder.build();

        // Verify the parsed message matches the original
        assertEquals(originalMessage.getMessage(), parsedMessage.getMessage());

        // Verify oneof field
        assertTrue(parsedMessage.hasElem2());
        assertEquals(originalMessage.getElem2().getB1(), parsedMessage.getElem2().getB1());
        assertEquals(originalMessage.getElem2().getB2(), parsedMessage.getElem2().getB2());
        assertEquals(originalMessage.getElem2().getB3(), parsedMessage.getElem2().getB3());

        // Verify map fields
        assertEquals(originalMessage.getMap1Count(), parsedMessage.getMap1Count());
        assertEquals(originalMessage.getMap1OrThrow("rt1"), parsedMessage.getMap1OrThrow("rt1"));
        assertEquals(originalMessage.getMap1OrThrow("rt2"), parsedMessage.getMap1OrThrow("rt2"));

        assertEquals(originalMessage.getMap2Count(), parsedMessage.getMap2Count());
        assertEquals(originalMessage.getMap2OrThrow(123), parsedMessage.getMap2OrThrow(123));

        // Verify repeated fields
        assertEquals(originalMessage.getListCount(), parsedMessage.getListCount());
        assertEquals(originalMessage.getListList(), parsedMessage.getListList());

        // Convert back to JSON and verify it matches the original JSON
        String secondJson = ProtoUtils.toJson(parsedMessage);
        assertEquals(json, secondJson);
    }

    @Test
    public void testEdgeCases() throws Exception {
        // Test with empty message
        ForTestProto.TestReq.Builder emptyBuilder = ForTestProto.TestReq.newBuilder();
        // We need to set the required field to make it valid
        emptyBuilder.setMessage("");
        ForTestProto.TestReq emptyMessage = emptyBuilder.build();

        String emptyJson = ProtoUtils.toJson(emptyMessage);
        System.out.println("Empty message JSON:");
        System.out.println(emptyJson);

        ForTestProto.TestReq.Builder parsedEmptyBuilder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(emptyJson, parsedEmptyBuilder);
        ForTestProto.TestReq parsedEmptyMessage = parsedEmptyBuilder.build();

        assertEquals(emptyMessage, parsedEmptyMessage);

        // Test with minimal JSON
        String minimalJson = "{\"message\": \"minimal\"}";
        ForTestProto.TestReq.Builder minimalBuilder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(minimalJson, minimalBuilder);
        ForTestProto.TestReq minimalMessage = minimalBuilder.build();

        assertEquals("minimal", minimalMessage.getMessage());
        assertFalse(minimalMessage.hasElem1());
        assertFalse(minimalMessage.hasElem2());
        assertEquals(0, minimalMessage.getMap1Count());
        assertEquals(0, minimalMessage.getListCount());
        assertEquals(0, minimalMessage.getElemsCount());

        // Test with empty arrays and maps
        String emptyCollectionsJson = "{\"message\": \"empty collections\", \"list\": [], " +
                                      "\"elems\": [], \"map1\": {}}";
        ForTestProto.TestReq.Builder emptyCollectionsBuilder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(emptyCollectionsJson, emptyCollectionsBuilder);
        ForTestProto.TestReq emptyCollectionsMessage = emptyCollectionsBuilder.build();

        assertEquals("empty collections", emptyCollectionsMessage.getMessage());
        assertEquals(0, emptyCollectionsMessage.getListCount());
        assertEquals(0, emptyCollectionsMessage.getElemsCount());
        assertEquals(0, emptyCollectionsMessage.getMap1Count());
    }

    @Test
    public void testWithTestResMessage() throws Exception {
        // Test with TestRes message type
        ForTestProto.TestRes.Builder resBuilder = ForTestProto.TestRes.newBuilder();
        resBuilder.setMessage("Test response");

        // Set oneof field
        ForTestProto.Elem2.Builder elem2Builder = ForTestProto.Elem2.newBuilder();
        elem2Builder.setB1(123);
        elem2Builder.setB2(false);
        elem2Builder.setB3(ByteString.copyFromUtf8("response bytes"));
        resBuilder.setElem2(elem2Builder.build());

        ForTestProto.TestRes testRes = resBuilder.build();

        // Get JSON schema for TestRes
        String jsonSchema = ProtoUtils.getJsonSchema(ForTestProto.TestRes.getDescriptor());
        System.out.println("TestRes JSON Schema:");
        System.out.println(jsonSchema);

        // Convert to JSON
        String json = ProtoUtils.toJson(testRes);
        System.out.println("TestRes JSON:");
        System.out.println(json);

        // Parse the JSON schema
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(jsonSchema);

        // Verify schema metadata
        assertEquals("http://json-schema.org/draft-07/schema#", schemaNode.get("$schema").asText());
        assertEquals("TestRes", schemaNode.get("title").asText());
        assertEquals("object", schemaNode.get("type").asText());

        // Verify properties
        assertTrue(schemaNode.has("properties"));
        JsonNode propertiesNode = schemaNode.get("properties");

        // Verify message field
        assertTrue(propertiesNode.has("message"));
        assertEquals("string", propertiesNode.get("message").get("type").asText());
        assertEquals("返回结果", propertiesNode.get("message").get("description").asText());

        // Verify oneof fields
        assertTrue(propertiesNode.has("elem1"));
        assertEquals("object", propertiesNode.get("elem1").get("type").asText());
        assertEquals("返回元素1", propertiesNode.get("elem1").get("description").asText());

        assertTrue(propertiesNode.has("elem2"));
        assertEquals("object", propertiesNode.get("elem2").get("type").asText());
        assertEquals("返回元素2", propertiesNode.get("elem2").get("description").asText());

        // Parse back to a message
        ForTestProto.TestRes.Builder parsedBuilder = ForTestProto.TestRes.newBuilder();
        ProtoUtils.fromJson(json, parsedBuilder);
        ForTestProto.TestRes parsedMessage = parsedBuilder.build();

        // Verify the parsed message matches the original
        assertEquals(testRes.getMessage(), parsedMessage.getMessage());
        assertTrue(parsedMessage.hasElem2());
        assertEquals(testRes.getElem2().getB1(), parsedMessage.getElem2().getB1());
        assertEquals(testRes.getElem2().getB2(), parsedMessage.getElem2().getB2());
        assertEquals(testRes.getElem2().getB3(), parsedMessage.getElem2().getB3());

        // Validate JSON against schema
        JsonNode jsonNode = JsonLoader.fromString(json);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(JsonLoader.fromString(jsonSchema));
        ProcessingReport report = schema.validate(jsonNode);
        assertTrue("JSON should conform to schema", report.isSuccess());
    }

    /**
     * Test that TestReq message with oneof fields conforms to its JSON schema
     */
    @Test
    public void testOneofFieldsJsonConformsToSchema() throws Exception {
        // Test with elem1 in oneof
        ForTestProto.TestReq.Builder builder1 = ForTestProto.TestReq.newBuilder();
        builder1.setMessage("Oneof test with elem1");
        builder1.addList("required list item"); // Required field

        ForTestProto.Elem1.Builder elem1Builder = ForTestProto.Elem1.newBuilder();
        elem1Builder.setA1(123);
        elem1Builder.setA2(true);
        elem1Builder.setA3(ByteString.copyFromUtf8("elem1 bytes"));
        builder1.setElem1(elem1Builder.build());

        ForTestProto.TestReq testReqWithElem1 = builder1.build();

        // Get JSON schema
        String jsonSchema = ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor());

        // Convert to JSON
        String json1 = ProtoUtils.toJson(testReqWithElem1);
        System.out.println("TestReq with elem1 JSON:");
        System.out.println(json1);

        // Validate JSON against schema
        JsonNode jsonNode1 = JsonLoader.fromString(json1);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(JsonLoader.fromString(jsonSchema));
        ProcessingReport report1 = schema.validate(jsonNode1);
        assertTrue("JSON with elem1 should conform to schema", report1.isSuccess());

        // Test with elem2 in oneof
        ForTestProto.TestReq.Builder builder2 = ForTestProto.TestReq.newBuilder();
        builder2.setMessage("Oneof test with elem2");
        builder2.addList("required list item"); // Required field

        ForTestProto.Elem2.Builder elem2Builder = ForTestProto.Elem2.newBuilder();
        elem2Builder.setB1(456);
        elem2Builder.setB2(false);
        elem2Builder.setB3(ByteString.copyFromUtf8("elem2 bytes"));
        builder2.setElem2(elem2Builder.build());

        ForTestProto.TestReq testReqWithElem2 = builder2.build();

        // Convert to JSON
        String json2 = ProtoUtils.toJson(testReqWithElem2);
        System.out.println("TestReq with elem2 JSON:");
        System.out.println(json2);

        // Validate JSON against schema
        JsonNode jsonNode2 = JsonLoader.fromString(json2);
        ProcessingReport report2 = schema.validate(jsonNode2);
        assertTrue("JSON with elem2 should conform to schema", report2.isSuccess());

        // Test with no oneof field set (should fail validation due to required oneof)
        ForTestProto.TestReq.Builder builder3 = ForTestProto.TestReq.newBuilder();
        builder3.setMessage("Oneof test with no elem");
        builder3.addList("required list item"); // Required field

        ForTestProto.TestReq testReqWithNoElem = builder3.build();

        // Convert to JSON
        String json3 = ProtoUtils.toJson(testReqWithNoElem);
        System.out.println("TestReq with no elem JSON:");
        System.out.println(json3);

        // Validate JSON against schema - should pass
        JsonNode jsonNode3 = JsonLoader.fromString(json3);
        ProcessingReport report3 = schema.validate(jsonNode3);
        assertTrue("JSON with no elem should pass", report3.isSuccess());
    }

    /**
     * Test that TestReq message with map fields conforms to its JSON schema
     */
    @Test
    public void testMapFieldsJsonConformsToSchema() throws Exception {
        // Create TestReq with map fields
        ForTestProto.TestReq.Builder builder = ForTestProto.TestReq.newBuilder();
        builder.setMessage("Map fields test");
        builder.addList("required list item"); // Required field

        // Set oneof field (required)
        ForTestProto.Elem1.Builder elem1Builder = ForTestProto.Elem1.newBuilder();
        elem1Builder.setA1(123);
        elem1Builder.setA2(true);
        elem1Builder.setA3(ByteString.copyFromUtf8("elem1 bytes"));
        builder.setElem1(elem1Builder.build());

        // Add string->int32 map entries
        builder.putMap1("key1", 100);
        builder.putMap1("key2", 200);
        builder.putMap1("key3", 300);

        // Add int32->bytes map entries
        builder.putMap2(1, ByteString.copyFromUtf8("value1"));
        builder.putMap2(2, ByteString.copyFromUtf8("value2"));

        ForTestProto.TestReq testReq = builder.build();

        // Get JSON schema
        String jsonSchema = ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor());

        // Convert to JSON
        String json = ProtoUtils.toJson(testReq);
        System.out.println("TestReq with maps JSON:");
        System.out.println(json);

        // Parse the JSON schema
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(jsonSchema);

        // Verify map1 field in schema
        JsonNode propertiesNode = schemaNode.get("properties");
        assertTrue(propertiesNode.has("map1"));
        assertEquals("object", propertiesNode.get("map1").get("type").asText());
        assertTrue(propertiesNode.get("map1").has("additionalProperties"));
        assertEquals("integer",
                propertiesNode.get("map1").get("additionalProperties").get("type").asText());

        // Verify map2 field in schema
        assertTrue(propertiesNode.has("map2"));
        assertEquals("object", propertiesNode.get("map2").get("type").asText());
        assertTrue(propertiesNode.get("map2").has("additionalProperties"));
        assertEquals("string",
                propertiesNode.get("map2").get("additionalProperties").get("type").asText());
        assertEquals("base64", propertiesNode.get("map2").get("additionalProperties").get(
                "contentEncoding").asText());

        // Validate JSON against schema
        JsonNode jsonNode = JsonLoader.fromString(json);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(JsonLoader.fromString(jsonSchema));
        ProcessingReport report = schema.validate(jsonNode);
        assertTrue("JSON with map fields should conform to schema", report.isSuccess());

        // Parse back to a message and verify map fields
        ForTestProto.TestReq.Builder parsedBuilder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(json, parsedBuilder);
        ForTestProto.TestReq parsedMessage = parsedBuilder.build();

        assertEquals(3, parsedMessage.getMap1Count());
        assertEquals(100, parsedMessage.getMap1OrThrow("key1"));
        assertEquals(200, parsedMessage.getMap1OrThrow("key2"));
        assertEquals(300, parsedMessage.getMap1OrThrow("key3"));

        assertEquals(2, parsedMessage.getMap2Count());
        assertEquals("value1", parsedMessage.getMap2OrThrow(1).toStringUtf8());
        assertEquals("value2", parsedMessage.getMap2OrThrow(2).toStringUtf8());
    }

    /**
     * Test that TestReq message with nested message fields conforms to its JSON schema
     */
    @Test
    public void testNestedMessageJsonConformsToSchema() throws Exception {
        // Create TestReq with nested messages
        ForTestProto.TestReq.Builder builder = ForTestProto.TestReq.newBuilder();
        builder.setMessage("Nested message test");
        builder.addList("required list item"); // Required field

        // Set oneof field with Elem1 (nested message)
        ForTestProto.Elem1.Builder elem1Builder = ForTestProto.Elem1.newBuilder();
        elem1Builder.setA1(123);
        elem1Builder.setA2(true);
        elem1Builder.setA3(ByteString.copyFromUtf8("elem1 bytes"));
        builder.setElem1(elem1Builder.build());

        // Add repeated Elem1 (nested messages)
        ForTestProto.Elem1.Builder elem1Builder2 = ForTestProto.Elem1.newBuilder();
        elem1Builder2.setA1(456);
        elem1Builder2.setA2(false);
        elem1Builder2.setA3(ByteString.copyFromUtf8("elem1-2 bytes"));
        builder.addElems(elem1Builder2.build());

        ForTestProto.Elem1.Builder elem1Builder3 = ForTestProto.Elem1.newBuilder();
        elem1Builder3.setA1(789);
        elem1Builder3.setA2(true);
        elem1Builder3.setA3(ByteString.copyFromUtf8("elem1-3 bytes"));
        builder.addElems(elem1Builder3.build());

        ForTestProto.TestReq testReq = builder.build();

        // Get JSON schema
        String jsonSchema = ProtoUtils.getJsonSchema(ForTestProto.TestReq.getDescriptor());

        // Convert to JSON
        String json = ProtoUtils.toJson(testReq);
        System.out.println("TestReq with nested messages JSON:");
        System.out.println(json);

        // Parse the JSON schema
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(jsonSchema);

        // Verify elem1 field in schema (nested message)
        JsonNode propertiesNode = schemaNode.get("properties");
        assertTrue(propertiesNode.has("elem1"));
        assertEquals("object", propertiesNode.get("elem1").get("type").asText());
        assertTrue(propertiesNode.get("elem1").has("properties"));

        // Verify elems field in schema (repeated nested message)
        assertTrue(propertiesNode.has("elems"));
        assertEquals("array", propertiesNode.get("elems").get("type").asText());
        assertEquals("object", propertiesNode.get("elems").get("items").get("type").asText());
        assertTrue(propertiesNode.get("elems").get("items").has("properties"));

        // Validate JSON against schema
        JsonNode jsonNode = JsonLoader.fromString(json);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(JsonLoader.fromString(jsonSchema));
        ProcessingReport report = schema.validate(jsonNode);
        assertTrue("JSON with nested messages should conform to schema", report.isSuccess());

        // Parse back to a message and verify nested message fields
        ForTestProto.TestReq.Builder parsedBuilder = ForTestProto.TestReq.newBuilder();
        ProtoUtils.fromJson(json, parsedBuilder);
        ForTestProto.TestReq parsedMessage = parsedBuilder.build();

        // Verify oneof field (elem1)
        assertTrue(parsedMessage.hasElem1());
        assertEquals(123, parsedMessage.getElem1().getA1());
        assertTrue(parsedMessage.getElem1().getA2());
        assertEquals("elem1 bytes", parsedMessage.getElem1().getA3().toStringUtf8());

        // Verify repeated message field (elems)
        assertEquals(2, parsedMessage.getElemsCount());

        assertEquals(456, parsedMessage.getElems(0).getA1());
        assertFalse(parsedMessage.getElems(0).getA2());
        assertEquals("elem1-2 bytes", parsedMessage.getElems(0).getA3().toStringUtf8());

        assertEquals(789, parsedMessage.getElems(1).getA1());
        assertTrue(parsedMessage.getElems(1).getA2());
        assertEquals("elem1-3 bytes", parsedMessage.getElems(1).getA3().toStringUtf8());
    }
}
