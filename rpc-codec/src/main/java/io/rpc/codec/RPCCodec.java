package io.rpc.codec;


import io.rpc.serialization.api.Serialization;
import io.rpc.spi.loader.ExtensionLoader;

public interface RPCCodec {
    /**
     * 根据serializationType通过SPI过去序列化句柄
     *
     * @param serializationType 序列化方式
     * @return Serialization对象
     */
    default Serialization getSerialization(String serializationType) {
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }
}
