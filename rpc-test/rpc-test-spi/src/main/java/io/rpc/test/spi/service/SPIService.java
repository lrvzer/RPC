package io.rpc.test.spi.service;

import io.rpc.spi.annotation.SPI;

@SPI("spiService")
public interface SPIService {
    String hello(String name);
}
