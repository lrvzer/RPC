package io.rpc.test.consumer;

import io.rpc.consumer.RPCClient;
import io.rpc.proxy.api.async.IAsyncObjectProxy;
import io.rpc.proxy.api.future.RPCFuture;
import io.rpc.test.api.DemoService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RPCConsumerNativeTest.class);

    @Test
    public void testInterfaceRPC() {
        RPCClient rpcClient = new RPCClient(
                "1.0.0",
                "lrw",
                "jdk",
                3000,
                false,
                false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("rpc-2023-7-17");
        logger.info("返回的结果数据===>{}", result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRPC() throws Exception {
        RPCClient rpcClient = new RPCClient("1.0.0", "lrw", "jdk", 300, false, false);
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RPCFuture future = demoService.call("hello", "rpc-2023-7-17");
        Thread.sleep(1000);
        logger.info("返回的结果数据 ---> {}", future.get());
        rpcClient.shutdown();
    }
}
