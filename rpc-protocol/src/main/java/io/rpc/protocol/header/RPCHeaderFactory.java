package io.rpc.protocol.header;

import io.rpc.common.id.IDFactory;
import io.rpc.constant.RPCConstants;
import io.rpc.protocol.enumeration.RPCType;

public class RPCHeaderFactory {
    public static RPCHeader getRequestHeader(String serializationType) {
        RPCHeader header = new RPCHeader();
        Long requestID = IDFactory.getID();
        header.setMagic(RPCConstants.MAGIC); //  short MAGIC = 0x10;
        header.setRequestId(requestID); //
        header.setMsgType((byte) RPCType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
