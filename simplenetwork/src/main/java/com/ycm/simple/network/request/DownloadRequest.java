package com.ycm.simple.network.request;

import com.ycm.simple.network.callback.DownloadCallBack;
import com.ycm.simple.network.func.HttpResponseFunc;
import com.ycm.simple.network.subscriber.DownloadSubscriber;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:下载请求
 */

public class DownloadRequest extends BaseRequest<DownloadRequest> {
    private String savePath;
    private String saveName;

    public DownloadRequest(String url) {
        super(url);
    }

    /**
     * 下载文件路径<br>
     */
    public DownloadRequest savePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    /**
     * 下载文件名称<br>
     */
    public DownloadRequest saveName(String saveName) {
        this.saveName = saveName;
        return this;
    }

    public <T> Disposable execute(final DownloadCallBack<T> simpleCallback) {
        Observable<ResponseBody> observable = build().generateRequest()
                .map(new Function<ResponseBody, ResponseBody>() {
                    @Override
                    public ResponseBody apply(ResponseBody responseBody) throws Exception {
                        return responseBody;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onErrorResumeNext(new HttpResponseFunc<ResponseBody>());
        return observable.subscribeWith(new DownloadSubscriber(context, savePath, saveName, simpleCallback));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        return apiManager.downloadFile(url);
    }
}
