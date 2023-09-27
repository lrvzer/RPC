package io.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * 主要标注在加入SPI机制的接口上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {
    String value() default "";
}
