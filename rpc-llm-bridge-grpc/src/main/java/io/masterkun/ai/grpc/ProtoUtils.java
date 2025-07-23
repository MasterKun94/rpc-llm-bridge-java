package io.masterkun.ai.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.masterkun.ai.proto.ToolProto;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for working with Protocol Buffer messages. Provides methods for converting protobuf
 * messages to/from JSON, generating JSON schemas for protobuf descriptors, and formatting protobuf
 * messages as human-readable strings.
 */
public class ProtoUtils {

    /**
     * Converts a protobuf message to its JSON representation.
     *
     * @param message The protobuf message to convert
     * @return A JSON string representation of the message
     * @throws RuntimeException if the conversion fails
     */
    public static String toJson(MessageOrBuilder message) {
        try {
            return JsonFormat.printer().print(message);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses a JSON string into a protobuf message builder. Unknown fields in the JSON are ignored
     * during parsing.
     *
     * @param json    The JSON string to parse
     * @param builder The protobuf message builder to populate
     * @throws RuntimeException if the parsing fails
     */
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
            ObjectMapper objectMapper = JSONUtils.OBJECT_MAPPER;
            ObjectNode schemaNode = objectMapper.createObjectNode();

            // Add schema metadata
            schemaNode.put("$schema", "http://json-schema.org/draft-07/schema#");
            schemaNode.put("title", descriptor.getName());
            addMessageToSchema(descriptor, schemaNode, objectMapper);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON schema", e);
        }
    }

    private static void addMessageToSchema(Descriptors.Descriptor descriptor,
                                           ObjectNode schemaNode,
                                           ObjectMapper objectMapper) {
        schemaNode.put("type", "object");

        // Add properties
        ObjectNode propertiesNode = objectMapper.createObjectNode();
        ArrayNode requiredArray = objectMapper.createArrayNode();

        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            // Skip fields that are part of oneof (they will be handled separately)
            if (field.getContainingOneof() != null) {
                continue;
            }

            addFieldToSchema(field, propertiesNode, requiredArray, objectMapper, false);
        }

        // Handle oneof fields
        ArrayNode allOf = objectMapper.createArrayNode();
        for (Descriptors.OneofDescriptor oneof : descriptor.getOneofs()) {
            ArrayNode oneOf = objectMapper.createArrayNode();
            for (Descriptors.FieldDescriptor field : oneof.getFields()) {
                addFieldToSchema(field, propertiesNode, requiredArray, objectMapper, true);
                ArrayNode required = objectMapper.createArrayNode();
                required.add(field.getName());
                ObjectNode subNode = objectMapper.createObjectNode();
                subNode.set("required", required);
                oneOf.add(subNode);
            }
            ObjectNode anyOfNode = objectMapper.createObjectNode();
            anyOfNode.set("anyOf", oneOf.deepCopy());
            ObjectNode notNode = objectMapper.createObjectNode();
            notNode.set("not", anyOfNode);
            oneOf.add(notNode);
            allOf.add(oneOf);
        }
        if (!allOf.isEmpty()) {
            if (allOf.size() == 1) {
                schemaNode.set("oneOf", allOf.get(0));
            } else {
                schemaNode.set("allOf", allOf);
            }
        }

        schemaNode.set("properties", propertiesNode);

