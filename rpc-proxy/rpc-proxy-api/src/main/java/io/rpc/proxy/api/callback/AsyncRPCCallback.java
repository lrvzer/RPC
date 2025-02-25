package io.rpc.proxy.api.callback;

public interface AsyncRPCCallback {
    /**
     * 成功后的回调方法
     *
     * @param result
     */
    void onSuccess(Object result);

    /**
     * 异常的回调方法
     *
     * @param e
     */
    void onException(Exception e);
}
