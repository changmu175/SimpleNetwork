package com.ycm.simple.network.subscriber;

import android.content.Context;
import android.text.TextUtils;

import com.ycm.simple.network.callback.DownloadCallBack;
import com.ycm.simple.network.callback.Listener;
import com.ycm.simple.network.exception.BaseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * <p>描述：定义一个下载的订阅者</p>
 * 作者： zhouyou<br>
 * 日期： 2016/12/19 16:35<br>
 * 版本： v2.0<br>
 */
public class DownloadSubscriber<T extends ResponseBody> extends BaseSubscriber<T> {
    private Context context;
    private String path;
    private String name;
    public Listener mCallBack;
    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";
    private static String PNG_CONTENTTYPE = "image/png";
    private static String JPG_CONTENTTYPE = "image/jpg";
    private static String TEXT_CONTENTTYPE = "text/html; charset=utf-8";
    private static String fileSuffix = "";
    private long lastRefreshUiTime;

    public DownloadSubscriber(Context context, String path, String name, Listener callBack) {
        super(context);
        this.path = path;
        this.name = name;
        this.mCallBack = callBack;
        this.context = context;
        this.lastRefreshUiTime = System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCallBack != null) {
            mCallBack.onStart();
        }
    }

    @Override
    public final void onComplete() {

    }

    @Override
    public void onError(final BaseException e) {
        finalonError(e);
    }

    @Override
    public void onNext(T responseBody) {
        writeResponseBodyToDisk(path, name, context, responseBody);
    }

    private String getName(String name, ResponseBody body) {
        if (!TextUtils.isEmpty(name)) {
            String type;
            if (!name.contains(".")) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    type = mediaType.toString();
                    if (type.equals(APK_CONTENTTYPE)) {
                        fileSuffix = ".apk";
                    } else if (type.equals(PNG_CONTENTTYPE)) {
                        fileSuffix = ".png";
                    } else if (type.equals(JPG_CONTENTTYPE)) {
                        fileSuffix = ".jpg";
                    } else {
                        fileSuffix = "." + mediaType.subtype();
                    }
                }
                name = name + fileSuffix;
            }
        } else {
            name = System.currentTimeMillis() + fileSuffix;
        }
        return name;
    }

    private String getPath(Context context, String path, String name) {
        if (path == null) {
            path = context.getExternalFilesDir(null) + File.separator + name;
        } else {
            File file = new File(path);
            if (!file.exists() && !file.mkdirs()) {
                finalonError(new BaseException("目录初始化失败", BaseException.ERROR.SECURITY_EXCEPTION));
            }
            path = path + File.separator + name;
            path = path.replaceAll("//", "/");
        }
        return path;
    }

    private void writeResponseBodyToDisk(String path, String name, Context context, ResponseBody body) {
        name = getName(name, body);
        path = getPath(context, path, name);
        try {
            File futureStudioIconFile = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                final Listener callBack = mCallBack;
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //下载进度
                    float progress = fileSizeDownloaded * 1.0f / fileSize;
                    long curTime = System.currentTimeMillis();
                    //每200毫秒刷新一次数据,防止频繁更新进度
                    if (curTime - lastRefreshUiTime >= 200 || progress == 1.0f) {
                        if (callBack != null) {
                            updateDownloadProgress(fileSizeDownloaded, fileSize, callBack);
                        }
                        lastRefreshUiTime = System.currentTimeMillis();
                    }
                }
                outputStream.flush();
                downloadComplete(path, callBack);
            } catch (IOException e) {
                finalonError(new BaseException(e.getMessage()));
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            finalonError(new BaseException(e.getMessage()));
        }
    }

    private void updateDownloadProgress(long fileSizeDownloaded, final long fileSize, final Listener callBack) {
        final long finalFileSizeDownloaded = fileSizeDownloaded;
        Observable.just(finalFileSizeDownloaded)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (callBack instanceof DownloadCallBack) {
                            ((DownloadCallBack) callBack).update(finalFileSizeDownloaded,
                                    fileSize, finalFileSizeDownloaded == fileSize);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }

    private void downloadComplete(String path, final Listener callBack) {
        if (callBack != null) {
            final String finalPath = path;
            Observable.just(finalPath).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(@NonNull String s) throws Exception {
                    if (callBack instanceof DownloadCallBack) {
                        ((DownloadCallBack) callBack).onComplete(finalPath);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {

                }
            });
        }
    }

    private void finalonError(final BaseException e) {

        if (mCallBack == null) {
            return;
        }
        Observable.just(e).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BaseException>() {
            @Override
            public void accept(@NonNull BaseException e) throws Exception {
                if (mCallBack != null) {
                    mCallBack.onFailure(e);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });
    }
}
