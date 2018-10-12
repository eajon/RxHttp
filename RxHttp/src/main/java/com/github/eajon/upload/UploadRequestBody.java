package com.github.eajon.upload;


import com.github.eajon.RxHttp;
import com.github.eajon.util.LogUtils;

import java.io.IOException;

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

    private MultipartUploadTask multipartUploadTask;

    private boolean isStick;

    private String eventId;

    public UploadRequestBody(RequestBody requestBody, String eventId,boolean isStick, UploadTask uploadTask) {
        this.requestBody = requestBody;
        this.eventId=eventId;
        this.isStick = isStick;
        this.uploadTask = uploadTask;

    }

    public UploadRequestBody(RequestBody requestBody,String eventId, boolean isStick, UploadTask uploadTask, MultipartUploadTask multipartUploadTask) {
        this.requestBody = requestBody;
        this.eventId=eventId;
        this.isStick = isStick;
        this.uploadTask = uploadTask;
        this.multipartUploadTask = multipartUploadTask;
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
        //必须调用flush，否则最后一部分数据可能不会被写入
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
                writtenBytesCount += byteCount;
                uploadTask.setCurrentSize(writtenBytesCount);

                if (writtenBytesCount >= uploadTask.getTotalSize()) {
                    uploadTask.setState(UploadTask.State.FINISH);
                } else {
                    uploadTask.setState(UploadTask.State.LOADING);
                }
//                LogUtils.e(RxHttp.getConfig().getLogTag(),"upload"+ uploadTask.getProgress());
                if (multipartUploadTask != null) {
                    multipartUploadTask.sendBus(eventId,isStick);
                } else {
                    uploadTask.sendBus(eventId,isStick);
                }
            }
        };
    }
}
