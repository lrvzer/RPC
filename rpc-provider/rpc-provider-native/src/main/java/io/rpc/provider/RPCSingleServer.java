package io.rpc.provider;

import io.rpc.common.scanner.server.RPCServiceScanner;
import io.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RPCSingleServer.class);

    public RPCSingleServer(String serverAddress, String scanPackage) {
        super(serverAddress);

        try {
            this.handlerMap = RPCServiceScanner.doScannerWithRPCServiceAnnotationFilterAndRegistryService(scanPackage);
        } catch (Exception e) {
            logger.error("RPC Server init error", e);
        }
    }

}
