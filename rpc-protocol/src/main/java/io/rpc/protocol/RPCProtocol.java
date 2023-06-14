package io.rpc.protocol;

import io.rpc.protocol.header.RPCHeader;

import java.io.Serializable;

public class RPCProtocol<T> implements Serializable {

    private static final long serialVersionUID = -8279107011176101740L;

    // 消息头
    private RPCHeader header;

    // 消息体
    private T body;

    public RPCHeader getHeader() {
        return header;
    }

    public void setHeader(RPCHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
