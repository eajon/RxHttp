package com.github.eajon.observer;



import com.github.eajon.RxHttp;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadTask;
import com.github.eajon.util.LogUtils;

import io.reactivex.disposables.Disposable;

public abstract class UploadObserver<T> extends BaseObserver <T> {

    private static final String TAG = "UploadObserver";

    private UploadTask uploadTask;

    private MultipartUploadTask multipartUploadTask;

    public void setMultipartUploadTask(MultipartUploadTask multipartUploadTask) {
        this.multipartUploadTask = multipartUploadTask;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        if (uploadTask != null) {
            uploadTask.setState(UploadTask.State.LOADING);
            uploadTask.sendBus();
        }

        if (multipartUploadTask != null) {
            multipartUploadTask.setState(MultipartUploadTask.State.LOADING);
            multipartUploadTask.sendBus();
        }
    }


    @Override
    public void onNext(T value) {
        onSuccess(value);
        if (uploadTask != null) {
            uploadTask.setState(UploadTask.State.FINISH);
            uploadTask.sendBus();
        }


        if (multipartUploadTask != null) {
            multipartUploadTask.setState(MultipartUploadTask.State.FINISH);
            multipartUploadTask.sendBus();
        }

    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(RxHttp.getConfig().getLogTag(), "error:" + e.toString());
        onError(e.toString());
        if (uploadTask != null) {
            uploadTask.setState(UploadTask.State.ERROR);
            uploadTask.sendBus();
        }
        if (multipartUploadTask != null) {
            multipartUploadTask.setState(MultipartUploadTask.State.ERROR);
            multipartUploadTask.sendBus();
        }
        dispose();
    }

    @Override
    public void onComplete() {
        LogUtils.d(RxHttp.getConfig().getLogTag(), "onComplete");
        dispose();

    }

    protected abstract void onSuccess(T t);

    protected abstract void onError(String t);
}
