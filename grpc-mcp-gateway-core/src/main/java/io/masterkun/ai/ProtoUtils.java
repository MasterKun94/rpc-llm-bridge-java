package io.masterkun.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.masterkun.mcp.proto.McpProto;

public class ProtoUtils {

    public static String toJson(MessageOrBuilder message) {
        try {
            return JsonFormat.printer().print(message);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fromJson(String json, Message.Builder builder) {
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a JSON schema representation for the given protobuf descriptor.
     *
     * @param descriptor the protobuf descriptor for which the JSON schema is to be generated
     * @return a string containing the generated JSON schema
     * @throws RuntimeException if any issue occurs during schema generation
     */
    public static String getJsonSchema(Descriptors.Descriptor descriptor) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode schemaNode = objectMapper.createObjectNode();

            // Add schema metadata
            schemaNode.put("$schema", "http://json-schema.org/draft-07/schema#");
            schemaNode.put("title", descriptor.getName());
            schemaNode.put("type", "object");

            // Add properties
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            ArrayNode requiredArray = objectMapper.createArrayNode();

            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                // Skip fields that are part of oneof (they will be handled separately)
                if (field.getContainingOneof() != null) {
                    continue;
                }

                addFieldToSchema(field, propertiesNode, requiredArray, objectMapper);
            }

            // Handle oneof fields
            for (Descriptors.OneofDescriptor oneof : descriptor.getOneofs()) {
                for (Descriptors.FieldDescriptor field : oneof.getFields()) {
                    addFieldToSchema(field, propertiesNode, requiredArray, objectMapper);
                }
            }

            schemaNode.set("properties", propertiesNode);

            // Add required fields if any
            if (!requiredArray.isEmpty()) {
                schemaNode.set("required", requiredArray);
            }

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON schema", e);
        }
    }

    private static void addFieldToSchema(Descriptors.FieldDescriptor field,
                                         ObjectNode propertiesNode,
                                         ArrayNode requiredArray, ObjectMapper objectMapper) {
        ObjectNode fieldNode = objectMapper.createObjectNode();

        // Add description if available
        if (field.getOptions().hasExtension(McpProto.fieldDesc)) {
            String description = field.getOptions().getExtension(McpProto.fieldDesc);
            fieldNode.put("description", description);
        }

        // Check if field is required
        if (field.getOptions().hasExtension(McpProto.fieldRequired) &&
            field.getOptions().getExtension(McpProto.fieldRequired)) {
            requiredArray.add(field.getName());
        }

        // Handle repeated fields (except for map fields which are handled separately)
        if (field.isRepeated() && !field.isMapField()) {
            fieldNode.put("type", "array");
            ObjectNode itemsNode = objectMapper.createObjectNode();

            if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                // For message types, generate a nested schema
                itemsNode.put("type", "object");
                ObjectNode nestedPropertiesNode = objectMapper.createObjectNode();
                ArrayNode nestedRequiredArray = objectMapper.createArrayNode();

                for (Descriptors.FieldDescriptor nestedField : field.getMessageType().getFields()) {
                    addFieldToSchema(nestedField, nestedPropertiesNode, nestedRequiredArray,
                            objectMapper);
                }

                itemsNode.set("properties", nestedPropertiesNode);
                if (!nestedRequiredArray.isEmpty()) {
                    itemsNode.set("required", nestedRequiredArray);
                }
            } else {
                // For primitive types, set the type of the items
                updateFieldType(field, itemsNode, objectMapper);
            }
            fieldNode.set("items", itemsNode);
        } else {
            // Handle non-repeated fields
            updateFieldType(field, fieldNode, objectMapper);
        }

        propertiesNode.set(field.getName(), fieldNode);
    }

    private static void updateFieldType(Descriptors.FieldDescriptor field, ObjectNode fieldNode,
                                        ObjectMapper objectMapper) {
        switch (field.getType()) {
            case STRING:
                fieldNode.put("type", "string");
                break;
            case BOOL:
                fieldNode.put("type", "boolean");
                break;
            case INT32:
            case INT64:
            case UINT32:
            case UINT64:
            case SINT32:
            case SINT64:
            case FIXED32:
            case FIXED64:
            case SFIXED32:
            case SFIXED64:
                fieldNode.put("type", "integer");
                break;
            case FLOAT:
            case DOUBLE:
                fieldNode.put("type", "number");
                break;
            case BYTES:
                fieldNode.put("type", "string");
                fieldNode.put("format", "byte");
                if (fieldNode.has("description")) {
                    fieldNode.put("description", fieldNode.get("description").asText() + " " +
                                                 "(Base64 encoded binary data)");
                } else {
                    fieldNode.put("description", "Base64 encoded binary data");
                }
                break;
            case ENUM:
                fieldNode.put("type", "string");
                ArrayNode enumValues = objectMapper.createArrayNode();
                for (Descriptors.EnumValueDescriptor value : field.getEnumType().getValues()) {
                    enumValues.add(value.getName());
                }
                fieldNode.set("enum", enumValues);
                break;
            case MESSAGE:
                if (field.isMapField()) {
                    // Handle map fields
                    fieldNode.put("type", "object");

                    // Value property
                    ObjectNode valueNode = objectMapper.createObjectNode();
                    Descriptors.FieldDescriptor valueField =
                            field.getMessageType().findFieldByName("value");
                    updateFieldType(valueField, valueNode, objectMapper);
                    fieldNode.set("additionalProperties", valueNode);
                    fieldNode.set("properties", objectMapper.createObjectNode());
                } else {
                    // Handle nested message fields
                    fieldNode.put("type", "object");
                    ObjectNode nestedPropertiesNode = objectMapper.createObjectNode();
                    ArrayNode nestedRequiredArray = objectMapper.createArrayNode();

                    for (Descriptors.FieldDescriptor nestedField :
                            field.getMessageType().getFields()) {
                        // Skip fields that are part of oneof (they will be handled separately)
                        if (nestedField.getContainingOneof() != null) {
                            continue;
                        }

                        addFieldToSchema(nestedField, nestedPropertiesNode,
                                nestedRequiredArray, objectMapper);
                    }

                    // Handle oneof fields in nested messages
                    for (Descriptors.OneofDescriptor oneof :
                            field.getMessageType().getOneofs()) {
                        for (Descriptors.FieldDescriptor nestedField : oneof.getFields()) {
                            addFieldToSchema(nestedField, nestedPropertiesNode,
                                    nestedRequiredArray, objectMapper);
                        }
                    }

                    fieldNode.set("properties", nestedPropertiesNode);
                    if (!nestedRequiredArray.isEmpty()) {
                        fieldNode.set("required", nestedRequiredArray);
                    }
                }
                break;
            default:
                fieldNode.put("type", "string");
        }
    }
}
