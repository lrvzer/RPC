package io.rpc.test.spi.service.impl;

import io.rpc.spi.annotation.SPIClass;
import io.rpc.test.spi.service.SPIService;

@SPIClass
public class SPIServiceImpl implements SPIService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
