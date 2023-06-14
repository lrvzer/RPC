package io.rpc.protocol.base;

import java.io.Serializable;

public class RPCMessage implements Serializable {
    // 是否单向发送
    private boolean oneway;
    // 是否异步调用
    private boolean async;

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
