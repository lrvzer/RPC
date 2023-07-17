package io.rpc.proxy.jdk;

import io.rpc.proxy.api.BaseProxyFactory;

import java.lang.reflect.Proxy;

public class JdkProxyFactory<T> extends BaseProxyFactory<T> {

    @Override
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                objectProxy
        );
    }

}
