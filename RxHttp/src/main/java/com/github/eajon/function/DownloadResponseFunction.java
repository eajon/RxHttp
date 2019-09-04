package com.github.eajon.function;

import com.github.eajon.body.DownloadResponseBody;
import com.github.eajon.util.FileUtils;

import java.lang.reflect.Field;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class DownloadResponseFunction<T> implements Function<ResponseBody, Object> {


    @Override
    public Object apply(ResponseBody responseBody) throws Exception {
        Class c = responseBody.getClass();
        Field field = c.getDeclaredField("delegate");
        field.setAccessible(true);
        DownloadResponseBody downloadResponseBody = ( DownloadResponseBody ) field.get(responseBody);
        FileUtils.write2File(downloadResponseBody);
        return downloadResponseBody.getDownloadTask();
    }
}
