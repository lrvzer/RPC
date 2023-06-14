package io.rpc.protocol.enumeration;

/**
 * 枚举类，主要标识传输消息的类型
 */
public enum RPCType {
    // 请求消息
    REQUEST(1),
    // 响应消息
    RESPONSE(2),
    // 心跳数据
    HEARTBEAT(3);

    private final int type;

    RPCType(int type) {
        this.type = type;
    }

    public static RPCType findByType(byte msgType) {
        switch (msgType) {
            case 1:
                return REQUEST;
            case 2:
                return RESPONSE;
            case 3:
                return HEARTBEAT;
            default:
                return null;
        }
    }

    public int getType() {
        return this.type;
    }
}