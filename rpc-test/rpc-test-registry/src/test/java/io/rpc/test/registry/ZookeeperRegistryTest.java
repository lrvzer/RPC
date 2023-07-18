package io.rpc.test.registry;

import io.rpc.protocol.meta.ServiceMeta;
import io.rpc.registry.api.RegistryService;
import io.rpc.registry.api.config.RegistryConfig;
import io.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperRegistryTest {
    private RegistryService registryService;
    private ServiceMeta serviceMeta;

    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181", "zookeeper");
        this.registryService = new ZookeeperRegistryService();
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(ZookeeperRegistryTest.class.getName(), "1.0.0", "127.0.0.1", 8080, "rpc");
    }

    @Test
    public void testRegister() throws Exception {
        this.registryService.registry(serviceMeta);
    }

    @Test
    public void testUnRegistry() throws Exception {
        this.registryService.unRegistry(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception {
        this.registryService.discovery(RegistryService.class.getName(), "rpc".hashCode());
    }

    @Test
    public void testDestroy() throws Exception {
        this.registryService.destroy();
    }
}
