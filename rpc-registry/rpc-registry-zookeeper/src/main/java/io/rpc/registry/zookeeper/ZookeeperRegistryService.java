package io.rpc.registry.zookeeper;

import io.rpc.common.helper.RPCServiceHelper;
import io.rpc.protocol.meta.ServiceMeta;
import io.rpc.registry.api.RegistryService;
import io.rpc.registry.api.config.RegistryConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ZookeeperRegistryService implements RegistryService {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    /**
     * 初始化CuratorFramework客户端时，进行连接重试的间隔时间
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;
    /**
     * 初始化CuratorFramework客户端时，进行连接重试的最大重试次数
     */
    public static final int MAX_RETRIES = 3;
    /**
     * 服务注册到Zookeeper的根路径
     */
    public static final String ZK_BASE_PATH = "/rpc";
    /**
     * 服务注册与发现的ServiceDiscovery类实例
     */
    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 服务注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 异常
     */
    @Override
    public void registry(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RPCServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 服务取消注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 异常
     */
    @Override
    public void unRegistry(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 服务发现
     *
     * @param serviceName     服务名称
     * @param invokerHashCode HashCode值
     * @return 服务元数据
     * @throws Exception 异常
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        logger.info("serviceName: {}, serviceInstances: {}", serviceName, serviceInstances);
        ServiceInstance<ServiceMeta> instance = this.selectOneServiceInstance((List<ServiceInstance<ServiceMeta>>) serviceInstances);
        if (instance != null) {
            return instance.getPayload();
        }
        return null;
    }

    private ServiceInstance<ServiceMeta> selectOneServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances) {
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(serviceInstances.size());
        return serviceInstances.get(index);
    }

    /**
     * 服务销毁
     *
     * @throws IOException 异常
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }

    /**
     * 构建CuratorFramework客户端，并初始化serviceDiscovery
     *
     * @param registryConfig 注册中心配置
     * @throws Exception 异常
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddr(), new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }
}
