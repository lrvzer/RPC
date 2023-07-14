package io.rpc.proxy.api.object;

import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeaderFactory;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.proxy.api.consumer.Consumer;
import io.rpc.proxy.api.future.RPCFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class ObjectProxy<T> implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);

    // 接口的class对象
    private Class<T> clazz;

    // 服务版本号
    private String serviceVersion;

    // 服务分组
    private String serviceGroup;

    // 超时时间，默认15s
    private long timeout = 15000;

    // 服务消费者
    private Consumer consumer;

    // 序列化类型
    private String serializationType;

    // 是否异步调用
    private boolean async;

    // 是否单向调用
    private boolean oneway;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup, long timeout, Consumer consumer, String serializationType, boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RPCProtocol<RPCRequest> requestRPCProtocol = new RPCProtocol<>();
        requestRPCProtocol.setHeader(RPCHeaderFactory.getRequestHeader(serializationType));

        RPCRequest request = new RPCRequest();
        request.setVersion(serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        request.setGroup(serviceGroup);
        request.setAsync(async);
        request.setOneway(oneway);
        requestRPCProtocol.setBody(request);

        // Debug
        logger.debug(method.getDeclaringClass().getName());
        logger.debug(method.getName());

        if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                logger.debug(method.getParameterTypes()[i].getName());
            }
        }

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                logger.debug(args[i].toString());
            }
        }

        RPCFuture rpcFuture = this.consumer.sendRequest(requestRPCProtocol);

        return rpcFuture == null
                ? null
                : (timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get());
    }
}
