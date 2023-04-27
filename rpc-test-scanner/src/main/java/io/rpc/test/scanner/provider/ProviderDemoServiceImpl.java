package io.rpc.test.scanner.provider;

import io.rpc.annotation.RPCService;
import io.rpc.test.scanner.service.DemoService;

@RPCService(
        interfaceClass = DemoService.class,
        interfaceClassName = "io.rpc.test.scanner.service.DemoService",
        version = "1.0.0",
        group = "lrw")
public class ProviderDemoServiceImpl implements DemoService {

}
