package com.github.eajon.function;

import com.github.eajon.body.DownloadResponseBody;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.util.FileUtils;

import java.lang.reflect.Field;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * @author eajon
 */
public class DownloadResponseFunction<T extends ResponseBody> implements Function<ResponseBody, DownloadTask> {

    @SuppressWarnings(value = "unchecked")
    @Override
    public DownloadTask apply(ResponseBody responseBody) throws Exception {
        Class c = responseBody.getClass();
        Field field = c.getDeclaredField("delegate");
        field.setAccessible(true);
        DownloadResponseBody downloadResponseBody = ( DownloadResponseBody ) field.get(responseBody);
        FileUtils.write2File(downloadResponseBody);
        return downloadResponseBody.getDownloadTask();
    }
}
