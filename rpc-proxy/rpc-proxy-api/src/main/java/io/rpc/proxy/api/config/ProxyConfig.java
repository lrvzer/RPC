package io.rpc.proxy.api.config;

import io.rpc.proxy.api.consumer.Consumer;
import io.rpc.registry.api.RegistryService;

import java.io.Serializable;

public class ProxyConfig<T> implements Serializable {
    private static final long serialVersionUID = -4910443735632547721L;
    // 接口的Class实例
    private Class<T> clazz;
    // 服务版本号
    private String serviceVersion;
    // 服务分组
    private String serviceGroup;
    // 序列化类型
    private String serializationType;
    // 消费者接口
    private Consumer consumer;
    // 超时时间
    private long timeout;
    // 是否异步调用
    private boolean async;
    // 是否单向调用
    private boolean oneway;

    private RegistryService registryService;

    public ProxyConfig(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType, RegistryService registryService, Consumer consumer, long timeout, boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.consumer = consumer;
        this.timeout = timeout;
        this.async = async;
        this.oneway = oneway;
        this.registryService = registryService;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(String serializationType) {
        this.serializationType = serializationType;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }
}
