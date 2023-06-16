package io.rpc.provider.common.server.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.rpc.codec.RPCDecoder;
import io.rpc.codec.RPCEncoder;
import io.rpc.provider.common.handler.RPCProviderHandler;
import io.rpc.provider.common.server.api.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseServer implements Server {

    private final Logger logger = LoggerFactory.getLogger(BaseServer.class);
    // 主机域名或者IP地址
    protected String host = "127.0.0.1";
    // 端口号
    protected int port = 27110;
    // 存储的是实体类关系
    protected Map<String, Object> handlerMap = new HashMap<>();

    private final String reflectType;

    public BaseServer(String serverAddress, String reflectType) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }

        this.reflectType = reflectType;
    }

    /**
     * 启动Netty服务
     */
    @Override
    public void startNettyServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel
                                    .pipeline()
                                    .addLast(new RPCDecoder())
                                    .addLast(new RPCEncoder())
                                    .addLast(new RPCProviderHandler(handlerMap, reflectType));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("server started on {}:{}", host, port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("RPC server start error", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
