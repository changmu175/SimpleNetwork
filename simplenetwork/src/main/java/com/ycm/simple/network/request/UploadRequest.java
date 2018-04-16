package com.ycm.simple.network.request;

import com.ycm.simple.network.callback.ProgressListener;
import com.ycm.simple.network.callback.RequestListener;
import com.ycm.simple.network.func.ApiResultFunc;
import com.ycm.simple.network.func.HttpResponseFunc;
import com.ycm.simple.network.model.ApiResult;
import com.ycm.simple.network.subscriber.CallBackListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by changmuyu on 2017/11/29.
 * Description:
 */

public class UploadRequest extends BaseUploadRequest<UploadRequest> {

    public UploadRequest(String url) {
        super(url);
    }

    public <T extends ApiResult> Disposable execute(final ProgressListener<T> listener) {
        Observable<T> observable = build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiResultFunc<T>(listener.getType()))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                })
                .onErrorResumeNext(new HttpResponseFunc<T>());
        return observable.subscribeWith(new CallBackListener<>(context, listener));
    }

    public <T extends ApiResult> Disposable execute(final RequestListener<T> simpleCallback) {
        Observable<T> observable = build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiResultFunc<T>(simpleCallback.getType()))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                })
                .onErrorResumeNext(new HttpResponseFunc<T>());
        return observable.subscribeWith(new CallBackListener<>(context, simpleCallback));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        return uploadFilesWithParts();
    }


}
