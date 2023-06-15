package io.rpc.test.consumer.codec.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeaderFactory;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCTestConsumerHandler extends SimpleChannelInboundHandler<RPCProtocol<RPCResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(RPCTestConsumerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("发送数据开始...");
        RPCProtocol<RPCRequest> protocol = new RPCProtocol<>();
        protocol.setHeader(RPCHeaderFactory.getRequestHeader("jdk"));

        RPCRequest request = new RPCRequest();
        request.setClassName("io.rpc.test.api.DemoService");
        request.setGroup("lrw");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"lrw"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);

        protocol.setBody(request);
        logger.info("服务消费者发送的数据：{}", JSON.toJSONString(protocol));
        ctx.writeAndFlush(protocol);
        logger.info("发送数据完毕...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCProtocol<RPCResponse> protocol) throws Exception {
        logger.info("服务消费者接收到的数据：{}", JSON.toJSONString(protocol));
    }
}
