package com.github.eajon.util;


import com.github.eajon.task.DownloadTask;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


public class FileUtils {

    /**
     * 写入文件
     *
     * @param responseBody
     * @param downloadTask
     * @throws IOException
     */
    public static void write2File(ResponseBody responseBody, DownloadTask downloadTask) throws IOException {
        File file = new File(downloadTask.getLocalUrl());
        BufferedSource source = null;
        BufferedSink sink = null;
        try {
            //创建文件夹
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.length() != downloadTask.getCurrentSize()) {
                sink = Okio.buffer(Okio.sink(file));
            } else {
                sink = Okio.buffer(Okio.appendingSink(file));
            }

            Buffer buffer = sink.buffer();
            long total = downloadTask.getCurrentSize();
            long len;
            int bufferSize = 64 * 1024;
            source = responseBody.source();
            while ((len = source.read(buffer, bufferSize)) != -1) {
                sink.emit();
                total += len;
                downloadTask.setCurrentSize(total);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (source != null) {
                IOUtils.close(source);
            }
            if (sink != null) {
                IOUtils.close(sink);
            }
        }

    }
}
