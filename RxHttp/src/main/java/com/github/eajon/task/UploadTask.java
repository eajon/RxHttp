package com.github.eajon.task;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;

public class UploadTask extends BaseTask implements Serializable {

    private String fileName;
    private File file;
    private long currentSize;
    private long totalSize;




    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UploadTask(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.totalSize = file.length();
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
        return "UploadTask{" +
                "fileName='" + fileName + '\'' +
                ", file=" + file +
                ", currentSize=" + currentSize +
                ", totalSize=" + totalSize +
                ", state=" + state +
                '}';
    }
}
