package io.rpc.constant;

public class RPCConstants {
    /**
     * 消息头，固定32个字节
     */
    public static final int HEADER_TOTAL_LEN = 32;
    /**
     * 魔数
     */
    public static final short MAGIC = 0x10;

    /**
     * REFLECT_TYPE_JDK
     */
    public static final String REFLECT_TYPE_JDK = "jdk";

    /**
     * REFLECT_TYPE_CGLIB
     */
    public static final String REFLECT_TYPE_CGLIB = "cglib";

}
