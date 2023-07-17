package io.rpc.proxy.api.async;

import io.rpc.proxy.api.future.RPCFuture;

public interface IAsyncObjectProxy {
    /**
     * 异步代理对象调用方式
     *
     * @param funcName 方法名称
     * @param args     方法参数
     * @return 等装好的RPCFuture对象
     */
    RPCFuture call(String funcName, Object... args);
}
