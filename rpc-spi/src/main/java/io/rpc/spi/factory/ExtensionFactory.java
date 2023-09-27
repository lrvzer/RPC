package io.rpc.spi.factory;

import io.rpc.spi.annotation.SPI;

/**
 * 扩展类加载器的工厂接口
 */
@SPI("spi")
public interface ExtensionFactory {
    /**
     * 获取扩展类对象
     *
     * @param <T>   范型类型
     * @param key   传入的key值
     * @param clazz Class类型对象
     * @return 扩展类对象
     */
    <T> T getExtension(String key, Class<T> clazz);
}
