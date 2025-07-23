package io.masterkun.ai.grpc.registry;

import com.google.protobuf.Descriptors;
import io.masterkun.toolcall.proto.TestDiscoveryProto;
import io.masterkun.toolcall.proto.TestDiscovery2Proto;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link GrpcBridgeToolGroupSet} class.
 * These tests verify that the auto-discovery functionality works as described in the comments.
 */
public class GrpcBridgeToolGroupSetTest {

    private GrpcBridgeToolGroupSet toolGroupSet;
    private Map<String, Descriptors.FileDescriptor> fileDescriptors;
    private Set<String> services;

    @Before
    public void setUp() {
        toolGroupSet = new GrpcBridgeToolGroupSet(null);
        fileDescriptors = new HashMap<>();
        services = new HashSet<>();

        // Add file descriptors for test proto files
        Descriptors.FileDescriptor testDiscoveryDescriptor = TestDiscoveryProto.getDescriptor();
        Descriptors.FileDescriptor testDiscovery2Descriptor = TestDiscovery2Proto.getDescriptor();

        fileDescriptors.put(testDiscoveryDescriptor.getName(), testDiscoveryDescriptor);
        fileDescriptors.put(testDiscovery2Descriptor.getName(), testDiscovery2Descriptor);

        // Add service names
        for (Descriptors.FileDescriptor descriptor : fileDescriptors.values()) {
            for (Descriptors.ServiceDescriptor service : descriptor.getServices()) {
                services.add(service.getFullName());
            }
        }
    }

    /**
     * Test that the auto-discovery functionality correctly handles file-level settings.
     * In test_discovery.proto, file_auto_discovery = true, so methods without explicit settings
     * should be enabled if their service doesn't have an explicit setting.
     */
    @Test
    public void testFileAutoDiscovery() throws IOException {
        // Reload tool groups using auto-discovery
        toolGroupSet.reloadByAutoDiscovery(services, fileDescriptors);

        // Verify that TestDiscoveryService has the correct tools
        assertTrue("TestDiscoveryService group should exist",
                toolGroupSet.containsGroup("io.masterkun.test.TestDiscoveryService"));

        GrpcBridgeToolGroup group = toolGroupSet.getGroup("io.masterkun.test.TestDiscoveryService");

        // test1 has method_auto_discovery = true, so it should be included
        boolean hasTest1 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test1"));
        assertTrue("TestDiscoveryService should include test1", hasTest1);

        // test2 has method_auto_discovery = false, so it should not be included
        boolean hasTest2 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test2"));
        assertFalse("TestDiscoveryService should not include test2", hasTest2);

        // test3 has no explicit setting, but file has auto_discovery = true,
        // so it should be included
        boolean hasTest3 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test3"));
        assertTrue("TestDiscoveryService should include test3", hasTest3);
    }

    /**
     * Test that the auto-discovery functionality correctly handles service-level settings.
     * In test_discovery.proto, TestDiscoveryService2 has service_auto_discovery = true,
     * so methods without explicit settings should be enabled.
     */
    @Test
    public void testServiceAutoDiscoveryEnabled() throws IOException {
        // Reload tool groups using auto-discovery
        toolGroupSet.reloadByAutoDiscovery(services, fileDescriptors);

        // Verify that TestDiscoveryService2 has the correct tools
        assertTrue("TestDiscoveryService2 group should exist",
                toolGroupSet.containsGroup("io.masterkun.test.TestDiscoveryService2"));

        GrpcBridgeToolGroup group = toolGroupSet.getGroup("io.masterkun.test.TestDiscoveryService2");

        // test1 has method_auto_discovery = true, so it should be included
        boolean hasTest1 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test1"));
        assertTrue("TestDiscoveryService2 should include test1", hasTest1);

        // test2 has method_auto_discovery = false, so it should not be included
        boolean hasTest2 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test2"));
        assertFalse("TestDiscoveryService2 should not include test2", hasTest2);

        // test3 has no explicit setting, but service has auto_discovery = true,
        // so it should be included
        boolean hasTest3 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test3"));
        assertTrue("TestDiscoveryService2 should include test3", hasTest3);
    }

    /**
     * Test that the auto-discovery functionality correctly handles service-level settings.
     * In test_discovery.proto, TestDiscoveryService3 has service_auto_discovery = false,
     * so methods without explicit settings should not be enabled.
     */
    @Test
    public void testServiceAutoDiscoveryDisabled() throws IOException {
        // Reload tool groups using auto-discovery
        toolGroupSet.reloadByAutoDiscovery(services, fileDescriptors);

        // Verify that TestDiscoveryService3 has the correct tools
        assertTrue("TestDiscoveryService3 group should exist",
                toolGroupSet.containsGroup("io.masterkun.test.TestDiscoveryService3"));

        GrpcBridgeToolGroup group = toolGroupSet.getGroup("io.masterkun.test.TestDiscoveryService3");

        // test1 has method_auto_discovery = true, so it should be included
        boolean hasTest1 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test1"));
        assertTrue("TestDiscoveryService3 should include test1", hasTest1);

        // test2 has method_auto_discovery = false, so it should not be included
        boolean hasTest2 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test2"));
        assertFalse("TestDiscoveryService3 should not include test2", hasTest2);

        // test3 has no explicit setting, but service has auto_discovery = false,
        // so it should not be included
        boolean hasTest3 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test3"));
        assertFalse("TestDiscoveryService3 should not include test3", hasTest3);
    }

