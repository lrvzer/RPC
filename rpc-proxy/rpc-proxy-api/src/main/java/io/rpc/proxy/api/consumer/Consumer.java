package io.rpc.proxy.api.consumer;

import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.proxy.api.future.RPCFuture;
import io.rpc.registry.api.RegistryService;

public interface Consumer {
    /**
     * 消息发送request请求
     *
     * @param protocol
     * @return
     * @throws Exception
     */
    RPCFuture sendRequest(RPCProtocol<RPCRequest> protocol, RegistryService registryService) throws Exception;
}
