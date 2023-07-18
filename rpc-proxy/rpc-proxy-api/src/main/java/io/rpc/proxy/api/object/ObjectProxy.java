package io.rpc.proxy.api.object;

import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeaderFactory;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.proxy.api.async.IAsyncObjectProxy;
import io.rpc.proxy.api.consumer.Consumer;
import io.rpc.proxy.api.future.RPCFuture;
import io.rpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class ObjectProxy<T> implements InvocationHandler, IAsyncObjectProxy {

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

    private RegistryService registryService;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup, long timeout, RegistryService registryService, Consumer consumer, String serializationType, boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.registryService = registryService;
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

        RPCFuture rpcFuture = this.consumer.sendRequest(requestRPCProtocol, registryService);

        return rpcFuture == null
                ? null
                : (timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get());
    }

    @Override
    public RPCFuture call(String funcName, Object... args) {
        RPCProtocol<RPCRequest> request = createRequest(this.clazz.getName(), funcName, args);
        RPCFuture rpcFuture = null;
        try {
            rpcFuture = this.consumer.sendRequest(request, registryService);
        } catch (Exception e) {
            logger.error("async all throws exception: {}", e);
        }
        return rpcFuture;
    }

    private RPCProtocol<RPCRequest> createRequest(String className, String methodName, Object[] args) {
        RPCProtocol<RPCRequest> requestRPCProtocol = new RPCProtocol<>();
        requestRPCProtocol.setHeader(RPCHeaderFactory.getRequestHeader(serializationType));
        RPCRequest request = new RPCRequest();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        request.setVersion(serviceVersion);
        request.setGroup(serviceGroup);
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        requestRPCProtocol.setBody(request);

        logger.debug(className);
        logger.debug(methodName);
        for (int i = 0; i < parameterTypes.length; i++) {
            logger.debug(parameterTypes[i].getName());
        }

        for (int i = 0; i < args.length; i++) {
            logger.debug(args[i].toString());
        }

        return requestRPCProtocol;
    }

    private Class getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }
        return classType;
    }
}
