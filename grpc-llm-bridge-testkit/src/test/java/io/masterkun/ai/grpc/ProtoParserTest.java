package io.masterkun.ai.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import io.masterkun.tool.proto.ForTestProto;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ProtoParser} class.
 * Tests verify that the methods work as described in their documentation.
 */
public class ProtoParserTest {

    /**
     * Test for {@link ProtoParser#load(java.io.InputStream)}.
     * Verifies that the method correctly loads Protocol Buffer descriptors from an input stream.
     */
    @Test
    public void testLoadFromInputStream() throws IOException {
        // Create a FileDescriptorSet
        Descriptors.FileDescriptor fileDescriptor = ForTestProto.getDescriptor();
        List<Descriptors.FileDescriptor> fileDescriptors = new ArrayList<>();
        fileDescriptors.add(fileDescriptor);

        DescriptorProtos.FileDescriptorSet fileDescriptorSet = ProtoParser.save(fileDescriptors);

        // Write to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileDescriptorSet.writeTo(outputStream);

        // Read from the byte array
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Load the descriptors
        Map<String, Descriptors.FileDescriptor> loadedDescriptors = ProtoParser.load(inputStream);

        // Verify the result
        assertNotNull("Loaded descriptors should not be null", loadedDescriptors);
        assertFalse("Loaded descriptors should not be empty", loadedDescriptors.isEmpty());
        assertTrue("Loaded descriptors should contain the file descriptor",
                loadedDescriptors.containsKey(fileDescriptor.getName()));
        assertEquals("Loaded descriptor should match the original",
                fileDescriptor.getName(), loadedDescriptors.get(fileDescriptor.getName()).getName());
    }

    /**
     * Test for {@link ProtoParser#load(com.google.protobuf.DescriptorProtos.FileDescriptorSet)}.
     * Verifies that the method correctly loads Protocol Buffer descriptors from a FileDescriptorSet
     * and resolves all dependencies between the file descriptors.
     */
    @Test
    public void testLoadFromFileDescriptorSet() {
        // Create a FileDescriptorSet
        Descriptors.FileDescriptor fileDescriptor = ForTestProto.getDescriptor();
        List<Descriptors.FileDescriptor> fileDescriptors = new ArrayList<>();
        fileDescriptors.add(fileDescriptor);

        DescriptorProtos.FileDescriptorSet fileDescriptorSet = ProtoParser.save(fileDescriptors);

        // Load the descriptors
        Map<String, Descriptors.FileDescriptor> loadedDescriptors = ProtoParser.load(fileDescriptorSet);

        // Verify the result
        assertNotNull("Loaded descriptors should not be null", loadedDescriptors);
        assertFalse("Loaded descriptors should not be empty", loadedDescriptors.isEmpty());
        assertTrue("Loaded descriptors should contain the file descriptor",
                loadedDescriptors.containsKey(fileDescriptor.getName()));
        assertEquals("Loaded descriptor should match the original",
                fileDescriptor.getName(), loadedDescriptors.get(fileDescriptor.getName()).getName());

        // Verify that dependencies are resolved
        for (Descriptors.FileDescriptor dependency : fileDescriptor.getDependencies()) {
            assertTrue("Loaded descriptors should contain the dependency",
                    loadedDescriptors.containsKey(dependency.getName()));
            assertEquals("Loaded dependency should match the original",
                    dependency.getName(), loadedDescriptors.get(dependency.getName()).getName());
        }
    }

    /**
     * Test for {@link ProtoParser#save(java.util.List, java.io.OutputStream)}.
     * Verifies that the method correctly saves a list of FileDescriptors to an output stream.
     */
    @Test
    public void testSaveToOutputStream() throws IOException {
        // Create a list of FileDescriptors
        Descriptors.FileDescriptor fileDescriptor = ForTestProto.getDescriptor();
        List<Descriptors.FileDescriptor> fileDescriptors = new ArrayList<>();
        fileDescriptors.add(fileDescriptor);

        // Save to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ProtoParser.save(fileDescriptors, outputStream);

        // Verify the result
        byte[] bytes = outputStream.toByteArray();
        assertNotNull("Saved bytes should not be null", bytes);
        assertTrue("Saved bytes should not be empty", bytes.length > 0);

        // Parse the bytes back to a FileDescriptorSet
        DescriptorProtos.FileDescriptorSet parsedSet = DescriptorProtos.FileDescriptorSet.parseFrom(bytes);
        assertNotNull("Parsed set should not be null", parsedSet);
        assertFalse("Parsed set should not be empty", parsedSet.getFileList().isEmpty());

        // Verify that the original file descriptor is in the set
        boolean found = false;
        for (DescriptorProtos.FileDescriptorProto proto : parsedSet.getFileList()) {
            if (proto.getName().equals(fileDescriptor.getName())) {
                found = true;
                break;
            }
        }
        assertTrue("Original file descriptor should be in the set", found);
    }

