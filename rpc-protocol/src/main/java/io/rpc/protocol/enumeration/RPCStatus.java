package io.rpc.protocol.enumeration;

/**
 * 服务调用的状态
 */
public enum RPCStatus {
    SUCCESS(0), // 成功
    FAIL(1); // 失败
    private final int code;

    RPCStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
