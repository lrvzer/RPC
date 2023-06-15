package io.rpc.test.provider.service.impl;

import io.rpc.annotation.RPCService;
import io.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RPCService(interfaceClass = DemoService.class,
        interfaceClassName = "io.rpc.test.api.DemoService",
        version = "1.0.0",
        group = "lrw")
public class ProviderDemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数-->{}", name);
        return "hello " + name;
    }
}
