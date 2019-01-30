package com.github.eajon.upload;


import com.github.eajon.observer.UploadObserver;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.task.UploadTask;

import java.io.IOException;

import io.reactivex.Observer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 上传RequestBody
 *
 * @author WENGYIJIONG
 */
public class UploadRequestBody extends RequestBody {

    //实际的待包装请求体
    private final RequestBody requestBody;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;

    private UploadTask uploadTask;

    private MultiUploadTask multiUploadTask;

    private Observer observer;


    private long time;
    private long secondBytesCount;


    public UploadRequestBody(RequestBody requestBody, Observer observer, UploadTask uploadTask) {
        this.requestBody = requestBody;
        this.observer = observer;
        this.uploadTask = uploadTask;

    }

    public UploadRequestBody(RequestBody requestBody, Observer observer, UploadTask uploadTask, MultiUploadTask multiUploadTask) {
        this.requestBody = requestBody;
        this.observer = observer;
        this.uploadTask = uploadTask;
        this.multiUploadTask = multiUploadTask;
    }


    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    /**
     * 重写writeTo
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (null == bufferedSink) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long writtenBytesCount = 0L;
//            //总字节长度，避免多次调用contentLength()方法
//            long totalBytesCount = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                //增加当前写入的字节数
                secondBytesCount += byteCount;
                writtenBytesCount += byteCount;
                if (time == 0) {
                    time = System.currentTimeMillis();
                }
                long millis = System.currentTimeMillis() - time;
                if (millis >= 500) {
                    uploadTask.setSpeed(secondBytesCount * 1000 / millis);
                    if (multiUploadTask != null) {
                        multiUploadTask.setSpeed(secondBytesCount * 1000 / millis);
                    }
                    secondBytesCount = 0;
                    time = System.currentTimeMillis();
                }


                uploadTask.setCurrentSize(writtenBytesCount);

                if (writtenBytesCount >= uploadTask.getTotalSize()) {
                    uploadTask.setState(UploadTask.State.FINISH);
                } else {
                    uploadTask.setState(UploadTask.State.LOADING);
                }
                if (observer instanceof UploadObserver) {
                    if (multiUploadTask != null) {
                        (( UploadObserver ) observer).onProgress(multiUploadTask);
                    } else {
                        (( UploadObserver ) observer).onProgress(uploadTask);
                    }
                }
            }
        };
    }
}
