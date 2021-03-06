package com.github.eajon.util;


import android.text.TextUtils;

import com.github.eajon.body.DownloadResponseBody;
import com.github.eajon.task.DownloadTask;

import java.io.File;
import java.io.IOException;

import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author eajon
 */
public class FileUtils {

    /**
     * 写入文件
     *
     * @param responseBody
     * @throws IOException
     */
    public static void write2File(DownloadResponseBody responseBody) throws IOException {
        DownloadTask downloadTask = responseBody.getDownloadTask();
        File file = new File(downloadTask.getLocalDir(), checkFileName(downloadTask.getName(), downloadTask.getOriginalName()));
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
            long bufferSize = 8 * 1024L;
            source = responseBody.source();
            while ((len = source.read(buffer, bufferSize)) != -1) {
                sink.emit();
                total += len;
                downloadTask.setCurrentSize(total);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            Util.closeQuietly(source);
            Util.closeQuietly(sink);
        }

    }

    private static String checkFileName(String saveFileName, String originalName) {
        if (!TextUtils.isEmpty(saveFileName)) {
            return saveFileName;
        } else if (!TextUtils.isEmpty(originalName)) {
            return originalName;
        } else {
            return System.currentTimeMillis() + ".file";
        }
    }
}
