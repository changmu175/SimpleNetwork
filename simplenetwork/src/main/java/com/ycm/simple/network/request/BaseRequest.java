package com.ycm.simple.network.request;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ycm.simple.network.NetworkClient;
import com.ycm.simple.network.api.ApiService;
import com.ycm.simple.network.interceptor.HeadersInterceptor;
import com.ycm.simple.network.model.HttpHeaders;
import com.ycm.simple.network.model.RequestParams;
import com.ycm.simple.network.utils.GsonUtils;
import com.ycm.simple.network.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:请求基类
 */

public abstract class BaseRequest<R extends BaseRequest> {
    protected String baseUrl;
    protected String url;
    protected boolean isSyncRequest;                                       //是否是同步请求
    protected long readTimeOut;                                            //读超时
    protected long writeTimeOut;                                           //写超时
    protected long connectTimeout;                                         //链接超时
    protected final List<Interceptor> networkInterceptors = new ArrayList<>();
    protected HttpHeaders headers = new HttpHeaders();                     //添加的header
    protected RequestParams params;                                           //添加的param
    protected Retrofit retrofit;
    protected ApiService apiManager;                                       //通用的的api接口
    protected OkHttpClient okHttpClient;
    protected Context context;
    protected HttpUrl httpUrl;
    protected HostnameVerifier hostnameVerifier;
    protected List<Converter.Factory> converterFactories = new ArrayList<>();
    protected List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
    protected final List<Interceptor> interceptors = new ArrayList<>();
    protected final Gson gson;

