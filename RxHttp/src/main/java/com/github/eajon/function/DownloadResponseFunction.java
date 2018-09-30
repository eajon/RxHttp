package com.github.eajon.function;

import com.github.eajon.download.DownloadTask;
import com.github.eajon.util.FileUtils;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class DownloadResponseFunction implements Function<ResponseBody, Object> {

    DownloadTask downloadTask;

    public DownloadResponseFunction(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }



    @Override
    public Object apply(ResponseBody responseBody) throws Exception {
        downloadTask.setState(DownloadTask.State.LOADING);
        FileUtils.writeFile(responseBody, downloadTask);
        return downloadTask;
    }
}
