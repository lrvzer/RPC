package io.rpc.consumer.common.context;

import io.rpc.proxy.api.future.RPCFuture;

public class RPCContext {

    public RPCContext() {
    }

    /**
     * 存放RPCFuture的InheritableThreadLocal
     */
    private static final InheritableThreadLocal<RPCFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    // RPCContext实例
    private static final RPCContext AGENT = new RPCContext();

    /**
     * 获取上下文
     *
     * @return RPC上下文信息
     */
    public static RPCContext getContext() {
        return AGENT;
    }

    // 降RPCFuture保存到线程的上下文
    public void setRPCFuture(RPCFuture rpcFuture) {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    // 获取RPCFuture
    public RPCFuture getRPCFuture() {
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    // 移除RPCFuture
    public void removeRPCFuture() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }

}
