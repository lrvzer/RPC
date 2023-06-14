package io.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.rpc.common.exception.SerializerException;
import io.rpc.common.utils.SerializationUtils;
import io.rpc.constant.RPCConstants;
import io.rpc.protocol.RPCProtocol;
import io.rpc.protocol.enumeration.RPCType;
import io.rpc.protocol.header.RPCHeader;
import io.rpc.protocol.request.RPCRequest;
import io.rpc.protocol.response.RPCResponse;
import io.rpc.serialization.api.Serialization;

import java.util.List;

public class RPCDecoder extends ByteToMessageDecoder implements RPCCodec {
    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RPCConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex();

        short magic = in.readShort();
        if (magic != RPCConstants.MAGIC) {
            throw new SerializerException("magic number is illegal, " + magic);
        }

        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        ByteBuf serializationTypeByteBuf = in.readBytes(SerializationUtils.MAX_SERIALIZATION_TYPE_COUNT);
        String serializationType = SerializationUtils.subString(serializationTypeByteBuf.toString(CharsetUtil.UTF_8));
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        RPCType msgTypeEnum = RPCType.findByType(msgType);
        if (msgTypeEnum == null) return;

        RPCHeader header = new RPCHeader();
        header.setMagic(magic);
        header.setMsgType(msgType);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLength);

        // TODO Serialization是扩展点
        Serialization serialization = getJdkSerialization();
        switch (msgTypeEnum) {
            case REQUEST:
                RPCRequest request = serialization.deserialize(data, RPCRequest.class);
                if (request != null) {
                    RPCProtocol<RPCRequest> protocol = new RPCProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;

            case RESPONSE:
                RPCResponse response = serialization.deserialize(data, RPCResponse.class);
                if (response != null) {
                    RPCProtocol<RPCResponse> protocol = new RPCProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO
                break;
        }
    }
}
