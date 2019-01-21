package com.eajon.my.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseManager {

    private static BaseManager mInstance = null;

    public HashMap<String, CompositeDisposable> mMaps;

    public BaseManager() {
        mMaps = new HashMap<>();
    }

    public static BaseManager get() {
        if (mInstance == null) {
            synchronized (BaseManager.class) {
                if (mInstance == null) {
                    mInstance = new BaseManager();
                }
            }
        }
        return mInstance;
    }

    public void add(String tag, Disposable disposable) {
        if (null == tag) {
            return;
        }
        //tag下的一组或一个请求，用来处理一个页面的所以请求或者某个请求
        //设置一个相同的tag就行就可以取消当前页面所有请求或者某个请求了
        CompositeDisposable compositeDisposable = mMaps.get(tag);
        if (compositeDisposable == null) {
            CompositeDisposable compositeDisposableNew = new CompositeDisposable();
            compositeDisposableNew.add(disposable);
            mMaps.put(tag, compositeDisposableNew);
        } else {
            compositeDisposable.add(disposable);
        }
    }


    public void remove(String tag) {
        if (null == tag) {
            return;
        }
        if (!mMaps.isEmpty()) {
            mMaps.remove(tag);
        }
    }

    public void cancel(String tag) {
        if (null == tag) {
            return;
        }
        if (mMaps.isEmpty()) {
            return;
        }
        if (null == mMaps.get(tag)) {
            return;
        }
        if (!mMaps.get(tag).isDisposed()) {
            mMaps.get(tag).dispose();
            mMaps.remove(tag);
        }
    }


    public void cancel(String... tags) {
        if (null == tags) {
            return;
        }
        for (String tag : tags) {
            cancel(tag);
        }
    }


    public void cancelAll() {
        if (mMaps.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<String, CompositeDisposable>> it = mMaps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CompositeDisposable> entry = it.next();
            CompositeDisposable disposable = entry.getValue();
            if (null != disposable) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                    it.remove();
                }
            }
        }
    }


}
