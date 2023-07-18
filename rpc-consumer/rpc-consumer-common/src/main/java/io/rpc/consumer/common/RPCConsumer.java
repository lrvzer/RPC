package io.rpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rpc.common.helper.RPCServiceHelper;
import io.rpc.common.threadpool.ClientThreadPool;
import io.rpc.consumer.common.handler.RPCConsumerHandler;
import io.rpc.consumer.common.helper.RPCConsumerHandlerHelper;
import io.rpc.consumer.common.initializer.RPCConsumerInitializer;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.meta.ServiceMeta;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.proxy.api.consumer.Consumer;
import io.rpc.proxy.api.future.RPCFuture;
import io.rpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCConsumer implements Consumer {
    private final Logger logger = LoggerFactory.getLogger(RPCConsumer.class);

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    private static volatile RPCConsumer instance;

    private static Map<String, RPCConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RPCConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new RPCConsumerInitializer());
    }

    public static RPCConsumer getInstance() {
        if (instance == null) {
            synchronized (RPCConsumer.class) {
                if (instance == null) {
                    instance = new RPCConsumer();
                }
            }
        }
        return instance;
    }

    public void close() {
        RPCConsumerHandlerHelper.closeRPCClientHandler();
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }

    @Override
    public RPCFuture sendRequest(RPCProtocol<RPCRequest> protocol, RegistryService registryService) throws Exception {
        RPCRequest request = protocol.getBody();
        String serviceKey = RPCServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object[] params = request.getParameters();
        int invokerHashCode = (params == null || params.length <= 0) ? serviceKey.hashCode() : params[0].hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMeta != null) {
            RPCConsumerHandler handler = RPCConsumerHandlerHelper.get(serviceMeta);
            // 缓存中五RPCClientHandler
            if (handler == null) {
                handler = getRPCConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RPCConsumerHandlerHelper.put(serviceMeta, handler);
            }
            // 缓存中存在RPCClientHandler，但不活跃
            else if (!handler.getChannel().isActive()) {
                handler.close();
                handler = getRPCConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RPCConsumerHandlerHelper.put(serviceMeta, handler);
            }
            return handler.sendRequest(protocol, request.isAsync(), request.isOneway());
        }
        return null;
    }

    private RPCConsumerHandler getRPCConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()) {
                logger.info("connect rpc server {} on port {} success.", serviceAddress, port);
            } else {
                logger.error("connect rpc server {} on port {} failed.", serviceAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RPCConsumerHandler.class);
    }

}