package io.rpc.proxy.api;

import io.rpc.proxy.api.config.ProxyConfig;
import io.rpc.proxy.api.object.ObjectProxy;

public abstract class BaseProxyFactory<T> implements ProxyFactory {
    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> proxyConfig) {
        this.objectProxy = new ObjectProxy(
                proxyConfig.getClazz(),
                proxyConfig.getServiceVersion(),
                proxyConfig.getServiceGroup(),
                proxyConfig.getTimeout(),
                proxyConfig.getConsumer(),
                proxyConfig.getSerializationType(),
                proxyConfig.isAsync(),
                proxyConfig.isOneway()
        );
    }
}
