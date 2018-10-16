package com.github.eajon.observer;

public abstract class DownloadObserver<DownloadTask> extends HttpObserver <DownloadTask> {
    public abstract void onPause(DownloadTask downloadTask);
}
