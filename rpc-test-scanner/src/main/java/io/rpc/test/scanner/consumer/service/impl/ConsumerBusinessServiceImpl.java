package io.rpc.test.scanner.consumer.service.impl;

import io.rpc.annotation.RPCReference;
import io.rpc.test.scanner.consumer.service.ConsumerBusinessService;
import io.rpc.test.scanner.service.DemoService;

public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {
    @RPCReference(
            registryType = "zookeeper",
            registryAddress = "127.0.0.1:2181",
            version = "1.0.0",
            group = "lrw")
    private DemoService demoService;
}
