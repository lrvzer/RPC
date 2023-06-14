package io.rpc.protocol.header;

import io.rpc.constant.RPCConstants;
import io.rpc.protocol.enumeration.RPCType;

public class RPCHeaderFactory {
    public static RPCHeader getRequestHeader(String serializationType) {
        RPCHeader header = new RPCHeader();

        header.setMagic(RPCConstants.MAGIC);
        header.setRequestId(1212);
        header.setMsgType((byte) RPCType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
