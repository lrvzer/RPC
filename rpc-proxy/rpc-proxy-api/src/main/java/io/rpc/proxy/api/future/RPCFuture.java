package io.rpc.proxy.api.future;

import io.rpc.common.threadpool.ClientThreadPool;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import io.rpc.proxy.api.callback.AsyncRPCCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class RPCFuture extends CompletableFuture<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RPCFuture.class);
    private final Sync sync;
    /**
     * RPCRequest类型的协议对象
     */
    private final RPCProtocol<RPCRequest> requestRPCProtocol;
    /**
     * RPCResponse类型的协议对象
     */
    private RPCProtocol<RPCResponse> responseRPCProtocol;
    /**
     * 开始时间
     */
    private final long startTime;
    /**
     * 默认超时时间
     */
    private final long responseTimeThreshold = 5000;

    private List<AsyncRPCCallback> pendingCallbacks = new ArrayList<AsyncRPCCallback>();

    private ReentrantLock lock = new ReentrantLock();

    public RPCFuture(RPCProtocol<RPCRequest> requestRPCProtocol) {
        this.sync = new Sync();
        this.requestRPCProtocol = requestRPCProtocol;
        this.startTime = System.currentTimeMillis();
    }

    private void runCallback(final AsyncRPCCallback callback) {
        final RPCResponse res = this.responseRPCProtocol.getBody();
        ClientThreadPool.submit(() -> {
            if (!res.isError()) {
                callback.onSuccess(res.getResult());
            } else {
                callback.onException(new RuntimeException("Response error", new Throwable(res.getError())));
            }
        });
    }

    public RPCFuture addCallback(AsyncRPCCallback callback) {
        lock.lock();
        try {
            if (isDone()) { // 运行完毕后运行callback
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncRPCCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    /**
     * 主要阻塞获取responseRPCProtocol协议对象中的实际结果数据
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.responseRPCProtocol != null) {
            return this.responseRPCProtocol.getBody().getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.responseRPCProtocol != null) {
                return this.responseRPCProtocol.getBody().getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: "
                    + requestRPCProtocol.getHeader().getRequestId()
                    + ". Request class name: " + this.requestRPCProtocol.getBody().getClassName()
                    + ". Request method: " + this.requestRPCProtocol.getBody().getMethodName()
            );
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    /**
     * 当服务消费者收到服务提供者响应的结果数据时，就会调用done()方法，
     * 并传入RPCResponse类型的协议对象，此时会唤醒阻塞的线程获取响应的结果数据
     *
     * @param responseRPCProtocol
     */
    public void done(RPCProtocol<RPCResponse> responseRPCProtocol) {
        this.responseRPCProtocol = responseRPCProtocol;
        sync.release(1);
        // 新增的调用的invokeCallbacks()方法
        invokeCallbacks();
        // Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = {}. Response Time = {}ms",
                    responseRPCProtocol.getHeader().getRequestId(),
                    responseTime);
        }
    }

    /**
     * Sync继承AQS
     */
    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = -1725634303906208000L;
        // future status
        private static final int DONE = 1;
        private static final int PENDING = 0;

        @Override
        protected boolean tryAcquire(int acquire) {
            return getState() == DONE;
        }

        @Override
        protected boolean tryRelease(int arg) {
            return (getState() == PENDING) && (compareAndSetState(PENDING, DONE));
        }

        public boolean isDone() {
            return getState() == DONE;
        }
    }
}
