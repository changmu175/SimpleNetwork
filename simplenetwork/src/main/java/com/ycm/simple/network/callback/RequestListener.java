package com.ycm.simple.network.callback;

import com.ycm.simple.network.model.ApiResult;

/**
 * Created by changmuyu on 2018/3/15.
 * Description:这个类是为了适配原有的网络框架所创建
 * 由于最开始没有想到如何适配原有的网络请求的代码，所以创建了
 * 两者作用一致
 */

public abstract class RequestListener<T extends ApiResult> extends Listener<T> {

    @Override
    public void onStart() {
    }

    @Override
    public void onCompleted() {

    }
}
