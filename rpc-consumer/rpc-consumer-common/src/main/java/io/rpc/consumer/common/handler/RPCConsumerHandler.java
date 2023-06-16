package io.rpc.consumer.common.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class RPCConsumerHandler extends SimpleChannelInboundHandler<RPCProtocol<RPCResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RPCConsumerHandler.class);
    private volatile Channel channel;
    private SocketAddress remotePeer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = ctx.channel().remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCProtocol<RPCResponse> protocol) throws Exception {
        logger.info("服务消费者接收到的数据--->{}", JSON.toJSONString(protocol));
    }

    public void sendRequest(RPCProtocol<RPCRequest> protocol) {
        logger.info("服务消费者接收到的数据--->{}", JSON.toJSONString(protocol));
        this.channel.writeAndFlush(protocol);
    }

    public void close() {
        this.channel
                .writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }
}
