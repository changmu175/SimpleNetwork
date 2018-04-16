package com.ycm.simple.network.interceptor;


import com.ycm.simple.network.model.HttpHeaders;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>描述：配置公共头部</p>
 * 作者： zhouyou<br>
 * 日期： 2016/12/19 16:46<br>
 * 版本： v2.0<br>
 */
public class HeadersInterceptor implements Interceptor {

    private HttpHeaders headers;

    public HeadersInterceptor(HttpHeaders headers) {
        this.headers = headers;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (headers.headersMap.isEmpty()) return chain.proceed(builder.build());
        try {
            for (Map.Entry<String, String> entry : headers.headersMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue()).build();
            }
        } catch (Exception e) {

        }
        return chain.proceed(builder.build());

    }
}
