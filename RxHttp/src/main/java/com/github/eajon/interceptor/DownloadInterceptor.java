package com.github.eajon.interceptor;

import com.github.eajon.download.DownloadResponseBody;
import com.github.eajon.task.DownloadTask;

import java.io.IOException;

import io.reactivex.Observer;
import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {


    DownloadTask downloadTask;
    private Observer observer;

    public DownloadInterceptor(Observer observer, DownloadTask downloadTask) {
        this.observer = observer;
        this.downloadTask = downloadTask;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new DownloadResponseBody(response, observer, downloadTask))
                .build();
    }
}