    /**
     * Test that the auto-discovery functionality correctly handles the case where there is no
     * file-level setting. In test_discovery2.proto, there is no file_auto_discovery option,
     * so methods without explicit settings should not be enabled if their service doesn't have
     * an explicit setting.
     */
    @Test
    public void testNoFileAutoDiscovery() throws IOException {
        // Reload tool groups using auto-discovery
        toolGroupSet.reloadByAutoDiscovery(services, fileDescriptors);

        // Verify that TestDiscovery2Service has the correct tools
        assertTrue("TestDiscovery2Service group should exist",
                toolGroupSet.containsGroup("io.masterkun.test.TestDiscovery2Service"));

        GrpcBridgeToolGroup group = toolGroupSet.getGroup("io.masterkun.test.TestDiscovery2Service");

        // test1 has method_auto_discovery = true, so it should be included
        boolean hasTest1 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test1"));
        assertTrue("TestDiscovery2Service should include test1", hasTest1);

        // test2 has method_auto_discovery = false, so it should not be included
        boolean hasTest2 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test2"));
        assertFalse("TestDiscovery2Service should not include test2", hasTest2);

        // test3 has no explicit setting, and there's no file-level setting,
        // so it should not be included
        boolean hasTest3 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test3"));
        assertFalse("TestDiscovery2Service should not include test3", hasTest3);
    }

    /**
     * Test that the auto-discovery functionality correctly handles the case where there is no
     * file-level setting but there is a service-level setting. In test_discovery2.proto,
     * TestDiscovery2Service2 has service_auto_discovery = true, so methods without explicit
     * settings should be enabled.
     */
    @Test
    public void testNoFileWithServiceAutoDiscoveryEnabled() throws IOException {
        // Reload tool groups using auto-discovery
        toolGroupSet.reloadByAutoDiscovery(services, fileDescriptors);

        // Verify that TestDiscovery2Service2 has the correct tools
        assertTrue("TestDiscovery2Service2 group should exist",
                toolGroupSet.containsGroup("io.masterkun.test.TestDiscovery2Service2"));

        GrpcBridgeToolGroup group = toolGroupSet.getGroup("io.masterkun.test.TestDiscovery2Service2");

        // test1 has method_auto_discovery = true, so it should be included
        boolean hasTest1 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test1"));
        assertTrue("TestDiscovery2Service2 should include test1", hasTest1);

        // test2 has method_auto_discovery = false, so it should not be included
        boolean hasTest2 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test2"));
        assertFalse("TestDiscovery2Service2 should not include test2", hasTest2);

        // test3 has no explicit setting, but service has auto_discovery = true,
        // so it should be included
        boolean hasTest3 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test3"));
        assertTrue("TestDiscovery2Service2 should include test3", hasTest3);
    }

    /**
     * Test that the auto-discovery functionality correctly handles the case where there is no
     * file-level setting but there is a service-level setting. In test_discovery2.proto,
     * TestDiscovery2Service3 has service_auto_discovery = false, so methods without explicit
     * settings should not be enabled.
     */
    @Test
    public void testNoFileWithServiceAutoDiscoveryDisabled() throws IOException {
        // Reload tool groups using auto-discovery
        toolGroupSet.reloadByAutoDiscovery(services, fileDescriptors);

        // Verify that TestDiscovery2Service3 has the correct tools
        assertTrue("TestDiscovery2Service3 group should exist",
                toolGroupSet.containsGroup("io.masterkun.test.TestDiscovery2Service3"));

        GrpcBridgeToolGroup group = toolGroupSet.getGroup("io.masterkun.test.TestDiscovery2Service3");

        // test1 has method_auto_discovery = true, so it should be included
        boolean hasTest1 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test1"));
        assertTrue("TestDiscovery2Service3 should include test1", hasTest1);

        // test2 has method_auto_discovery = false, so it should not be included
        boolean hasTest2 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test2"));
        assertFalse("TestDiscovery2Service3 should not include test2", hasTest2);

        // test3 has no explicit setting, but service has auto_discovery = false,
        // so it should not be included
        boolean hasTest3 = group.tools().stream()
                .anyMatch(tool -> tool.methodDescriptor().getName().equals("test3"));
        assertFalse("TestDiscovery2Service3 should not include test3", hasTest3);
    }
}
