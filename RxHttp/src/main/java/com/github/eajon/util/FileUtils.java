package com.github.eajon.util;



import com.github.eajon.RxHttp;
import com.github.eajon.download.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;


public class FileUtils {
    /**
     * 写入文件
     *
     * @param responseBody
     * @param downloadTask
     * @throws IOException
     */
    public static void writeFile(ResponseBody responseBody, DownloadTask downloadTask) throws IOException {
        File file = new File(downloadTask.getLocalUrl());
        RandomAccessFile randomAccessFile = null;
        FileChannel channelOut = null;
        InputStream inputStream = null;
        try {
            //创建文件夹
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //初始化
            inputStream = responseBody.byteStream();
            randomAccessFile = new RandomAccessFile(file, "rwd");
            channelOut = randomAccessFile.getChannel();
            //总长度
            long allLength = downloadTask.getTotalSize() == 0 ? responseBody.contentLength() : downloadTask.getCurrentSize() + responseBody.contentLength();

            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, downloadTask.getCurrentSize(), allLength - downloadTask.getCurrentSize());

            byte[] buffer = new byte[1024 * 4];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, length);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (inputStream != null) {
                IOUtils.close(inputStream);
            }
            if (channelOut != null) {
                IOUtils.close(channelOut);
            }
            if (randomAccessFile != null) {
                IOUtils.close(randomAccessFile);
            }
        }

    }
}
