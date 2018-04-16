package com.ycm.network.networkexample;

import android.app.Application;

import com.ycm.simple.network.NetworkClient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by changmuyu on 2018/4/16.
 * Description:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkClient.init(this);
        NetworkClient
                .getInstance()
                .setBaseUrl("https://api.github.com/")
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "token " + "e4edea431ed426ac36aaa95d1534884612879518")
                                .build();
                        return chain.proceed(request);
                    }
                });
    }
}