    /**
     * Test for {@link ProtoParser#save(java.util.List)}.
     * Verifies that the method correctly converts a list of FileDescriptors to a FileDescriptorSet
     * and includes all dependencies of the provided FileDescriptors.
     */
    @Test
    public void testSaveToFileDescriptorSet() {
        // Create a list of FileDescriptors
        Descriptors.FileDescriptor fileDescriptor = ForTestProto.getDescriptor();
        List<Descriptors.FileDescriptor> fileDescriptors = new ArrayList<>();
        fileDescriptors.add(fileDescriptor);

        // Save to a FileDescriptorSet
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = ProtoParser.save(fileDescriptors);

        // Verify the result
        assertNotNull("FileDescriptorSet should not be null", fileDescriptorSet);
        assertFalse("FileDescriptorSet should not be empty", fileDescriptorSet.getFileList().isEmpty());

        // Verify that the original file descriptor is in the set
        boolean found = false;
        for (DescriptorProtos.FileDescriptorProto proto : fileDescriptorSet.getFileList()) {
            if (proto.getName().equals(fileDescriptor.getName())) {
                found = true;
                break;
            }
        }
        assertTrue("Original file descriptor should be in the set", found);

        // Verify that dependencies are included
        for (Descriptors.FileDescriptor dependency : fileDescriptor.getDependencies()) {
            boolean dependencyFound = false;
            for (DescriptorProtos.FileDescriptorProto proto : fileDescriptorSet.getFileList()) {
                if (proto.getName().equals(dependency.getName())) {
                    dependencyFound = true;
                    break;
                }
            }
            assertTrue("Dependency should be in the set: " + dependency.getName(), dependencyFound);
        }
    }

    /**
     * Test for round-trip conversion.
     * Verifies that saving and then loading descriptors produces the same result.
     */
    @Test
    public void testRoundTripConversion() throws IOException {
        // Create a list of FileDescriptors
        Descriptors.FileDescriptor fileDescriptor = ForTestProto.getDescriptor();
        List<Descriptors.FileDescriptor> fileDescriptors = new ArrayList<>();
        fileDescriptors.add(fileDescriptor);

        // Save to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ProtoParser.save(fileDescriptors, outputStream);

        // Load from the byte array
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Map<String, Descriptors.FileDescriptor> loadedDescriptors = ProtoParser.load(inputStream);

        // Verify the result
        assertNotNull("Loaded descriptors should not be null", loadedDescriptors);
        assertFalse("Loaded descriptors should not be empty", loadedDescriptors.isEmpty());
        assertTrue("Loaded descriptors should contain the file descriptor",
                loadedDescriptors.containsKey(fileDescriptor.getName()));

        // Compare the original and loaded descriptors
        Descriptors.FileDescriptor loadedDescriptor = loadedDescriptors.get(fileDescriptor.getName());
        assertEquals("Descriptor names should match",
                fileDescriptor.getName(), loadedDescriptor.getName());
        assertEquals("Descriptor package names should match",
                fileDescriptor.getPackage(), loadedDescriptor.getPackage());
        assertEquals("Descriptor message types count should match",
                fileDescriptor.getMessageTypes().size(), loadedDescriptor.getMessageTypes().size());
        assertEquals("Descriptor service count should match",
                fileDescriptor.getServices().size(), loadedDescriptor.getServices().size());
    }

    /**
     * Test for error handling in load method.
     * Verifies that the method throws a RuntimeException when there is an error parsing the input stream.
     */
    @Test(expected = RuntimeException.class)
    public void testLoadErrorHandling() {
        // Create an invalid input stream
        byte[] invalidBytes = {0, 1, 2, 3};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidBytes);

        // This should throw a RuntimeException
        ProtoParser.load(inputStream);
    }
}
