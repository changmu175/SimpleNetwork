package com.ycm.simple.network.exception;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:服务端返回的请求枚举
 */

public enum ServiceError {
    COMMON_ENTITY_NOT_EXIST(100003, "无法找到指定的数据"),
    COMMON_ACTION_NO_AUTHORITY(100004, "该操作没有权限完成"),
    COMMON_JSON_ILLEGAL(100009, "json数据转换错误"),
    COMMON_XML_ILLEGAL(1000010, "xml数据转换错误"),
    MYBATIS_ERROR(300101, "数据库请求失败"),
    KVSTORE_ERROR(301001, "缓存请求失败"),
    NETWORK_ERROR(302001, "网络请求失败");

    public int code;
    public String message;

    ServiceError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
