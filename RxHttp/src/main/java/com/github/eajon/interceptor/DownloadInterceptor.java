package com.github.eajon.interceptor;

import com.github.eajon.download.DownloadResponseBody;
import com.github.eajon.task.DownloadTask;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {


    DownloadTask downloadTask;
    boolean isStick;
    String tag;

    public DownloadInterceptor(String tag, boolean isStick, DownloadTask downloadTask) {
        this.tag = tag;
        this.isStick = isStick;
        this.downloadTask = downloadTask;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new DownloadResponseBody(response, tag, isStick, downloadTask))
                .build();
    }
}
