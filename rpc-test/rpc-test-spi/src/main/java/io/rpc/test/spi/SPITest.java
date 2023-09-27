package io.rpc.test.spi;

import io.rpc.spi.loader.ExtensionLoader;
import io.rpc.test.spi.service.SPIService;
import org.junit.Test;

public class SPITest {
    @Test
    public void testSpiLoader() {
        SPIService spiService = ExtensionLoader.getExtension(SPIService.class, "spiService");
        String result = spiService.hello("extension loader");
        System.out.println(result);
    }
}
