package com.ycm.simple.network.body;

import android.support.annotation.NonNull;

import com.ycm.simple.network.callback.UploadCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by changmuyu on 2017/11/14.
 * Description: 上传进度
 */

public class UploadRequestBody extends RequestBody {
    protected RequestBody delegate;
    protected UploadCallBack progressCallBack;
    protected CountingSink countingSink;

    public UploadRequestBody(UploadCallBack listener) {
        this.progressCallBack = listener;
    }

    public UploadRequestBody(RequestBody delegate, UploadCallBack progressCallBack) {
        this.delegate = delegate;
        this.progressCallBack = progressCallBack;
    }

    public void setRequestBody(RequestBody delegate) {
        this.delegate = delegate;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();

    }

    /**
     * 重写调用实际的响应体的contentLength
     */
    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;
        countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }


    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;
        private long contentLength = 0;  //总字节长度，避免多次调用contentLength()方法
        private long lastRefreshTime;  //最后一次刷新的时间

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            //获得contentLength的值，后续不再调用
            if (contentLength <= 0) contentLength = contentLength();
            //增加当前写入的字节数
            bytesWritten += byteCount;
            long curTime = System.currentTimeMillis();
            //每100毫秒刷新一次数据
            if (curTime - lastRefreshTime >= 100 || bytesWritten == contentLength) {
                progressCallBack.onResponseProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                lastRefreshTime = System.currentTimeMillis();
            }
        }
    }

}
