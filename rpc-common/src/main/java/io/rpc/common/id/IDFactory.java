package io.rpc.common.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 简易ID工厂类
 **/
public class IDFactory {
    private static final AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static Long getID() {
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
