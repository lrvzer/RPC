package io.rpc.test.consumer.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rpc.test.consumer.codec.init.RPCTestConsumerInitializer;

/**
 * @Description
 * @Author: Lrwei
 * @Date: 2023/6/14
 **/
public class RPCTestConsumer {
    public static void main(String[] args) {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(4);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new RPCTestConsumerInitializer())
            ;

            bootstrap.connect("127.0.0.1", 27880).sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            eventExecutors.shutdownGracefully();
        }
    }
}
