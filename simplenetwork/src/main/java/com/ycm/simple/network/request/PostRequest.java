package com.ycm.simple.network.request;


import com.ycm.simple.network.callback.RequestListener;
import com.ycm.simple.network.func.ApiResultFunc;
import com.ycm.simple.network.func.HttpResponseFunc;
import com.ycm.simple.network.model.ApiResult;
import com.ycm.simple.network.model.RequestParams;
import com.ycm.simple.network.subscriber.CallBackListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:post请求类
 */

public class PostRequest extends BaseRequest<PostRequest> {

    public PostRequest(String url) {
        super(url);
    }

    @Override
    public PostRequest params(RequestParams requestParams) {
        params.put(requestParams);
        return this;
    }

    public <T extends ApiResult> Disposable execute(final RequestListener<T> simpleCallback) {
        Observable<T> observable = build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiResultFunc<T>(simpleCallback.getType()))
                .onErrorResumeNext(new HttpResponseFunc<T>());
        return observable.subscribeWith(new CallBackListener(context, simpleCallback));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        String json = gson.toJson(params.urlParamsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return apiManager.postJson(url, body);
    }
}
