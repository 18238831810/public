package com.cf.util.exception;


/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/3/28
 */

public class CFException extends RuntimeException {
    //是否从配置文件中拉取信息
    private boolean fromProperties=false;
    private String uri;

    public CFException(String message,boolean fromProperties) {
        this(message);
        this.fromProperties=fromProperties;
    }

    public CFException(String uri,String message,boolean fromProperties) {
        this(message);
        this.uri=uri;
        this.fromProperties=fromProperties;
    }
    public CFException() {
        super();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setFromProperties(boolean fromProperties) {
        this.fromProperties = fromProperties;
    }
    public boolean isFromProperties() {
        return fromProperties;
    }
    public CFException(String message) {
        super(message);
    }
}
