package com.eajon.my.manager;

import com.github.eajon.enums.RequestType;

public class RxManager {

    private static RxManager mInstance = null;
    private static BaseManager requestManager;
    private static BaseManager downloadManager;
    private static BaseManager uploadManager;

    public RxManager() {
        requestManager = new BaseManager();
        uploadManager = new BaseManager();
        downloadManager = new BaseManager();
    }


    public static RxManager init() {
        if (mInstance == null) {
            synchronized (RxManager.class) {
                if (mInstance == null) {
                    mInstance = new RxManager();
                }
            }
        }
        return mInstance;
    }

    public static BaseManager get(RequestType requestType) {
        if (mInstance == null) {
            init();
        }
        switch (requestType) {
            case REQUEST:
                return requestManager;
            case UPLOAD:
                return uploadManager;
            case DOWNLOAD:
                return downloadManager;
            default:
                return downloadManager;
        }
    }

    public static BaseManager getDownloadManager() {
        if (mInstance == null) {
            init();
        }
        return downloadManager;
    }
}