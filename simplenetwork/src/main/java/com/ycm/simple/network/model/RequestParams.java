package com.ycm.simple.network.model;


import com.ycm.simple.network.callback.UploadCallBack;

import org.json.JSONArray;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * Created by changmuyu on 2017/11/14.
 * Description:参数
 */

public class RequestParams implements Serializable {
    /**
     * 普通的键值对参数
     */
    public LinkedHashMap<String, String> urlParamsMap;

    public LinkedHashMap<String, Object> objectParamsMap;
    /**
     * 文件的键值对参数
     */
    public LinkedHashMap<String, List<FileWrapper>> fileParamsMap;

    public RequestParams() {
        init();
    }

    public RequestParams(String key, String value) {
        init();
        put(key, value);
    }

    private void init() {
        urlParamsMap = new LinkedHashMap<>();
        fileParamsMap = new LinkedHashMap<>();
        objectParamsMap = new LinkedHashMap<>();
    }

    public void put(RequestParams params) {
        if (params != null) {
            if (params.urlParamsMap != null && !params.urlParamsMap.isEmpty()) {
                urlParamsMap.putAll(params.urlParamsMap);
            }

            if (params.fileParamsMap != null && !params.fileParamsMap.isEmpty()) {
                fileParamsMap.putAll(params.fileParamsMap);
            }

            if (params.objectParamsMap != null && !params.objectParamsMap.isEmpty()) {
                objectParamsMap.putAll(params.objectParamsMap);
            }
        }
    }

    public void put(Map<String, String> params) {
        if (params == null || params.isEmpty()) return;
        urlParamsMap.putAll(params);
    }

    public void put(String key, String value) {
        urlParamsMap.put(key, value);
    }

    public void put(String key, BigDecimal value) {
        if (value != null) {
            urlParamsMap.put(key, String.valueOf(value));
        }
    }

    public void put(String key, Long value) {
        if (value != null) {
            urlParamsMap.put(key, String.valueOf(value));
        }
    }

    public void put(String key, Boolean value) {
        if (value != null) {
            urlParamsMap.put(key, String.valueOf(value));
        }
    }

    public void put(String key, Integer value) {
        if (value != null) {
            urlParamsMap.put(key, String.valueOf(value));
        }
    }

    public void put(String key, Byte value) {
        if (value != null) {
            urlParamsMap.put(key, String.valueOf(value));
        }
    }

    public void put(String key, Object value) {
        objectParamsMap.put(key, value);
    }

    public void put(String value, double key) {
        urlParamsMap.put(value, String.valueOf(key));
    }

    public void put(String key, long value) {
        urlParamsMap.put(key, String.valueOf(value));
    }

    public void put(String key, JSONArray value) {
        urlParamsMap.put(key, value.toString());
    }

    public void put(String key, int value) {
        urlParamsMap.put(key, String.valueOf(value));
    }

    public void put(String key, byte value) {
        urlParamsMap.put(key, String.valueOf(value));
    }

    public void put(String key, boolean value) {
        urlParamsMap.put(key, String.valueOf(value));
    }

    public <T extends File> void put(String key, T file, String fileName, UploadCallBack responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    public <T> void put(String key, T content, String fileName, MediaType contentType, UploadCallBack responseCallBack) {
        if (key != null) {
            List<FileWrapper> fileWrappers = fileParamsMap.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParamsMap.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper(content, fileName, contentType, responseCallBack));
        }
    }

    public void removeUrl(String key) {
        urlParamsMap.remove(key);
    }


    public void remove(String key) {
        removeUrl(key);
//        removeFile(key);
    }

    public void clear() {
        urlParamsMap.clear();
//        fileParamsMap.clear();
    }

    private MediaType guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        path = path.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = /*"video/mp4"*/"application/octet-stream";
        }
        return MediaType.parse(contentType);
    }


    /**
     * 文件类型的包装类
     */
    public static class FileWrapper<T> {
        public T file;//可以是
        public String fileName;
        public MediaType contentType;
        public long fileSize;
        public UploadCallBack responseCallBack;

        public FileWrapper(T file, String fileName, MediaType contentType, UploadCallBack responseCallBack) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            if (file instanceof File) {
                this.fileSize = ((File) file).length();
            } else if (file instanceof byte[]) {
                this.fileSize = ((byte[]) file).length;
            }
            this.responseCallBack = responseCallBack;
        }

        @Override
        public String toString() {
            return "FileWrapper{" + "countent=" + file + ", fileName='" + fileName + ", contentType=" + contentType + ", fileSize=" + fileSize + '}';
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParamsMap.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }
}