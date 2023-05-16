package io.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @RPCService注解主要使用的场景是：标注到RPC服务实现类上，
 * 如果某个接口的实现类被标注了@RPCService注解，则这个接口与实现类会被发布为RPC服务，对外提供远程服务。
 * 使用@RPCService注解标注的服务承担服务提供者的角色。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RPCService {

    /**
     * 接口的class
     *
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口ClassName
     *
     * @return
     */
    String interfaceClassName() default "";

    /**
     * 版本号
     *
     * @return
     */
    String version() default "1.0.0";

    /**
     * 服务分组，默认为空
     *
     * @return
     */
    String group() default "";
}
