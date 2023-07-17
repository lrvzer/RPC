package io.rpc.consumer;

import io.rpc.consumer.common.RPCConsumer;
import io.rpc.proxy.api.async.IAsyncObjectProxy;
import io.rpc.proxy.api.config.ProxyConfig;
import io.rpc.proxy.api.object.ObjectProxy;
import io.rpc.proxy.jdk.JdkProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCClient {
    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);
    // 服务版本号
    private String serviceVersion;

    // 服务分组
    private String serviceGroup;
    // 序列化类型
    private String serializationType;
    private long timeout;

    // 是否异步调用
    private boolean async;

    // 是否单向调用
    private boolean oneway;

    public RPCClient(String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.timeout = timeout;
        this.async = async;
        this.oneway = oneway;
    }

    public <T> T create(Class<T> interfaceClass) {
        JdkProxyFactory<T> jdkProxyFactory = new JdkProxyFactory<>();
        jdkProxyFactory.init(new ProxyConfig(interfaceClass, serviceVersion, serviceGroup, serializationType, RPCConsumer.getInstance(), timeout, async, oneway));
        return jdkProxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, timeout, RPCConsumer.getInstance(), serializationType, async, oneway);
    }

    public void shutdown() {
        RPCConsumer.getInstance().close();
    }
}
