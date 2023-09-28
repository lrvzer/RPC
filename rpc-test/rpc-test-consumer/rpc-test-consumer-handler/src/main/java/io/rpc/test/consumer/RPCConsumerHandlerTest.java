package io.rpc.test.consumer;

import io.rpc.common.exception.RegistryException;
import io.rpc.consumer.common.RPCConsumer;
import io.rpc.consumer.common.context.RPCContext;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.header.RPCHeaderFactory;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.proxy.api.callback.AsyncRPCCallback;
import io.rpc.proxy.api.future.RPCFuture;
import io.rpc.registry.api.RegistryService;
import io.rpc.registry.api.config.RegistryConfig;
import io.rpc.spi.loader.ExtensionLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCConsumerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(RPCConsumerHandlerTest.class);

    /**
     * test oneway:false,async:true
     */
    public static void main3(String[] args) throws Exception {
        RPCConsumer consumer = RPCConsumer.getInstance();
        consumer.sendRequest(getPRCRequestProtocol(), getRegistryService("127.0.0.1:2181", "zookeeper", "random"));
        logger.info("无需获取返回的结果数据");
        consumer.close();
    }

    /**
     * test oneway:false,async:true
     */
    public static void main2(String[] args) throws Exception {
        RPCConsumer consumer = RPCConsumer.getInstance();
        consumer.sendRequest(getPRCRequestProtocol(), getRegistryService("127.0.0.1:2181", "zookeeper", "random"));
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
        RPCFuture future = consumer.sendRequest(getPRCRequestProtocol(), getRegistryService("127.0.0.1:2181", "zookeeper", "random"));
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

    private static RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }

        RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);

        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("RpcClient init registry service throws exception:{}", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
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
