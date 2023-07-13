package io.rpc.test.consumer;

import io.rpc.consumer.common.RPCConsumer;
import io.rpc.consumer.common.callback.AsyncRPCCallback;
import io.rpc.consumer.common.context.RPCContext;
import io.rpc.consumer.common.future.RPCFuture;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.header.RPCHeaderFactory;
import io.rpc.protocol.request.RPCRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCConsumerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(RPCConsumerHandlerTest.class);

    /**
     * test oneway:false,async:true
     */
    public static void main3(String[] args) throws Exception {
        RPCConsumer consumer = RPCConsumer.getInstance();
        consumer.sendRequest(getPRCRequestProtocol());
        logger.info("无需获取返回的结果数据");
        consumer.close();
    }

    /**
     * test oneway:false,async:true
     */
    public static void main2(String[] args) throws Exception {
        RPCConsumer consumer = RPCConsumer.getInstance();
        consumer.sendRequest(getPRCRequestProtocol());
        RPCFuture future = RPCContext.getContext().getRPCFuture();
        logger.info("future--->{}", future);
        Thread.sleep(1000);
        logger.info("从服务消费者获取到的对象--->{}", future.get());
        consumer.close();
    }

    /**
     * main1
     * test oneway:false,async:false
     */
    public static void main(String[] args) throws Exception {
        RPCConsumer consumer = RPCConsumer.getInstance();
        RPCFuture future = consumer.sendRequest(getPRCRequestProtocol());
        future.addCallback(new AsyncRPCCallback() {
            @Override
            public void onSuccess(Object result) {
                logger.info("从服务消费者获取到的数据--->{}", result);
            }

            @Override
            public void onException(Exception e) {
                logger.info("抛出异常--->{}", e);
            }
        });
        Thread.sleep(1000);
        logger.info("从服务消费者获取到的对象--->{}", future.get());
        consumer.close();
    }

    private static RPCProtocol<RPCRequest> getPRCRequestProtocol() {
        RPCProtocol<RPCRequest> protocol = new RPCProtocol<>();
        RPCHeader header = RPCHeaderFactory.getRequestHeader("jdk");
        protocol.setHeader(header);
        RPCRequest request = new RPCRequest();
        request.setClassName("io.rpc.test.api.DemoService");
        request.setGroup("lrw");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"hello, rpc"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        // 对应main1
        request.setAsync(false);
        request.setOneway(false);
        // main2
//        request.setAsync(true);
//        request.setOneway(false);
        // main3
//        request.setAsync(false);
//        request.setOneway(true);
        protocol.setBody(request);
        return protocol;
    }
}
