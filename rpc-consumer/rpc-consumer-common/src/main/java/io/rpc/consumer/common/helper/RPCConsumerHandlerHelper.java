package io.rpc.consumer.common.helper;

import io.rpc.consumer.common.handler.RPCConsumerHandler;
import io.rpc.protocol.meta.ServiceMeta;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCConsumerHandlerHelper {
    private static Map<String, RPCConsumerHandler> rpcConsumerHandlerMap;

    static {
        rpcConsumerHandlerMap = new ConcurrentHashMap<>();
    }

    private static String getKey(ServiceMeta key) {
        return key.getServiceAddr().concat("_").concat(String.valueOf(key.getServicePort()));
    }

    public static void put(ServiceMeta key, RPCConsumerHandler value) {
        rpcConsumerHandlerMap.put(getKey(key), value);
    }

    public static RPCConsumerHandler get(ServiceMeta key) {
        return rpcConsumerHandlerMap.get(getKey(key));
    }

    public static void closeRPCClientHandler() {
        Collection<RPCConsumerHandler> rpcClientHandlers = rpcConsumerHandlerMap.values();
        if (rpcClientHandlers != null) {
            rpcClientHandlers.stream().forEach(RPCConsumerHandler::close);
        }
        rpcClientHandlers.clear();
    }

}
