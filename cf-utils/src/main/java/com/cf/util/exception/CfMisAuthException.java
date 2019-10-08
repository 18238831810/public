package com.cf.util.exception;


/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author frank
 * @date 2019/4/11
 */

public class CfMisAuthException extends RuntimeException {
    //是否从配置文件中拉取信息
    private boolean fromProperties=false;

    public CfMisAuthException(String message, boolean fromProperties) {
        this(message);
        this.fromProperties=fromProperties;
    }
    public CfMisAuthException() {
        super();
    }

    public void setFromProperties(boolean fromProperties) {
        this.fromProperties = fromProperties;
    }
    public boolean isFromProperties() {
        return fromProperties;
    }
    public CfMisAuthException(String message) {
        super(message);
    }
}
