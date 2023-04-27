package io.rpc.annotation;


import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Autowired
public @interface RPCReference {

    /**
     * 版本号
     *
     * @return
     */
    String version() default "1.0.0";

    /**
     * 注册中心类型，默认zookeeper
     * zookeeper
     * nacos
     * etcd
     * consul
     *
     * @return
     */
    String registryType() default "zookeeper";

    /**
     * 注册地址
     *
     * @return
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 负载均衡，默认基于zk的一致性hash
     *
     * @return
     */
    String loadBalanceType() default "zkconsistenthash";


    /**
     * 序列化类型，默认protostuff
     * protostuff
     * kryo
     * json
     * jdk
     * hessian2
     * fst
     *
     * @return
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间，默认5s
     *
     * @return
     */
    long timeout() default 5000;

    /**
     * 是否异步执行
     *
     * @return
     */
    boolean async() default false;

    /**
     * 是否单向吊用
     *
     * @return
     */
    boolean oneway() default false;

    /**
     * 代理类型，默认jdk
     * jdk
     * javassist
     * cglib
     *
     * @return
     */
    String proxy() default "jdk";

    /**
     * 服务分组，默认为空
     *
     * @return
     */
    String group() default "";
}
