package io.rpc.serialization.jdk;

import io.rpc.common.exception.SerializerException;
import io.rpc.serialization.api.Serialization;

import java.io.*;

public class JdkSerialization implements Serialization {

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(obj);
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        }
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
        if (data == null) {
            throw new SerializerException("serialize data is null");
        }

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(is);
            return (T) in.readObject();
        } catch (Exception e) {
            throw new SerializerException(e.getMessage(), e);
        }
    }
}
