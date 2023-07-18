package io.rpc.test.provider.single;

import io.rpc.provider.RPCSingleServer;
import org.junit.Test;

public class RPCSingleServerTest {

    @Test
    public void startRPCSingleServer() {
//        RPCSingleServer singleServer = new RPCSingleServer("127.0.0.1:27880", "io.rpc.test", "jdk");
        RPCSingleServer singleServer = new RPCSingleServer(
                "127.0.0.1:27880",
                "127.0.0.1:2181",
                "zookeeper",
                "io.rpc.test",
                "cglib");
        singleServer.startNettyServer();
    }

}
