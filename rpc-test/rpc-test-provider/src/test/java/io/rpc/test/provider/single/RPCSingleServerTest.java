package io.rpc.test.provider.single;

import org.junit.Test;
import io.rpc.provider.RPCSingleServer;

public class RPCSingleServerTest {

    @Test
    public void startRPCSingleServer() {
        RPCSingleServer singleServer = new RPCSingleServer("127.0.0.1:27880", "io.rpc.test");
        singleServer.startNettyServer();
    }

}
