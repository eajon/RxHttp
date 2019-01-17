package com.github.eajon.interceptor;

import com.github.eajon.download.DownloadResponseBody;
import com.github.eajon.task.DownloadTask;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {


    DownloadTask downloadTask;
    boolean isStick;
    String eventId;

    public DownloadInterceptor(String eventId, boolean isStick, DownloadTask downloadTask) {
        this.eventId=eventId;
        this.isStick = isStick;
        this.downloadTask = downloadTask;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new DownloadResponseBody(response,eventId, isStick, downloadTask))
                .build();
    }
}
