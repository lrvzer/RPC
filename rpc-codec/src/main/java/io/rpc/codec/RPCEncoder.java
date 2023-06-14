package io.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.rpc.common.utils.SerializationUtils;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.serialization.api.Serialization;

import java.nio.charset.StandardCharsets;

public class RPCEncoder extends MessageToByteEncoder<RPCProtocol<Object>> implements RPCCodec {
    @Override
    protected void encode(ChannelHandlerContext ctx, RPCProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        RPCHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());

        // 序列化
        String serializationType = header.getSerializationType();
        Serialization serialization = getJdkSerialization();

        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));
        byte[] data = serialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
