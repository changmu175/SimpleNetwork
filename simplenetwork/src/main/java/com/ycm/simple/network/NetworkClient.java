package com.ycm.simple.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ycm.simple.network.model.HttpHeaders;
import com.ycm.simple.network.model.RequestParams;
import com.ycm.simple.network.request.DownloadRequest;
import com.ycm.simple.network.request.GetRequest;
import com.ycm.simple.network.request.PostRequest;
import com.ycm.simple.network.request.UploadRequest;
import com.ycm.simple.network.utils.NetworkUtils;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.disposables.Disposable;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by changmuyu on 2017/11/15.
 * Description:
 */
public class NetworkClient {
    public static final int DEFAULT_MILLISECONDS = 60000;             //默认的超时时间
    private static final int DEFAULT_RETRY_COUNT = 3;                 //默认重试次数
    private static final int DEFAULT_RETRY_INCREASE_DELAY = 500;      //默认重试叠加时间
    private static final int DEFAULT_RETRY_DELAY = 500;               //默认重试延时
    private int mRetryIncreaseDelay = DEFAULT_RETRY_INCREASE_DELAY;   //叠加延迟
    private String mBaseUrl;                                          //全局BaseUrl
    private HttpHeaders mCommonHeaders;                               //全局公共请求头
    private RequestParams mCommonRequestParams;
    private OkHttpClient.Builder okHttpClientBuilder;                 //okhttp请求的客户端
    private Retrofit.Builder retrofitBuilder;                         //Retrofit请求Builder
    private volatile static NetworkClient singleton = null;

    private NetworkClient() {
        SimpleTrustManger trustManager = new SimpleTrustManger();
        TrustManager[] mTrustManagers = new TrustManager[]{trustManager};
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.sslSocketFactory(getSSLSocketFactory(mTrustManagers), trustManager);
        okHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("com.ycm.network------", message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(logging).build();
        }
        retrofitBuilder = new Retrofit.Builder();
    }

    public static NetworkClient getInstance() {
        testInitialize();
        if (singleton == null) {
            synchronized (NetworkClient.class) {
                if (singleton == null) {
                    singleton = new NetworkClient();
                }
            }
        }
        return singleton;
    }

    private static WeakReference<Application> contextWeakReference;

    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     */
    public static void init(Application app) {
        contextWeakReference = new WeakReference<>(app);
    }


    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        testInitialize();
        return contextWeakReference.get();
    }

    private static void testInitialize() {
        if (contextWeakReference.get() == null)
            throw new ExceptionInInitializerError("请先在全局Application中调用 NetworkClient.init() 初始化！");
    }

    public static OkHttpClient getOkHttpClient() {
        return getInstance().okHttpClientBuilder.build();
    }

    public static Retrofit getRetrofit() {
        return getInstance().retrofitBuilder.build();
    }

    /**
     * 对外暴露 OkHttpClient,方便自定义
     */
    public static OkHttpClient.Builder getOkHttpClientBuilder() {
        return getInstance().okHttpClientBuilder;
    }

    /**
     * 对外暴露 Retrofit,方便自定义
     */
    public static Retrofit.Builder getRetrofitBuilder() {
        return getInstance().retrofitBuilder;
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory(TrustManager[] trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class SimpleTrustManger implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 全局读取超时时间
     */
    public NetworkClient setReadTimeOut(long readTimeOut) {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局写入超时时间
     */
    public NetworkClient setWriteTimeOut(long writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局连接超时时间
     */
    public NetworkClient setConnectTimeout(long connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public NetworkClient setRetryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0)
            throw new IllegalArgumentException("retryIncreaseDelay must > 0");
        mRetryIncreaseDelay = retryIncreaseDelay;
        return this;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public int getRetryIncreaseDelay() {
        return getInstance().mRetryIncreaseDelay;
    }

    /**
     * 获取全局公共请求头
     */
    public RequestParams getCommonRequestParams() {
        return mCommonRequestParams;
    }

    public NetworkClient addCommonToken(String token) {
        if (mCommonRequestParams == null) {
            mCommonRequestParams = new RequestParams();
        }
        mCommonRequestParams.put("token", token);
        return this;
    }

    public NetworkClient addAppVersion(String version) {
        if (mCommonRequestParams == null) {
            mCommonRequestParams = new RequestParams();
        }
        mCommonRequestParams.put("versionCode", version);
        return this;
    }

    /**
     * 获取全局公共请求头
     */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /**
     * 添加全局公共请求参数
     */
    public NetworkClient addCommonHttpParams(RequestParams commonHeaders) {
        if (mCommonRequestParams == null) {
            mCommonRequestParams = new RequestParams();
        }
        mCommonRequestParams.put(commonHeaders);
        return this;
    }

    /**
     * 添加全局公共请求参数
     */
    public NetworkClient addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) {
            mCommonHeaders = new HttpHeaders();
        }
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /**
     * 添加全局拦截器
     */
    public NetworkClient addInterceptor(Interceptor interceptor) {
        okHttpClientBuilder.addInterceptor(NetworkUtils.checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 添加全局网络拦截器
     */
    public NetworkClient addNetworkInterceptor(Interceptor interceptor) {
        okHttpClientBuilder.addNetworkInterceptor(NetworkUtils.checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 全局设置请求的连接池
     */
    public NetworkClient setOkconnectionPool(ConnectionPool connectionPool) {
        okHttpClientBuilder.connectionPool(NetworkUtils.checkNotNull(connectionPool, "connectionPool == null"));
        return this;
    }

    /**
     * 全局为Retrofit设置自定义的OkHttpClient
     */
    public NetworkClient setOkclient(OkHttpClient client) {
        retrofitBuilder.client(NetworkUtils.checkNotNull(client, "client == null"));
        return this;
    }

    /**
     * 全局设置Converter.Factory,默认GsonConverterFactory.create()
     */
    public NetworkClient addConverterFactory(Converter.Factory factory) {
        retrofitBuilder.addConverterFactory(NetworkUtils.checkNotNull(factory, "factory == null"));
        return this;
    }

    /**
     * 全局设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    public NetworkClient addCallAdapterFactory(CallAdapter.Factory factory) {
        retrofitBuilder.addCallAdapterFactory(NetworkUtils.checkNotNull(factory, "factory == null"));
        return this;
    }

    /**
     * 全局设置Retrofit callbackExecutor
     */
    public NetworkClient setCallbackExecutor(Executor executor) {
        retrofitBuilder.callbackExecutor(NetworkUtils.checkNotNull(executor, "executor == null"));
        return this;
    }

    /**
     * 全局设置Retrofit对象Factory
     */
    public NetworkClient setCallFactory(okhttp3.Call.Factory factory) {
        retrofitBuilder.callFactory(NetworkUtils.checkNotNull(factory, "factory == null"));
        return this;
    }

    /**
     * 全局设置baseurl
     */
    public NetworkClient setBaseUrl(String baseUrl) {
        mBaseUrl = NetworkUtils.checkNotNull(baseUrl, "baseUrl == null");
        return this;
    }

    /**
     * 获取全局baseurl
     */
    public String getBaseUrl() {
        return getInstance().mBaseUrl;
    }

    /**
     * get请求
     */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /**
     * post请求
     */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

    public static UploadRequest uploadRequest(String url) {
        return new UploadRequest(url);
    }

    public static DownloadRequest downLoad(String url) {
        return new DownloadRequest(url);
    }

    /**
     * 取消订阅
     */
    public static void cancelSubscription(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
