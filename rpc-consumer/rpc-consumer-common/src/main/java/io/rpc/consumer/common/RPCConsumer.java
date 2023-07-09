package io.rpc.consumer.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rpc.consumer.common.future.RPCFuture;
import io.rpc.consumer.common.handler.RPCConsumerHandler;
import io.rpc.consumer.common.initializer.RPCConsumerInitializer;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.request.RPCRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCConsumer {
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
        eventLoopGroup.shutdownGracefully();
    }

    public RPCFuture sendRequest(RPCProtocol<RPCRequest> protocol) throws Exception {
        // TODO 暂时写死，后续引入注册中心时，从注册中心获取
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        String key = serviceAddress.concat("_").concat(String.valueOf(port));
        RPCConsumerHandler handler = handlerMap.get(key);
        if (handler == null) {
            handler = getRPCConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        }
        // 缓存中存在RPCClientHandler，但不活跃
        else if (!handler.getChannel().isActive()) {
            handler.close();
            handler = getRPCConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        }
        return handler.sendRequest(protocol);
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