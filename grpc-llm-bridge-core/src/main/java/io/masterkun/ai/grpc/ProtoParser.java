package io.masterkun.ai.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing, loading, and saving Protocol Buffer descriptors. Provides
 * functionality to convert between different Protocol Buffer descriptor formats and handle
 * dependencies between Protocol Buffer files.
 */
public class ProtoParser {

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
