package com.github.eajon.observer;

import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadTask;
import io.reactivex.disposables.Disposable;

public abstract class UploadObserver<T> extends BaseObserver <T> {

    private static final String TAG = "UploadObserver";

    public abstract void onCancel();


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
            multipartUploadTask.setState(UploadTask.State.LOADING);
            multipartUploadTask.sendBus();
        }
    }


    @Override
    public void onNext(T value) {
        super.onNext(value);
        if (uploadTask != null) {
            uploadTask.setState(UploadTask.State.FINISH);
            uploadTask.sendBus();
        }
        if (multipartUploadTask != null) {
            multipartUploadTask.setState(UploadTask.State.FINISH);
            multipartUploadTask.sendBus();
        }

    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if (uploadTask != null) {
            uploadTask.setState(UploadTask.State.ERROR);
            uploadTask.sendBus();
        }
        if (multipartUploadTask != null) {
            multipartUploadTask.setState(UploadTask.State.ERROR);
            multipartUploadTask.sendBus();
        }
    }

}
