package io.rpc.serialization.fst;

import io.rpc.common.exception.SerializerException;
import io.rpc.serialization.api.Serialization;
import io.rpc.spi.annotation.SPIClass;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
public class FstSerialization implements Serialization {
    private final Logger logger = LoggerFactory.getLogger(FstSerialization.class);

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute fst serialize...");
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }

        FSTConfiguration configuration = FSTConfiguration.getDefaultConfiguration();
        return configuration.asByteArray(obj);
    }

    /**
     * 反序列化
     *
     * @param data
     * @param cls
     * @return
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute fst deserialize...");
        if (data == null) {
            throw new SerializerException("deserialize data is null");
        }

        FSTConfiguration configuration = FSTConfiguration.getDefaultConfiguration();
        return (T) configuration.asObject(data);
    }
}
