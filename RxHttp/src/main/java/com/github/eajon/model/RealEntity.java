package com.github.eajon.model;

import java.io.Serializable;

/**
 *
 * @author eajon
 */

public class RealEntity<T> implements Serializable {
    //缓存的时间，以ms为单位
    private long cacheTime;
    //实际需要缓存的数据
    private T data;
    //缓存开始的时间
    private long updateDate;

    public RealEntity() {
    }

    public RealEntity(T data, long cacheTime) {
        this.cacheTime = cacheTime;
        this.data = data;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
}
