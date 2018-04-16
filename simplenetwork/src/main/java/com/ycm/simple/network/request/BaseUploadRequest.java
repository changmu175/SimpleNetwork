package com.ycm.simple.network.request;

import com.ycm.simple.network.body.UploadRequestBody;
import com.ycm.simple.network.callback.UploadCallBack;
import com.ycm.simple.network.model.RequestParams;
import com.ycm.simple.network.utils.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:上传请求
 */

public abstract class BaseUploadRequest<R extends BaseUploadRequest> extends BaseRequest<R> {
    private static final String TAG = BaseUploadRequest.class.getSimpleName();
    protected String string;                                   //上传的文本内容
    protected MediaType mediaType;                                   //上传的文本内容
    protected String json;                                     //上传的Json
    protected byte[] bs;                                       //上传的字节数据
    protected Object object;                                   //上传的对象
    protected RequestBody requestBody;                         //自定义的请求体

    public BaseUploadRequest(String url) {
        super(url);
    }

    public R params(File file, String fileName, UploadCallBack responseCallBack) {
        params.put("file", file, fileName, responseCallBack);
        return (R) this;
    }

    public R params(String key, File file, String fileName, UploadCallBack responseCallBack) {
        params.put(key, file, fileName, responseCallBack);
        return (R) this;
    }

    protected Observable<ResponseBody> uploadFilesWithParts() {
        List<MultipartBody.Part> parts = new ArrayList<>();
        //拼接参数键值对
        for (Map.Entry<String, String> mapEntry : params.urlParamsMap.entrySet()) {
            String value = mapEntry.getValue() == null ? "" : mapEntry.getValue();
            parts.add(MultipartBody.Part.createFormData(mapEntry.getKey(), value));
        }

        if (!params.objectParamsMap.isEmpty()) {
            for (Map.Entry<String, Object> mapEntry : params.objectParamsMap.entrySet()) {
                Object value = mapEntry.getValue() == null ? "" : mapEntry.getValue();
                parts.addAll(getParamsList(mapEntry.getKey(), value));
            }
        }

        //拼接文件
        for (Map.Entry<String, List<RequestParams.FileWrapper>> entry : params.fileParamsMap.entrySet()) {
            List<RequestParams.FileWrapper> fileValues = entry.getValue();
            for (RequestParams.FileWrapper fileWrapper : fileValues) {
                MultipartBody.Part part = addFile(entry.getKey(), fileWrapper);
                parts.add(part);
            }
        }
        return apiManager.uploadFiles(url, parts);
    }

    protected Observable<ResponseBody> uploadFilesWithBodys() {
        Map<String, RequestBody> mBodyMap = new HashMap<>();
        //拼接参数键值对
        for (Map.Entry<String, String> mapEntry : params.urlParamsMap.entrySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), mapEntry.getValue());
            mBodyMap.put(mapEntry.getKey(), body);
        }
        //拼接文件
        for (Map.Entry<String, List<RequestParams.FileWrapper>> entry : params.fileParamsMap.entrySet()) {
            List<RequestParams.FileWrapper> fileValues = entry.getValue();
            for (RequestParams.FileWrapper fileWrapper : fileValues) {
                RequestBody requestBody = getRequestBody(fileWrapper);
                UploadRequestBody uploadRequestBody =
                        new UploadRequestBody(requestBody, fileWrapper.responseCallBack);
                mBodyMap.put(entry.getKey(), uploadRequestBody);
            }
        }
        return apiManager.uploadFiles(url, mBodyMap);
    }

    //文件方式
    private MultipartBody.Part addFile(String key, RequestParams.FileWrapper fileWrapper) {
        RequestBody requestBody = getRequestBody(fileWrapper);
        NetworkUtils.checkNotNull(requestBody, "requestBody==null fileWrapper.file must is File/InputStream/byte[]");
        //包装RequestBody，在其内部实现上传进度监听
        if (fileWrapper.responseCallBack != null) {
            UploadRequestBody uploadRequestBody =
                    new UploadRequestBody(requestBody, fileWrapper.responseCallBack);
            return MultipartBody.Part.createFormData(key, fileWrapper.fileName, uploadRequestBody);
        } else {
            return MultipartBody.Part.createFormData(key, fileWrapper.fileName, requestBody);
        }
    }

    private RequestBody getRequestBody(RequestParams.FileWrapper fileWrapper) {
        RequestBody requestBody = null;
        if (fileWrapper.file instanceof File) {
            requestBody = RequestBody.create(fileWrapper.contentType, (File) fileWrapper.file);
        }
        return requestBody;
    }
}
