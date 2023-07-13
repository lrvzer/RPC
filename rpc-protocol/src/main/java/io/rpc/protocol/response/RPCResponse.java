package io.rpc.protocol.response;

import io.rpc.protocol.base.RPCMessage;

public class RPCResponse extends RPCMessage {

    private static final long serialVersionUID = 4103062588985633577L;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
