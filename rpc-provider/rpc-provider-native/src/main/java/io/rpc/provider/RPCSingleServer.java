package io.rpc.provider;

import io.rpc.provider.common.scanner.RPCServiceScanner;
import io.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RPCSingleServer.class);

    public RPCSingleServer(String serverAddress, String registryAddress, String registryType, String scanPackage, String reflectType) {
        super(serverAddress, registryAddress, registryType, reflectType);

        try {
            this.handlerMap = RPCServiceScanner.doScannerWithRPCServiceAnnotationFilterAndRegistryService(this.host, this.port, scanPackage, registryService);
        } catch (Exception e) {
            logger.error("RPC Server init error", e);
        }
    }

}
