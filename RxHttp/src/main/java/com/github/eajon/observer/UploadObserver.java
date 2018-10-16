package com.github.eajon.observer;

public abstract class UploadObserver<T> extends HttpObserver<T>{
    public abstract void onCancel();

}
