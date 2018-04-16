package com.ycm.simple.network.exception;

import android.net.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.HttpException;
import retrofit2.Response;


/**
 * Created by changmuyu on 2017/11/14.
 * Description:网络请求异常
 */

public class BaseException extends Exception {
    public int code;
    public String message;

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(String message, int code) {
        this.code = code;
        this.message = message;
    }

    public static BaseException handleException(Throwable e) {
        BaseException ex;
        if (e instanceof BaseException) {
            return (BaseException) e;
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            Response response = httpException.response();
            ResponseBody errorBody = response.errorBody();

            if (errorBody == null) {
                ex = new BaseException(httpException.getMessage(), httpException.code());
                ex.message = httpException.getMessage();
                return ex;
            }

            BufferedSource bufferedSource = errorBody.source();
            InputStream inputStream = bufferedSource.inputStream();
            String errorJson = convertStreamToString(inputStream);
            Gson gson = new Gson();
            BaseException baseException = gson.fromJson(errorJson, BaseException.class);
            if (baseException == null) {
                return new BaseException(e.getMessage(), ERROR.UNKNOWN);
            }
            errorBody.close();
            return new BaseException(baseException.getMessage(), baseException.getCode());
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new BaseException("解析错误", ERROR.PARSE_ERROR);
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new BaseException("类型转换错误", ERROR.CAST_ERROR);
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new BaseException("证书验证失败", ERROR.SSL_ERROR);
            return ex;
        } else if (e instanceof ConnectTimeoutException
                || e instanceof ConnectException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof UnknownHostException
                ) {
            ex = new BaseException("您的网络好像有点问题", ERROR.TIMEOUT_ERROR);
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new BaseException(e.getMessage(), ERROR.NULL_POINTER_EXCEPTION);
            ex.message = "NullPointerException";
            return ex;
        } else if (e instanceof FileNotFoundException) {
            ex = new BaseException("没有找到文件，可能是没有权限", ERROR.FILE_NOT_FOUND_EXCEPTION);
            return ex;
        } else if (e instanceof SecurityException) {
            ex = new BaseException("未获取相关权限", ERROR.SECURITY_EXCEPTION);
            return ex;
        } else {
            ex = new BaseException(e.getMessage(), ERROR.UNKNOWN);
            return ex;
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = UNKNOWN + 1;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = PARSE_ERROR + 1;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = NETWORK_ERROR + 1;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = HTTP_ERROR + 1;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

        /**
         * 调用错误
         */
        public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = INVOKE_ERROR + 1;
        /**
         * 请求取消
         */
        public static final int REQUEST_CANCEL = CAST_ERROR + 1;
        /**
         * 未知主机错误
         */
        public static final int UNKNOWN_HOST_ERROR = REQUEST_CANCEL + 1;

        /**
         * 空指针错误
         */
        public static final int NULL_POINTER_EXCEPTION = UNKNOWN_HOST_ERROR + 1;
        /**
         * 文件未找到
         */
        public static final int FILE_NOT_FOUND_EXCEPTION = NULL_POINTER_EXCEPTION + 1;

        /**
         * 文件未找到
         */
        public static final int SECURITY_EXCEPTION = FILE_NOT_FOUND_EXCEPTION + 1;
    }
}