    public BaseRequest(String url) {
        this.url = url;
        context = NetworkClient.getContext();
        gson = GsonUtils.getGson(true);
        NetworkClient config = NetworkClient.getInstance();
        this.baseUrl = config.getBaseUrl();
        if (!TextUtils.isEmpty(this.baseUrl)) {
            httpUrl = HttpUrl.parse(baseUrl);
        }

        //默认添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) {
            headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
        }
        //默认添加 User-Agent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) {
            headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        }

        params = new RequestParams();
        if (config.getCommonRequestParams() != null) {
            params.put(config.getCommonRequestParams());
        }

        if (config.getCommonHeaders() != null) {
            headers.put(config.getCommonHeaders());
        }
    }

    public RequestParams getParams() {
        return this.params;
    }

    public R readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    public R writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    public R connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return (R) this;
    }

    public R baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!TextUtils.isEmpty(this.baseUrl))
            httpUrl = HttpUrl.parse(baseUrl);
        return (R) this;
    }

    public R addInterceptor(Interceptor interceptor) {
        interceptors.add(NetworkUtils.checkNotNull(interceptor, "interceptor == null"));
        return (R) this;
    }

    public R addNetworkInterceptor(Interceptor interceptor) {
        networkInterceptors.add(NetworkUtils.checkNotNull(interceptor, "interceptor == null"));
        return (R) this;
    }

    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    public R addConverterFactory(Converter.Factory factory) {
        converterFactories.add(factory);
        return (R) this;
    }

    /**
     * 设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    public R addCallAdapterFactory(CallAdapter.Factory factory) {
        adapterFactories.add(factory);
        return (R) this;
    }

    /**
     * https的全局访问规则
     */
    public R hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return (R) this;
    }

    /**
     * 添加头信息
     */
    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    /**
     * 添加头信息
     */
    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    /**
     * 移除头信息
     */
    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    /**
     * 移除所有头信息
     */
    public R removeAllHeaders() {
        headers.clear();
        return (R) this;
    }

    /**
     * 设置参数
     */
    public R params(RequestParams params) {
        this.params.put(params);
        return (R) this;
    }

    public R params(String key, String value) {
        params.put(key, value);
        return (R) this;
    }

    public R params(String key, int value) {
        params.put(key, String.valueOf(value));
        return (R) this;
    }

    public R params(String key, long value) {
        params.put(key, String.valueOf(value));
        return (R) this;
    }


    public R removeParam(String key) {
        params.remove(key);
        return (R) this;
    }

    public R removeAllParams() {
        params.clear();
        return (R) this;
    }


    public R syncRequest(boolean syncRequest) {
        this.isSyncRequest = syncRequest;
        return (R) this;
    }


    /**
     * 根据当前的请求参数，生成对应的OkClient
     */
    private OkHttpClient.Builder createOkHttpClient() {
        if (readTimeOut <= 0 && writeTimeOut <= 0 && connectTimeout <= 0 && headers.isEmpty()) {
            return NetworkClient.getOkHttpClientBuilder();
        } else {
            final OkHttpClient.Builder newClientBuilder = NetworkClient.getOkHttpClient().newBuilder();
            if (readTimeOut > 0) {
                newClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            }
            if (writeTimeOut > 0) {
                newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            }
            if (connectTimeout > 0) {
                newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            }
            if (hostnameVerifier != null) {
                newClientBuilder.hostnameVerifier(hostnameVerifier);
            }

            if (networkInterceptors.size() > 0) {
                for (Interceptor interceptor : networkInterceptors) {
                    newClientBuilder.addNetworkInterceptor(interceptor);
                }
            }

            newClientBuilder.addInterceptor(new HeadersInterceptor(headers));
            return newClientBuilder;
        }
    }

    /**
     * 根据当前的请求参数，生成对应的Retrofit
     */
    private Retrofit.Builder generateRetrofit() {
        if (converterFactories.isEmpty() && adapterFactories.isEmpty()) {
            return NetworkClient.getRetrofitBuilder().baseUrl(baseUrl);
        } else {
            final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            if (!converterFactories.isEmpty()) {
                for (Converter.Factory converterFactory : converterFactories) {
                    retrofitBuilder.addConverterFactory(converterFactory);
                }
            } else {
                //获取全局的对象重新设置
                List<Converter.Factory> listConverterFactory = NetworkClient.getRetrofit().converterFactories();
                for (Converter.Factory factory : listConverterFactory) {
                    retrofitBuilder.addConverterFactory(factory);
                }
            }
            if (!adapterFactories.isEmpty()) {
                for (CallAdapter.Factory adapterFactory : adapterFactories) {
                    retrofitBuilder.addCallAdapterFactory(adapterFactory);
                }
            } else {
                //获取全局的对象重新设置
                List<CallAdapter.Factory> listAdapterFactory = NetworkClient.getRetrofit().callAdapterFactories();
                for (CallAdapter.Factory factory : listAdapterFactory) {
                    retrofitBuilder.addCallAdapterFactory(factory);
                }
            }
            return retrofitBuilder.baseUrl(baseUrl);
        }
    }


    protected R build() {
        OkHttpClient.Builder okHttpClientBuilder = createOkHttpClient();
        final Retrofit.Builder retrofitBuilder = generateRetrofit();
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());//增加RxJava2CallAdapterFactory
        okHttpClient = okHttpClientBuilder.build();
        retrofitBuilder.client(okHttpClient);
        retrofit = retrofitBuilder.build();
        apiManager = retrofit.create(ApiService.class);
        return (R) this;
    }

    public List<MultipartBody.Part> getParamsList(String key, Object value) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (value instanceof List) {
            List list = (List) value;
            int listSize = list.size();
            for (int nestedValueIndex = 0; nestedValueIndex < listSize; nestedValueIndex++) {
                String nestedKey = String.format(Locale.US, "%s[%d]", key, nestedValueIndex);
                Object nestedValue = list.get(nestedValueIndex);
                parts.addAll(getParamsList(nestedKey, nestedValue));
            }
        } else if (value instanceof Map) {
            Map map = (Map) value;
            List list = new ArrayList<Object>(map.keySet());
            // Ensure consistent ordering in query string
            if (list.size() > 0 && list.get(0) instanceof Comparable) {
                Collections.sort(list);
            }
            for (Object nestedKey : list) {
                if (nestedKey instanceof String) {
                    Object nestedValue = map.get(nestedKey);
                    if (nestedValue != null) {
                        parts.addAll(getParamsList((String) nestedKey, nestedValue));
                    }
                }
            }
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            int arrayLength = array.length;
            for (int nestedValueIndex = 0; nestedValueIndex < arrayLength; nestedValueIndex++) {
                String nestedKey = String.format(Locale.US, "%s[%d]", key, nestedValueIndex);
                Object nestedValue = array[nestedValueIndex];
                parts.addAll(getParamsList(nestedKey, nestedValue));
            }
        } else if (value instanceof Set) {
            Set set = (Set) value;
            for (Object nestedValue : set) {
                parts.addAll(getParamsList(key, nestedValue));
            }
        } else {
            parts.add(MultipartBody.Part.createFormData(key, value.toString()));
        }
        return parts;
    }

    protected abstract Observable<ResponseBody> generateRequest();
}
