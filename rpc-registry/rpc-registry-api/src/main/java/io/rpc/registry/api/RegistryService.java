package io.rpc.registry.api;

import io.rpc.protocol.meta.ServiceMeta;
import io.rpc.registry.api.config.RegistryConfig;

import java.io.IOException;

/**
 * 服务注册与发现接口
 **/
public interface RegistryService {
    /**
     * 服务注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 异常
     */
    void registry(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务取消注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 异常
     */
    void unRegistry(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     *
     * @param serviceName     服务名称
     * @param invokerHashCode HashCode值
     * @return 服务元数据
     * @throws Exception 异常
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    /**
     * 服务销毁
     *
     * @throws IOException 异常
     */
    void destroy() throws IOException;

    /**
     * 默认初始化方法
     *
     * @param registryConfig 注册中心配置
     * @throws Exception 异常
     */
    default void init(RegistryConfig registryConfig) throws Exception {

    }
}
