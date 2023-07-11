package io.rpc.test.consumer;

import io.rpc.consumer.common.RPCConsumer;
import io.rpc.consumer.common.future.RPCFuture;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.header.RPCHeaderFactory;
import io.rpc.protocol.request.RPCRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCConsumerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(RPCConsumerHandlerTest.class);

    public static void main(String[] args) throws Exception {
        RPCConsumer consumer = RPCConsumer.getInstance();
        RPCFuture future = consumer.sendRequest(getPRCRequestProtocol());
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
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }
}
