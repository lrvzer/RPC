package io.rpc.test.consumer.codec.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.rpc.codec.RPCDecoder;
import io.rpc.codec.RPCEncoder;
import io.rpc.test.consumer.codec.handler.RPCTestConsumerHandler;

public class RPCTestConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline
                .addLast(new RPCEncoder())
                .addLast(new RPCDecoder())
                .addLast(new RPCTestConsumerHandler());
    }
}
