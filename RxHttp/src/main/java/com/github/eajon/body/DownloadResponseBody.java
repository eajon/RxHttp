package com.github.eajon.body;


import android.text.TextUtils;

import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.task.DownloadTask;

import java.io.IOException;

import io.reactivex.Observer;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


/**
 * 下载返回Body
 *
 * @author WENGYIJIONG
 */

public class DownloadResponseBody extends ResponseBody {


    private static final String FILENAME = "filename=";
    private Response originalResponse;
    private DownloadTask downloadTask;
    private Observer observer;
    private BufferedSource bufferedSource;
    private long time;
    private long secondBytesCount;

    public DownloadResponseBody(Response originalResponse, Observer observer, DownloadTask downloadTask) {
        this.originalResponse = originalResponse;
        this.observer = observer;
        this.downloadTask = downloadTask;
        getFileOriginalName();
    }

    private void getFileOriginalName() {
        String disposition = originalResponse.header("Content-Disposition");
        if (!TextUtils.isEmpty(disposition)) {
            assert disposition != null;
            int index = disposition.indexOf(FILENAME);
            if (index >= 0) {
                String name = disposition.substring(index + FILENAME.length());
                name = name.replace("UTF-8", "");
                name = name.replace("\"", "");
                if (!TextUtils.isEmpty(name)) {
                    downloadTask.setOriginalName(name);
                }
            }
        }
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }


    @Override
    public MediaType contentType() {
        assert originalResponse.body() != null;
        return originalResponse.body().contentType();
    }


    @Override
    public long contentLength() {
        assert originalResponse.body() != null;
        return originalResponse.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            assert originalResponse.body() != null;
            bufferedSource = Okio.buffer(source(originalResponse.body().source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long readBytesCount = downloadTask.getCurrentSize();
            long totalBytesCount = downloadTask.getTotalSize();

            @Override
            public long read(Buffer sink, final long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                secondBytesCount += byteCount != -1 ? bytesRead : 0;
                readBytesCount += bytesRead != -1 ? bytesRead : 0;
                if (totalBytesCount == 0) {
                    totalBytesCount = contentLength();
                }
                if (time == 0) {
                    time = System.currentTimeMillis();
                }
                downloadTask.setCurrentSize(readBytesCount);
                downloadTask.setTotalSize(totalBytesCount);
                long millis = System.currentTimeMillis() - time;
                if (millis >= 500L) {
                    downloadTask.setSpeed(secondBytesCount * 1000 * 2 / millis);
                    secondBytesCount = 0;
                    time = System.currentTimeMillis();
                    if (observer instanceof DownloadObserver) {
                        (( DownloadObserver ) observer).onProgress(downloadTask);
                    }
                }
                return bytesRead;
            }
        };
    }


}