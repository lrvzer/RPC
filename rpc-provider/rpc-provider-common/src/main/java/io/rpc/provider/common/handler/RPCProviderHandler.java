package io.rpc.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RPCProviderHandler extends SimpleChannelInboundHandler<Object> {

    private final Logger logger = LoggerFactory.getLogger(RPCProviderHandler.class);
    private final Map<String, Object> handlerMap;

    public RPCProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("RCP提供者收到的数据为：{}", msg.toString());
        logger.info("handlerMap中存放的数据如下所示：");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            logger.info(entry.getKey() + "===" + entry.getValue());
        }
        // 直接返回数据
        ctx.writeAndFlush(msg);
    }
}
