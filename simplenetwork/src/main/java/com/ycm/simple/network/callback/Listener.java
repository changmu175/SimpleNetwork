package com.ycm.simple.network.callback;

import com.ycm.simple.network.exception.BaseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by changmuyu on 2018/3/15.
 * Description:
 */

public abstract class Listener<T> implements IType {
    public abstract void onStart();

    public abstract void onCompleted();

    public abstract void onFailure(BaseException e);

    public abstract void onSuccess(T t);

    @Override
    public final Type getType() {
        //以下代码是通过泛型解析实际参数,泛型必须传
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) {
                throw new IllegalStateException("没有填写泛型参数");
            }
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }
}
