package com.ycm.simple.network.func;

import com.ycm.simple.network.exception.BaseException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:处理异常的
 */

public class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        return Observable.error(BaseException.handleException(throwable));
    }
}
