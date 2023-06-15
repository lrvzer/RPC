package io.rpc.provider.common.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rpc.common.helper.RPCServiceHelper;
import io.rpc.common.threadpool.ServerThreadPool;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.enumeration.RPCStatus;
import io.rpc.protocol.enumeration.RPCType;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
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
        ServerThreadPool.submit(() -> {
            RPCHeader header = protocol.getHeader();
            // 将header中的消息类型设置为响应类型的消息
            header.setMsgType((byte) RPCType.RESPONSE.getType());
            RPCRequest request = protocol.getBody();
            logger.debug("Receive request " + header.getRequestId());

            // 构建响应协议数据
            RPCProtocol<RPCResponse> responseRPCProtocol = new RPCProtocol<>();
            // 响应信息
            RPCResponse response = new RPCResponse();

            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.isAsync());
                response.setOneway(request.isOneway());
                header.setStatus((byte) RPCStatus.SUCCESS.getCode());
            } catch (Throwable t) {
                response.setError(t.toString());
                header.setStatus((byte) RPCStatus.FAIL.getCode());
                logger.error("RPC Server handle request error", t);
            }
            responseRPCProtocol.setHeader(header);
            responseRPCProtocol.setBody(response);
            ctx.writeAndFlush(responseRPCProtocol).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.debug("Send response for request " + header.getRequestId());
                }
            });
        });
    }

    private Object handle(RPCRequest request) throws Throwable {
        String serviceKey = RPCServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        String methodName = request.getMethodName();
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), methodName));
        }


        Class<?> serviceClass = serviceBean.getClass();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug(serviceClass.getName());
        logger.debug(methodName);

        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                logger.debug(parameterTypes[i].getName());
            }
        }

        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                logger.debug(parameters[i].toString());
            }
        }

        return invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }

    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }
}
