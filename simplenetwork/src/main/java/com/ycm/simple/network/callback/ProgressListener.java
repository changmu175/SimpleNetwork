package com.ycm.simple.network.callback;

import com.ycm.simple.network.exception.BaseException;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:进度弹窗
 */
public abstract class ProgressListener<T> extends Listener<T> {


    public ProgressListener() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onFailure(BaseException e) {

    }
}
