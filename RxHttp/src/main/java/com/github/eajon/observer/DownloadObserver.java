package com.github.eajon.observer;


import com.github.eajon.RxHttp;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.util.LogUtils;

import io.reactivex.disposables.Disposable;

public abstract class DownloadObserver<T extends DownloadTask> extends BaseObserver <T> {
    private static final String TAG = "DownloadObserver";
    DownloadTask downloadTask;
    Disposable disposable;

    public void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        downloadTask.setState(DownloadTask.State.WAITING);
        downloadTask.sendBus();
    }

    @Override
    public void onNext(T t) {
        downloadTask.setState(DownloadTask.State.FINISH);
        downloadTask.sendBus();
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        downloadTask.setState(DownloadTask.State.ERROR);
        downloadTask.sendBus();
        dispose();
        LogUtils.e(RxHttp.getConfig().getLogTag(), "error:" + e.toString());
        onError(e.toString());
    }

    @Override
    public void onComplete() {
        LogUtils.d(RxHttp.getConfig().getLogTag(), "onComplete");
        dispose();
    }


    protected abstract void onSuccess(T t);

    protected abstract void onError(String t);
}
