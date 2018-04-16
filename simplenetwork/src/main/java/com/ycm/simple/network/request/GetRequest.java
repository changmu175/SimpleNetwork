package com.ycm.simple.network.request;


import com.ycm.simple.network.callback.RequestListener;
import com.ycm.simple.network.func.ApiResultFunc;
import com.ycm.simple.network.func.HttpResponseFunc;
import com.ycm.simple.network.model.ApiResult;
import com.ycm.simple.network.subscriber.CallBackListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:get请求类
 */

public class GetRequest extends BaseRequest<GetRequest> {
    public GetRequest(String url) {
        super(url);
    }

    public <T extends ApiResult> Disposable execute(final RequestListener<T> requestListener) {
        Observable<T> observable = build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiResultFunc<T>(requestListener.getType()))
                .onErrorResumeNext(new HttpResponseFunc<T>());
        return observable.subscribeWith(new CallBackListener(context, requestListener));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        return apiManager.get(url, params.urlParamsMap);
    }
}
