/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.eajon.exception;

import android.net.ParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.IOException;
import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * http结果异常处理
 *
 * @author WENGYIJIONG
 */

public class ApiException extends Exception {

    private final int code;

    private String message;


    private String bodyMessage;


    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }


    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public String getDisplayMessage() {
        return "HttpCode:" + code + ";" + "throwable:" + message + ";" + "body:" + bodyMessage;
    }


    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = ( HttpException ) e;
            ResponseBody responseBody = httpException.response().errorBody();
            ex = new ApiException(httpException, httpException.code());
            try {
                ex.bodyMessage = responseBody.string();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return ex;
        } else if (e instanceof JSONException
                || e instanceof NotSerializableException
                || e instanceof ParseException
                || e instanceof UnsupportedOperationException) {
            ex = new ApiException(e, ERROR.PARSE_ERROR);
            ex.bodyMessage = "解析错误";
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(e, ERROR.CAST_ERROR);
            ex.bodyMessage = "类型转换错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ERROR.NETWORK_ERROR);
            ex.bodyMessage = "网络错误";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ERROR.SSL_ERROR);
            ex.bodyMessage = "证书错误";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(e, ERROR.TIMEOUT_ERROR);
            ex.bodyMessage = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(e, ERROR.TIMEOUT_ERROR);
            ex.bodyMessage = "连接超时";
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, ERROR.UNKNOWNHOST_ERROR);
            ex.bodyMessage = "未知主机错误";
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new ApiException(e, ERROR.NULLPOINTER_EXCEPTION);
            ex.bodyMessage = "空指针错误";
            return ex;
        } else if (e instanceof ProtocolException) {
            ex = new ApiException(e, ERROR.PROTOCOL_EXCEPTION);
            ex.bodyMessage = "文件流意外关闭";
            return ex;
        } else if (e instanceof SocketException) {
            ex = new ApiException(e, ERROR.SOCKET_CLOSE_ERROR);
            ex.bodyMessage = "文件流被关闭，暂停下载";
            return ex;
        } else if (e instanceof IOException) {
            ex = new ApiException(e, ERROR.IO_ERROR);
            ex.bodyMessage = "文件流错误";
            return ex;
        } else {
            ex = new ApiException(e, ERROR.UNKNOWN);
            ex.bodyMessage = "未定义错误";
            return ex;
        }
    }


    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未定义错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = UNKNOWN + 1;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = PARSE_ERROR + 1;
        /**
         * 协议错误
         */
        public static final int HTTP_ERROR = NETWORK_ERROR + 1;

        /**
         * 证书错误
         */
        public static final int SSL_ERROR = HTTP_ERROR + 1;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

        /**
         * 调用错误
         */
        public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = INVOKE_ERROR + 1;
        /**
         * 请求取消
         */
        public static final int REQUEST_CANCEL = CAST_ERROR + 1;
        /**
         * 未知主机错误
         */
        public static final int UNKNOWNHOST_ERROR = REQUEST_CANCEL + 1;
        /**
         * 空指针错误
         */
        public static final int NULLPOINTER_EXCEPTION = UNKNOWNHOST_ERROR + 1;
        /**
         * 文件流意外关闭
         */
        public static final int PROTOCOL_EXCEPTION = NULLPOINTER_EXCEPTION + 1;

        /**
         * 文件流被关闭，暂停下载
         */
        public static final int SOCKET_CLOSE_ERROR = PROTOCOL_EXCEPTION + 1;
        /**
         * 流错误
         */
        public static final int IO_ERROR = SOCKET_CLOSE_ERROR + 1;
    }
}