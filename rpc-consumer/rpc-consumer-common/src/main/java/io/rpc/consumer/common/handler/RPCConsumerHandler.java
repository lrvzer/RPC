package io.rpc.consumer.common.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rpc.consumer.common.context.RPCContext;
import io.rpc.consumer.common.future.RPCFuture;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCConsumerHandler extends SimpleChannelInboundHandler<RPCProtocol<RPCResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RPCConsumerHandler.class);
    private volatile Channel channel;
    private SocketAddress remotePeer;

    private static final Map<Long, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

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
        if (protocol == null) {
            return;
        }
        logger.info("服务消费者接收到的数据--->{}", JSON.toJSONString(protocol));

        RPCHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        RPCFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null) {
            rpcFuture.done(protocol);
        }
    }

    public RPCFuture sendRequest(RPCProtocol<RPCRequest> protocol, boolean async, boolean oneway) {
        logger.info("服务消费者接收到的数据--->{}", JSON.toJSONString(protocol));
        return oneway ? this.sendRequestOneway(protocol) : (async ? sendRequestAsync(protocol) : this.sendRequestSync(protocol));
    }

    public RPCFuture sendRequestSync(RPCProtocol<RPCRequest> protocol) {
        RPCFuture rpcFuture = this.getRPCFuture(protocol);
        this.channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    public RPCFuture sendRequestAsync(RPCProtocol<RPCRequest> protocol) {
        logger.info("sendRequestAsync.......");
        RPCFuture rpcFuture = this.getRPCFuture(protocol);
        // 如果异步调用，则将RPCFuture放入RPCContext
        RPCContext.getContext().setRPCFuture(rpcFuture);
        channel.writeAndFlush(protocol);
        return null;
    }

    public RPCFuture sendRequestOneway(RPCProtocol<RPCRequest> protocol) {
        channel.writeAndFlush(protocol);
        return null;
    }

    private RPCFuture getRPCFuture(RPCProtocol<RPCRequest> protocol) {
        RPCFuture rpcFuture = new RPCFuture(protocol);
        RPCHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
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
