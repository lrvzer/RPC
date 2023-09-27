package io.rpc.spi.factory;

import io.rpc.spi.annotation.SPI;
import io.rpc.spi.annotation.SPIClass;
import io.rpc.spi.loader.ExtensionLoader;

import java.util.Optional;

/**
 * 基于SPI实现的扩展类加载器工具类
 */
@SPIClass
public class SpiExtensionFactory implements ExtensionFactory {
    /**
     * 获取扩展类对象
     *
     * @param key   传入的key值
     * @param clazz Class类型对象
     * @return 扩展类对象
     */
    @Override
    public <T> T getExtension(String key, Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);
    }
}
