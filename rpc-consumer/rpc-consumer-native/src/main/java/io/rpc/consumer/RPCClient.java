package io.rpc.consumer;

import io.rpc.consumer.common.RPCConsumer;
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
        JdkProxyFactory jdkProxyFactory = new JdkProxyFactory(serviceVersion, serviceGroup, timeout, RPCConsumer.getInstance(), serializationType, async, oneway);
        return jdkProxyFactory.getProxy(interfaceClass);
    }

    public void shutdown()   {
        RPCConsumer.getInstance().close();
    }
}