        // Add required fields if any
        if (!requiredArray.isEmpty()) {
            schemaNode.set("required", requiredArray);
        }
    }

    private static void addFieldToSchema(Descriptors.FieldDescriptor field,
                                         ObjectNode propertiesNode,
                                         ArrayNode requiredArray,
                                         ObjectMapper objectMapper,
                                         boolean isOneOf) {
        ObjectNode fieldNode = objectMapper.createObjectNode();

        // Add description if available
        if (field.getOptions().hasExtension(ToolProto.fieldDesc)) {
            String description = field.getOptions().getExtension(ToolProto.fieldDesc);
            fieldNode.put("description", description);
        }

        // Check if field is required
        if (field.getOptions().hasExtension(ToolProto.fieldRequired) &&
            field.getOptions().getExtension(ToolProto.fieldRequired) && !isOneOf) {
            requiredArray.add(field.getName());
        }

        // Handle repeated fields (except for map fields which are handled separately)
        if (field.isRepeated() && !field.isMapField()) {
            fieldNode.put("type", "array");
            ObjectNode itemsNode = objectMapper.createObjectNode();

            if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                // For message types, generate a nested schema
                addMessageToSchema(field.getMessageType(), itemsNode, objectMapper);
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
                fieldNode.put("contentEncoding", "base64");
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
                    addMessageToSchema(field.getMessageType(), fieldNode, objectMapper);
                }
                break;
            default:
                fieldNode.put("type", "string");
        }
    }

    /**
     * Formats a protobuf message as a human-readable string. The format is optimized for
     * readability, with nested messages and repeated fields properly indented and structured.
     *
     * @param message The protobuf message to format
     * @return A formatted string representation of the message
     */
    public static String formatString(MessageOrBuilder message) {
        StringBuilder builder = new StringBuilder();
        List<Integer> list = new ArrayList<>();

        Map<Descriptors.FieldDescriptor, Object> fields = message.getAllFields();
        boolean formatLevel = fields
                .keySet()
                .stream()
                .anyMatch(ProtoUtils::isFieldComposite);
        if (!formatLevel && fields.size() == 1) {
            builder.append(fields.values().iterator().next());
        } else {
            formatTo(message, builder, list, formatLevel);
        }
        return builder.toString();
    }

    /**
     * Determines if a field is composite (message type or repeated).
     *
     * @param field The field descriptor to check
     * @return true if the field is a message type or repeated, false otherwise
     */
    private static boolean isFieldComposite(Descriptors.FieldDescriptor field) {
        return field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE || field.isRepeated();
    }

    private static void formatTo(Object message, StringBuilder builder, List<Integer> level,
                                 boolean formatLevel) {
        if (message == null) {
            builder.append("null");
        } else if (message instanceof CharSequence) {
            builder.append('\'').append(message).append('\'');
        } else if (message instanceof ByteString byteString) {
            builder.append('\'').append(byteString.toStringUtf8()).append('\'');
        } else if (message instanceof MessageOrBuilder proto) {
            level.add(1);
            boolean first = true;
            for (Descriptors.FieldDescriptor field : proto.getDescriptorForType().getFields()) {
                Object value = proto.getField(field);
                if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    if (value.equals(field.getDefaultValue())) {
                        continue;
                    }
                } else if (value instanceof List<?> l) {
                    if (l.isEmpty()) {
                        continue;
                    }
                } else if (value instanceof Map<?, ?> m) {
                    if (m.isEmpty()) {
                        continue;
                    }
                } else if (value instanceof Message m) {
                    if (m.equals(m.getDefaultInstanceForType())) {
                        continue;
                    }
                }
                if (first) {
                    first = false;
                }
                formatLevel(level, builder, formatLevel);
                String name = field.getOptions().hasExtension(ToolProto.fieldDesc) ?
                        field.getOptions().getExtension(ToolProto.fieldDesc) :
                        field.getName();
                builder.append(name).append(": ");
                formatTo(value, builder, level, formatLevel);
                level.set(level.size() - 1, level.get(level.size() - 1) + 1);
            }
            level.remove(level.size() - 1);
        } else if (message instanceof Iterable<?> iterable) {
            boolean first = true;
            level.add(1);
            boolean singleLine = false;
            for (Object o : iterable) {
                if (first) {
                    first = false;
                    singleLine =
                            !((o instanceof MapEntry<?, ?> e && e.getValue() instanceof MessageOrBuilder) ||
                              o instanceof MessageOrBuilder);
                    if (singleLine) {
                        builder.append('[');
                    }
                } else {
                    builder.append(", ");
                }
                if (o instanceof MapEntry<?, ?> e) {
                    if (e.getValue() instanceof MessageOrBuilder m) {
                        formatLevel(level, builder, formatLevel);
                        builder.append(e.getKey()).append(": ");
                        formatTo(m, builder, level, formatLevel);
                    } else {
                        formatTo(e.getValue(), builder.append(e.getKey()).append(": "), level,
                                formatLevel);
                    }
                } else if (o instanceof MessageOrBuilder m) {
                    formatLevel(level, builder, formatLevel);
                    formatTo(m, builder, level, formatLevel);
                } else {
                    formatTo(o, builder, level, formatLevel);
                }
                level.set(level.size() - 1, level.get(level.size() - 1) + 1);
            }
            level.remove(level.size() - 1);
            if (singleLine) {
                builder.append(']');
            }
        } else if (message instanceof Map<?, ?> map) {
            boolean first = true;
            builder.append('{');
            for (Map.Entry<?, ?> o : map.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                formatTo(o.getValue(), builder.append(o.getKey()).append(": "), level, formatLevel);
            }
            builder.append('}');
        } else {
            builder.append(message);
        }
    }

    private static void formatLevel(List<Integer> level, StringBuilder builder,
                                    boolean formatLevel) {
        builder.append("\n");
        builder.append("  ".repeat(level.size() - 1));
        if (formatLevel) {
            builder.append(level.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(".", "", ". ")));
        }
    }

    /**
     * Loads Protocol Buffer descriptors from an input stream.
     *
     * @param inputStream The input stream containing a FileDescriptorSet
     * @return A map of file names to their corresponding FileDescriptors
     * @throws RuntimeException If there is an error parsing the input stream
     */
    public static Map<String, Descriptors.FileDescriptor> load(InputStream inputStream) {
        try {
            return load(DescriptorProtos.FileDescriptorSet.parseFrom(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads Protocol Buffer descriptors from a FileDescriptorSet. Resolves all dependencies between
     * the file descriptors.
     *
     * @param fileDescriptorSet The FileDescriptorSet containing file descriptor protos
     * @return A map of file names to their corresponding FileDescriptors
     */
    public static Map<String, Descriptors.FileDescriptor> load(DescriptorProtos.FileDescriptorSet fileDescriptorSet) {
        List<DescriptorProtos.FileDescriptorProto> fileProtos = fileDescriptorSet.getFileList();
        Map<String, Descriptors.FileDescriptor> map = new HashMap<>();
        for (int i = 0; i < fileProtos.size(); i++) {
            loadFileDescriptor(i, fileProtos, map);
        }
        return map;
    }

    /**
     * Saves a list of FileDescriptors to an output stream. Converts the FileDescriptors to a
     * FileDescriptorSet and writes it to the output stream.
     *
     * @param fileDescriptors The list of FileDescriptors to save
     * @param outputStream    The output stream to write to
     * @throws RuntimeException If there is an error writing to the output stream
     */
    public static void save(List<Descriptors.FileDescriptor> fileDescriptors,
                            OutputStream outputStream) {
        try {
            save(fileDescriptors).writeTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a list of FileDescriptors to a FileDescriptorSet. Includes all dependencies of the
     * provided FileDescriptors.
     *
     * @param fileDescriptors The list of FileDescriptors to convert
     * @return A FileDescriptorSet containing all the FileDescriptorProtos
     */
    public static DescriptorProtos.FileDescriptorSet save(Iterable<Descriptors.FileDescriptor> fileDescriptors) {
        Map<String, DescriptorProtos.FileDescriptorProto> map = new LinkedHashMap<>();
        saveFileDescriptor(fileDescriptors, map);
        return DescriptorProtos.FileDescriptorSet.newBuilder()
                .addAllFile(map.values())
                .build();
    }

    private static void loadFileDescriptor(int index,
                                           List<DescriptorProtos.FileDescriptorProto> fileProtos,
                                           Map<String, Descriptors.FileDescriptor> map) {
        DescriptorProtos.FileDescriptorProto proto = fileProtos.get(index);
        if (map.containsKey(proto.getName())) {
            return;
        }
        List<Descriptors.FileDescriptor> dependencies = new ArrayList<>(proto.getDependencyCount());
        for (String dependency : proto.getDependencyList()) {
            if (map.containsKey(dependency)) {
                dependencies.add(map.get(dependency));
            } else {
                for (int i = 0; i < fileProtos.size(); i++) {
                    if (fileProtos.get(i).getName().equals(dependency)) {
                        loadFileDescriptor(i, fileProtos, map);
                        assert map.containsKey(dependency);
                        dependencies.add(map.get(dependency));
                        break;
                    }
                }
                if (!map.containsKey(dependency)) {
                    throw new IllegalArgumentException("Dependency not found: " + dependency);
                }
            }
        }
        loadFileDescriptor(proto, dependencies, map);
    }

    private static void loadFileDescriptor(DescriptorProtos.FileDescriptorProto proto,
                                           List<Descriptors.FileDescriptor> dependencies,
                                           Map<String, Descriptors.FileDescriptor> map) {
        try {
            Descriptors.FileDescriptor descriptor = Descriptors.FileDescriptor.buildFrom(proto,
                    dependencies.toArray(Descriptors.FileDescriptor[]::new));
            map.put(proto.getName(), descriptor);
        } catch (Descriptors.DescriptorValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveFileDescriptor(Iterable<Descriptors.FileDescriptor> fileDescriptors,
                                           Map<String, DescriptorProtos.FileDescriptorProto> map) {
        for (Descriptors.FileDescriptor fileDescriptor : fileDescriptors) {
            saveFileDescriptor(fileDescriptor.getDependencies(), map);
            if (map.containsKey(fileDescriptor.getName())) {
                continue;
            }
            DescriptorProtos.FileDescriptorProto proto = fileDescriptor.toProto();
            map.put(proto.getName(), proto);
        }
    }
}
