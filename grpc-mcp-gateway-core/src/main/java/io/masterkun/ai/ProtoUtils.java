package io.masterkun.ai;

import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.masterkun.mcp.proto.McpProto;

import java.util.HashMap;
import java.util.Map;

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
     * Generates a JSON schema representation of a Protocol Buffer message.
     *
     * @param descriptor The Protocol Buffer message descriptor to generate schema for
     * @return A JSON string representing the schema of the message
     */
    public static String getJsonSchema(Descriptors.Descriptor descriptor) {
        if (descriptor == null) {
            throw new NullPointerException("descriptor cannot be null");
        }

        StringBuilder schema = new StringBuilder();
        schema.append("{\n");
        schema.append("  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n");
        schema.append("  \"title\": \"").append(descriptor.getName()).append("\",\n");
        schema.append("  \"type\": \"object\",\n");
        schema.append("  \"properties\": {\n");

        Map<String, String> fieldSchemas = new HashMap<>();
        // Collect required field names
        java.util.List<String> requiredFields = new java.util.ArrayList<>();

        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            String fieldSchema = generateFieldSchema(field, "    ");
            fieldSchemas.put(field.getName(), fieldSchema);

            // Check if field is required
            if (field.getOptions().hasExtension(McpProto.fieldRequired)) {
                boolean fieldRequired = field.getOptions().getExtension(McpProto.fieldRequired);
                if (fieldRequired) {
                    requiredFields.add(field.getName());
                }
            }
        }

        boolean first = true;
        for (Map.Entry<String, String> entry : fieldSchemas.entrySet()) {
            if (!first) {
                schema.append(",\n");
            }
            first = false;
            schema.append("    \"").append(entry.getKey()).append("\": ").append(entry.getValue());
        }

        schema.append("\n  }");

        // Add required fields array if there are any required fields
        if (!requiredFields.isEmpty()) {
            schema.append(",\n  \"required\": [");
            first = true;
            for (String requiredField : requiredFields) {
                if (!first) {
                    schema.append(", ");
                }
                first = false;
                schema.append("\"").append(requiredField).append("\"");
            }
            schema.append("]");
        }

        schema.append("\n}");

        return schema.toString();
    }

    private static String generateFieldSchema(Descriptors.FieldDescriptor field, String indent) {
        StringBuilder fieldSchema = new StringBuilder();
        fieldSchema.append("{\n");

        // Add description if available
        if (field.getOptions().hasExtension(McpProto.fieldDesc)) {
            String description = field.getOptions().getExtension(McpProto.fieldDesc);
            fieldSchema.append(indent).append("  \"description\": \"").append(description).append("\",\n");
        }

        // Handle different field types
        switch (field.getType()) {
            case STRING:
                fieldSchema.append(indent).append("  \"type\": \"string\"");
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
                fieldSchema.append(indent).append("  \"type\": \"integer\"");
                break;
            case FLOAT:
            case DOUBLE:
                fieldSchema.append(indent).append("  \"type\": \"number\"");
                break;
            case BOOL:
                fieldSchema.append(indent).append("  \"type\": \"boolean\"");
                break;
            case ENUM:
                fieldSchema.append(indent).append("  \"type\": \"string\",\n");
                fieldSchema.append(indent).append("  \"enum\": [");

                Descriptors.EnumDescriptor enumDescriptor = field.getEnumType();
                boolean firstEnum = true;
                for (Descriptors.EnumValueDescriptor value : enumDescriptor.getValues()) {
                    if (!firstEnum) {
                        fieldSchema.append(", ");
                    }
                    firstEnum = false;
                    fieldSchema.append("\"").append(value.getName()).append("\"");
                }

                fieldSchema.append("]");
                break;
            case MESSAGE:
                fieldSchema.append(indent).append("  \"type\": \"object\",\n");
                fieldSchema.append(indent).append("  \"properties\": {\n");

                Descriptors.Descriptor messageDescriptor = field.getMessageType();
                Map<String, String> nestedFieldSchemas = new HashMap<>();

                for (Descriptors.FieldDescriptor nestedField : messageDescriptor.getFields()) {
                    String nestedFieldSchema = generateFieldSchema(nestedField, indent + "    ");
                    nestedFieldSchemas.put(nestedField.getName(), nestedFieldSchema);
                }

                boolean firstNested = true;
                for (Map.Entry<String, String> entry : nestedFieldSchemas.entrySet()) {
                    if (!firstNested) {
                        fieldSchema.append(",\n");
                    }
                    firstNested = false;
                    fieldSchema.append(indent).append("    \"").append(entry.getKey()).append(
                            "\": ").append(entry.getValue());
                }

                fieldSchema.append("\n").append(indent).append("  }");
                break;
            default:
                fieldSchema.append(indent).append("  \"type\": \"string\"");
                break;
        }

        // Handle repeated fields
        if (field.isRepeated()) {
            // Save the current schema as the items schema
            String itemsSchema = fieldSchema.toString();
            // Remove the opening and closing braces
            itemsSchema = itemsSchema.substring(itemsSchema.indexOf("{") + 1,
                    itemsSchema.lastIndexOf("}"));

            // Create a new schema for the array
            fieldSchema = new StringBuilder();
            fieldSchema.append("{\n");
            if (field.getOptions().hasExtension(McpProto.fieldDesc)) {
                String description = field.getOptions().getExtension(McpProto.fieldDesc);
                fieldSchema.append(indent).append("  \"description\": \"").append(description).append("\",\n");
            }
            fieldSchema.append(indent).append("  \"type\": \"array\",\n");
            fieldSchema.append(indent).append("  \"items\": {").append(itemsSchema).append("}");
        }

        fieldSchema.append("\n").append(indent).append("}");
        return fieldSchema.toString();
    }
}
