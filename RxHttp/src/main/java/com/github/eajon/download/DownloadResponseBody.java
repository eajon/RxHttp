package com.github.eajon.download;



import com.github.eajon.RxHttp;
import com.github.eajon.util.LogUtils;

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
 * 作者：Tailyou （祝文飞）
 * <p>
 * 时间：2016/5/30 16:19
 * <p>
 * 邮箱：tailyou@163.com
 * <p>
 * 描述：
 */

public class DownloadResponseBody extends ResponseBody {


    Response originalResponse;

    DownloadTask downloadTask;

    private BufferedSource bufferedSource;

    public DownloadResponseBody(Response originalResponse, DownloadTask downloadTask) {
        this.originalResponse = originalResponse;
        this.downloadTask = downloadTask;
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
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                readBytesCount += bytesRead != -1 ? bytesRead : 0;
//                LogUtils.e(RxHttp.getConfig().getLogTag(),readBytesCount);
                if (totalBytesCount == 0) {
                    totalBytesCount = contentLength();
                }
                downloadTask.setCurrentSize(readBytesCount);
                downloadTask.setTotalSize(totalBytesCount);
                downloadTask.sendBus();
                return bytesRead;
            }
        };
    }


}