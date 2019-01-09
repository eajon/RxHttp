package com.github.eajon.download;


import com.github.eajon.task.DownloadTask;

import java.io.IOException;

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
 *  @author WENGYIJIONG
 */

public class DownloadResponseBody extends ResponseBody {


    Response originalResponse;

    DownloadTask downloadTask;

    private BufferedSource bufferedSource;

    private boolean isStick;
    private String eventId;
    private long time;
    private long secondBytesCount;

    public DownloadResponseBody(Response originalResponse, String eventId, boolean isStick, DownloadTask downloadTask) {
        this.originalResponse = originalResponse;
        this.eventId = eventId;
        this.isStick = isStick;
        this.downloadTask = downloadTask;
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }


    @Override
    public MediaType contentType() {
        return originalResponse.body().contentType();
    }


    @Override
    public long contentLength() {
        return originalResponse.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
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
                long millis = System.currentTimeMillis() - time;
                if (millis >= 500) {
                    downloadTask.setSpeed(secondBytesCount * 1000 / millis);
                    secondBytesCount = 0;
                    time = System.currentTimeMillis();
                }
                downloadTask.setCurrentSize(readBytesCount);
                downloadTask.setTotalSize(totalBytesCount);
                downloadTask.sendBus(eventId, isStick);
                return bytesRead;
            }
        };
    }


}