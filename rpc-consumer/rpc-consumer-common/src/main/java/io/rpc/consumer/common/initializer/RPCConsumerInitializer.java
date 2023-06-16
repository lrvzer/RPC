package io.rpc.consumer.common.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.rpc.codec.RPCDecoder;
import io.rpc.codec.RPCEncoder;
import io.rpc.consumer.common.handler.RPCConsumerHandler;

public class RPCConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new RPCEncoder())
                .addLast(new RPCDecoder())
                .addLast(new RPCConsumerHandler());
    }
}
