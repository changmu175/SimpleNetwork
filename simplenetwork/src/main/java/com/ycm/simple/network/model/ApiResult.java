package com.ycm.simple.network.model;

/**
 * Created by changmuyu on 2017/11/24.
 * Description:
 */

public class ApiResult {
    //服务器成功处理了请求，但没有返回任何内容
    public static final int RESULT_SUCCESS_EMPTY = 205;
    //服务器成功处理了请求，并返回了结果
    public static final int RESULT_SUCCESS_NORMAL = 200;
    private int resultCode;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int type) {
        this.resultCode = type;
    }
}
