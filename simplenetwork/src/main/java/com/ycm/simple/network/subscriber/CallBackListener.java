package com.ycm.simple.network.subscriber;

import android.content.Context;

import com.ycm.simple.network.callback.Listener;
import com.ycm.simple.network.exception.BaseException;
import com.ycm.simple.network.model.ApiResult;

import io.reactivex.annotations.NonNull;

public class CallBackListener<T extends ApiResult> extends BaseSubscriber<T> {
    public Listener<T> mCallBack;

    public CallBackListener(Context context, Listener<T> simpleCallback) {
        mCallBack = simpleCallback;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCallBack != null) {
            mCallBack.onStart();
        }
    }

    @Override
    public void onError(BaseException e) {
        if (mCallBack != null) {
            mCallBack.onFailure(e);
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        super.onNext(t);
        if (mCallBack != null) {
            mCallBack.onSuccess(t);
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (mCallBack != null) {
            mCallBack.onCompleted();
        }
    }
}
