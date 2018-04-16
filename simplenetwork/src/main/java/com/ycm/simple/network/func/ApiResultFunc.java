package com.ycm.simple.network.func;

import com.google.gson.Gson;
import com.ycm.simple.network.exception.BaseException;
import com.ycm.simple.network.exception.ServiceError;
import com.ycm.simple.network.model.ApiResult;
import com.ycm.simple.network.utils.GsonUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;


/**
 * Created by changmuyu on 2017/11/14.
 * Description:结果转换
 */

public class ApiResultFunc<T extends ApiResult> implements Function<ResponseBody, T> {
    protected Type type;
    protected Gson gson;

    public ApiResultFunc(Type type) {
        gson = GsonUtils.getGson(true);
        this.type = type;
    }

    @Override
    public T apply(@NonNull ResponseBody responseBody) throws Exception {
        String json = responseBody.string();
        T apiResult;
        //其实后台给这样的结果很不规范，最少应该给一个默认的值
        if (json.equals("{}")) {
            apiResult = create(type);
            apiResult.setResultCode(ApiResult.RESULT_SUCCESS_EMPTY);
        } else {
            apiResult = gson.fromJson(json, this.type);
            apiResult.setResultCode(ApiResult.RESULT_SUCCESS_NORMAL);
        }

        if (apiResult == null) {
            throw new BaseException(ServiceError.COMMON_ENTITY_NOT_EXIST.message,
                    ServiceError.COMMON_ENTITY_NOT_EXIST.code);
        } else {
            return apiResult;
        }
    }


    public static <T> T create(Type type) throws NoSuchMethodException {
        Class<?> rawType = getRawType(type);
        ObjectConstructor<T> constructor = newDefaultConstructor((Class<? super T>) rawType);
        if (constructor == null) {
            throw new NoSuchMethodException("没有默认构的造函数");
        }
        return constructor.construct();
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class.
            // Neal isn't either but suspects some pathological case related
            // to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            checkArgument(rawType instanceof Class);
            return (Class<?>) rawType;

        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();

        } else if (type instanceof TypeVariable) {
            // we could use the variable's bounds, but that won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;

        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);

        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <" + type + "> is of type " + className);
        }
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    private static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
        try {
            final Constructor<? super T> constructor = rawType.getDeclaredConstructor();

            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    try {
                        Object[] args = null;
                        return (T) constructor.newInstance(args);
                    } catch (InstantiationException e) {
                        throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Failed to invoke " + constructor + " with no args", e.getTargetException());
                    } catch (IllegalAccessException e) {
                        throw new AssertionError(e);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public interface ObjectConstructor<T> {

        /**
         * Returns a new instance.
         */
        public T construct();
    }
}
