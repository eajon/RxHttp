package com.github.eajon.task;

import java.io.Serializable;


public class DownloadTask extends BaseTask implements Serializable {


    private String fileName;
    private String localUrl;//本地存储地址
    private long totalSize;//文件大小
    private long currentSize;//当前大小


    public DownloadTask(String fileName, String localUrl) {
        this.fileName = fileName;
        this.localUrl = localUrl;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalUrl() {
        return localUrl == null ? "" : localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }


    public int getProgress() {
        if (totalSize != 0) {
            float progress = (float) currentSize / (float) totalSize;
            return (int) (progress * 100);
        } else {
            return 0;
        }
    }



    @Override
    public String toString() {
        return "DownloadTask{" +
                "fileName='" + fileName + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                ", state=" + state +
                '}';
    }
}
