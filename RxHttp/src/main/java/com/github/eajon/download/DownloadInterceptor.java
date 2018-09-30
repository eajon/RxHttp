package com.github.eajon.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor{


    DownloadTask downloadTask;

    public DownloadInterceptor(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new DownloadResponseBody(response, downloadTask))
                .build();
    }
}
