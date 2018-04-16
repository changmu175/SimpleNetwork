package com.ycm.simple.network.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ycm.simple.network.func.GsonTypeAdapter;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;

/**
 * Created by changmuyu on 2018/4/16.
 * Description:
 */

public class GsonUtils {
    //设置为true表示保证数据安全，除去null值
    private static Gson gson;
    private static boolean needDataSafe = false;

    public static Gson getGson(boolean isNeedDataSafe) {
        if (needDataSafe = !isNeedDataSafe) {
            needDataSafe = isNeedDataSafe;
            if (gson != null) {
                gson = null;
            }
        }

        if (gson != null) {
            return gson;
        } else {
            return createGson(isNeedDataSafe);
        }

    }

    private static Gson createGson(boolean isNeedDataSafe) {
        if (isNeedDataSafe) {
            return createDataSafeGson();
        }

        return createGson();
    }

    private static Gson createGson() {
        return gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
    }

    private static Gson createDataSafeGson() {
        return gson = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, GsonTypeAdapter.BOOLEAN)
                .registerTypeAdapter(boolean.class, GsonTypeAdapter.BOOLEAN)
                .registerTypeAdapter(BigDecimal.class, GsonTypeAdapter.BIG_DECIMAL)
                .registerTypeAdapter(Byte.class, GsonTypeAdapter.BYTE)
                .registerTypeAdapter(byte.class, GsonTypeAdapter.BYTE)
                .registerTypeAdapter(Double.class, GsonTypeAdapter.DOUBLE)
                .registerTypeAdapter(double.class, GsonTypeAdapter.DOUBLE)
                .registerTypeAdapter(Float.class, GsonTypeAdapter.FLOAT)
                .registerTypeAdapter(float.class, GsonTypeAdapter.FLOAT)
                .registerTypeAdapter(Integer.class, GsonTypeAdapter.INTEGER)
                .registerTypeAdapter(Integer.class, GsonTypeAdapter.INTEGER)
                .registerTypeAdapter(Long.class, GsonTypeAdapter.LONG)
                .registerTypeAdapter(long.class, GsonTypeAdapter.LONG)
                .registerTypeAdapter(String.class, GsonTypeAdapter.STRING)
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
    }
}
