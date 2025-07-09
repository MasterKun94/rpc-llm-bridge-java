package io.masterkun.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.protobuf.ByteString;
import io.masterkun.mcp.proto.ForTestProto;
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

    @Test
    public void testToJsonConformsToSchema() throws Exception {
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

        // Add repeated fields
        builder.addList("item1");
        builder.addList("item2");

        ForTestProto.Elem1 elem = ForTestProto.Elem1.newBuilder()
            .setA1(456)
            .setA2(false)
            .setA3(ByteString.copyFromUtf8("elem bytes"))
            .build();
        builder.addElems(elem);

        ForTestProto.TestReq testReq = builder.build();

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
        String emptyCollectionsJson = "{\"message\": \"empty collections\", \"list\": [], \"elems\": [], \"map1\": {}}";
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
    }
}
