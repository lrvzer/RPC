package io.rpc.provider.common.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.enumeration.RPCType;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Netty 接收请求信息
 */
public class RPCProviderHandler extends SimpleChannelInboundHandler<RPCProtocol<RPCRequest>> {

    private final Logger logger = LoggerFactory.getLogger(RPCProviderHandler.class);
    private final Map<String, Object> handlerMap;

    public RPCProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCProtocol<RPCRequest> protocol) throws Exception {
        logger.info("RCP提供者收到的数据为：{}", JSONObject.toJSONString(protocol));
        logger.info("handlerMap中存放的数据如下所示：");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            logger.info("{}==={}", entry.getKey(), entry.getValue());
        }

        RPCHeader header = protocol.getHeader();
        RPCRequest request = protocol.getBody();
        // 将header中的消息类型设置为响应类型的消息
        header.setMsgType((byte) RPCType.RESPONSE.getType());

        // 构建响应协议数据
        RPCProtocol<RPCResponse> responseRPCProtocol = new RPCProtocol<>();

        // 响应信息
        RPCResponse response = new RPCResponse();
        response.setResult("数据交互成功");
        response.setAsync(request.isAsync());
        response.setOneway(request.isOneway());

        responseRPCProtocol.setHeader(header);
        responseRPCProtocol.setBody(response);
        ctx.writeAndFlush(responseRPCProtocol);
    }
}
