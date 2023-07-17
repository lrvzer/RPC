package io.rpc.test.consumer;

import io.rpc.consumer.RPCClient;
import io.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author: Lrwei
 * @Date: 2023/7/17
 **/
public class RPCConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RPCConsumerNativeTest.class);

    public static void main(String[] args) {
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
}
