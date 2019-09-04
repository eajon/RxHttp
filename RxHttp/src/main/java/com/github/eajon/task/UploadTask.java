package com.github.eajon.task;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author eajon
 */
public class UploadTask extends BaseTask implements Serializable {


    private String name;//上传的参数名
    private File file;//文件
    private String fileName;//上传的文件名
    private long currentSize;//当前上传的大小
    private long totalSize;//总文件大小


    public UploadTask(String name, File file) {
        this.name = name;
        this.file = file;
        this.fileName = file.getName();
        this.totalSize = file.length();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public int getProgress() {
        if (totalSize != 0) {
            float progress = ( float ) currentSize / ( float ) totalSize;
            return ( int ) (progress * 100);
        } else {
            return 0;
        }
    }

    @Override
    public float getAverageSpeed() {
        return getAverageSpeed(currentSize);
    }

    @Override
    public String getAverageSpeedFormat() {
        return getAverageSpeedFormat(currentSize);
    }

    @Override
    public String getAverageSpeedFormat(TimeUnit timeUnit) {
        return getAverageSpeedFormat(currentSize, timeUnit);
    }


    @Override
    public String toString() {
        return "UploadTask{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", file=" + file +
                ", currentSize=" + currentSize +
                ", totalSize=" + totalSize +
                '}';
    }
}
