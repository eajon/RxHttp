package com.github.eajon.observer;

import com.github.eajon.task.DownloadTask;

public abstract class DownloadObserver extends HttpObserver<DownloadTask> {
    /*callback on MainThread*/
    public abstract void onPause(DownloadTask downloadTask);

    /*callback on IO thread,if you want to do something on ui thread,please use runOnUiThread method*/
    public abstract void onProgress(DownloadTask downloadTask);
}
