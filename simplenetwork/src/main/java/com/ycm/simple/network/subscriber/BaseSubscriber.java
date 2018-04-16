package com.ycm.simple.network.subscriber;

import android.content.Context;

import com.ycm.simple.network.exception.BaseException;
import com.ycm.simple.network.utils.NetworkUtils;

import java.lang.ref.WeakReference;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by changmuyu on 2017/11/14.
 * Description:订阅基类
 */

public abstract class BaseSubscriber<T> extends DisposableObserver<T> {
    public WeakReference<Context> contextWeakReference;

    public BaseSubscriber() {
    }

    @Override
    protected void onStart() {
        if (contextWeakReference != null
                && contextWeakReference.get() != null
                && !NetworkUtils.isNetworkAvailable(contextWeakReference.get())) {
            onComplete();
        }
    }


    public BaseSubscriber(Context context) {
        if (context != null) {
            contextWeakReference = new WeakReference<>(context);
        }
    }

    @Override
    public void onNext(@NonNull T t) {
    }

    @Override
    public final void onError(Throwable e) {
        if (e instanceof BaseException) {
            onError((BaseException) e);
        } else {
            onError(BaseException.handleException(e));
        }
    }

    @Override
    public void onComplete() {
    }


    public abstract void onError(BaseException e);

}
