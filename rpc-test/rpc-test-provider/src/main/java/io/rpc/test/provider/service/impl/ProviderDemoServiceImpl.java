package io.rpc.test.provider.service.impl;

import io.rpc.annotation.RPCService;
import io.rpc.test.provider.service.DemoService;

@RPCService(interfaceClass = DemoService.class,
        interfaceClassName = "io.rpc.test.provider.service.DemoService",
        version = "1.0.0",
        group = "lrw")
public class ProviderDemoServiceImpl implements DemoService {
}
