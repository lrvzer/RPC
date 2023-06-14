package io.rpc.serialization.api;

/**
 * 序列化与反序列化接口
 */
public interface Serialization {

    /**
     * 序列化
     * @param obj
     * @return
     * @param <T>
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param data
     * @param cls
     * @return
     * @param <T>
     */
    <T> T deserialize(byte[] data, Class<T> cls);

}
