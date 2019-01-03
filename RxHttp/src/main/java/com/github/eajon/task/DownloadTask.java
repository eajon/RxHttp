package com.github.eajon.task;

import java.io.Serializable;
import java.math.BigDecimal;


public class DownloadTask extends BaseTask implements Serializable {


    private String name;//存储在本地的文件名
    private String localUrl;//本地存储地址
    private long totalSize;//文件大小
    private long currentSize;//当前大小




    public DownloadTask(String name, String localUrl) {
        this.name = name;
        this.localUrl = localUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                '}';
    }
}
