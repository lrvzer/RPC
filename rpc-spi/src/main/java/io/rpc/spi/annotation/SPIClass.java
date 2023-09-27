package io.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * 主要标注到加入SPI机制的接口的实现类上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPIClass {
}
