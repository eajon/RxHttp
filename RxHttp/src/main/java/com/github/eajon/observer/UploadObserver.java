package com.github.eajon.observer;

import com.github.eajon.task.BaseTask;


public abstract class UploadObserver<T> extends HttpObserver<T> {
    /*callback on MainThread*/
    public abstract void onCancel();

    /*callback on IO thread,if you want to do something on ui thread,please use runOnUiThread method*/
    public abstract void onProgress(BaseTask baseTask);


}
