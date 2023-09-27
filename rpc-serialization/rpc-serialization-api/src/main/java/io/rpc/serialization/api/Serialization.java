package io.rpc.serialization.api;

import io.rpc.constant.RPCConstants;
import io.rpc.spi.annotation.SPI;

/**
 * 序列化与反序列化接口
 */
@SPI(RPCConstants.SERIALIZATION_JDK)
public interface Serialization {

    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     *
     * @param data
     * @param cls
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> cls);

}
