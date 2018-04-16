package com.ycm.simple.network.callback;

import java.util.List;

/**
 * Created by changmuyu on 2017/11/14.
 * Description: 下载进度回调
 */

public abstract class DownloadCallBack<T> extends Listener<T> {
    public DownloadCallBack() {
    }

    @Override
    public void onSuccess(T response) {

    }

    public abstract void update(long bytesRead, long contentLength, boolean done);

    public abstract void onComplete(String path);

    @Override
    public void onCompleted() {

    }
}
