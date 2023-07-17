package io.rpc.proxy.api;

import io.rpc.proxy.api.config.ProxyConfig;

/**
 * @Description
 * @Author: Lrwei
 * @Date: 2023/7/17
 **/
public interface ProxyFactory {
    // 获取代理对象
    <T> T getProxy(Class<T> clazz);

    // 默认初始化方法
    default <T> void init(ProxyConfig<T> proxyConfig) {
    }
}
