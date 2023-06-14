package io.rpc.codec;


import io.rpc.serialization.api.Serialization;
import io.rpc.serialization.jdk.JdkSerialization;

public interface RPCCodec {
    default Serialization getJdkSerialization() {
        return new JdkSerialization();
    }
}
