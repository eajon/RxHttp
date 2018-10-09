package com.github.eajon;

import android.text.TextUtils;


import com.github.eajon.function.DownloadResponseFunction;
import com.github.eajon.function.ErrorResponseFunction;
import com.github.eajon.function.HttpResponseFunction;
import com.github.eajon.download.DownloadInterceptor;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.observer.BaseObserver;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.observer.UploadObserver;
import com.github.eajon.retrofit.Method;
import com.github.eajon.retrofit.RxConfig;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadRequestBody;
import com.github.eajon.upload.UploadTask;
import com.github.eajon.util.RetrofitUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Http请求类
 *
 * @author wengyijiong
 */
public class RxHttp {

    /*请求方式*/
    private Method method;
    /*请求参数*/
    private Map <String, Object> parameter;
    /*header*/
    private Map <String, Object> header;
    /*json参数*/
    private RequestBody requestBody;
    /*LifecycleProvider*/
    private LifecycleProvider lifecycle;
    /*ActivityEvent*/
    private ActivityEvent activityEvent;
    /*FragmentEvent*/
    private FragmentEvent fragmentEvent;
    /*标识请求的TAG*/
    private String tag;
    /*上传任务列表*/
    private UploadTask uploadTask;
    /*上传任务列表*/
    private MultipartUploadTask multipartUploadTask;
    /*entity*/
    private DownloadTask downloadTask;
    /*基础URL*/
    private String baseUrl;
    /*apiUrl*/
    private String apiUrl;
    /*entity*/
    private Class <?> clazz;
    /*HTTP回调*/
    private BaseObserver baseObserver;
    /*下载文件回调*/
    private DownloadObserver downloadObserver;
    /*上传文件回调*/
    private UploadObserver uploadObserver;
    /*Retrofit observable */
    Observable apiObservable;


    /*构造函数*/
    private RxHttp(Builder builder) {
        this.parameter = builder.parameter;
        this.header = builder.header;
        this.lifecycle = builder.lifecycle;
        this.activityEvent = builder.activityEvent;
        this.fragmentEvent = builder.fragmentEvent;
        this.tag = builder.tag;
        this.uploadTask = builder.uploadTask;
        this.multipartUploadTask = builder.multipartUploadTask;
        this.baseUrl = builder.baseUrl;
        this.apiUrl = builder.apiUrl;
        this.method = builder.method;
        this.clazz = builder.clazz;
        this.requestBody = builder.requestBody;
        this.downloadTask = builder.downloadTask;
    }

    /*普通Http请求*/
    public void request(BaseObserver baseObserver) {
        this.baseObserver = baseObserver;
        if (baseObserver == null) {
            throw new NullPointerException("Observer must not null!");
        } else {
            doRequest();
        }
    }

    /*上传文件请求*/
    public void upload(UploadObserver uploadObserver) {
        this.uploadObserver = uploadObserver;
        if (uploadObserver == null) {
            throw new NullPointerException("UploadObserve must not null!");
        } else {
            if (uploadTask == null && multipartUploadTask == null) {
                throw new NullPointerException("UploadTask must not null!");
            } else {
                doUpload();
            }

        }
    }

    /*下载文件请求*/
    public void download(DownloadObserver downloadObserver) {
        this.downloadObserver = downloadObserver;
        if (downloadObserver == null) {
            throw new NullPointerException("DownloadObserver must not null!");
        } else {
            if (downloadTask == null) {
                throw new NullPointerException("DownloadTask must not null!");
            } else {
                doDownload();
            }
        }
    }


    /*执行请求*/
    private void doRequest() {
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();

        /*请求方式处理*/
        if (method == null) {
            method = Method.POST;
        }
        switch (method) {
            case GET:
                apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).get(disposeApiUrl(), parameter, header);
                break;
            case POST:
                if (requestBody != null) {
                    apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), requestBody, header);
                } else {
                    apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), parameter, header);
                }
                break;
            case DELETE:
                apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).delete(disposeApiUrl(), parameter, header);
                break;
            case PUT:
                apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).put(disposeApiUrl(), parameter, header);
                break;
            default:
                apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), parameter, header);
                break;
        }
        observe().subscribe(baseObserver);
    }

    /*执行文件上传*/
    private void doUpload() {
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();

        /*处理文件*/
        List <MultipartBody.Part> fileList = new ArrayList <>();
        File file;
        RequestBody requestBody;
        if (uploadTask != null) {

            file = uploadTask.getFile();
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(uploadTask.getTag(), file.getName(), new UploadRequestBody(requestBody, uploadTask));
            fileList.add(part);

            uploadObserver.setUploadTask(uploadTask);
        } else {
            for (int i = 0; i < multipartUploadTask.getUploadTasks().size(); i++) {
                UploadTask task = multipartUploadTask.getUploadTasks().get(i);
                file = task.getFile();
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(task.getTag(), file.getName(), new UploadRequestBody(requestBody, task, multipartUploadTask));
                fileList.add(part);
            }
            uploadObserver.setMultipartUploadTask(multipartUploadTask);
        }

        /*请求处理*/
        apiObservable = RetrofitUtils.get().getRetrofit(getBaseUrl()).upload(disposeApiUrl(), parameter, header, fileList);
        observe().subscribe(uploadObserver);

    }

    private void doDownload() {
        /*下载任务关联observer用于改变状态*/
        downloadObserver.setDownloadTask(downloadTask);
        /*请求处理*/
        apiObservable = RetrofitUtils.get().getRetrofit(getBasUrl(downloadTask.getServerUrl()), new DownloadInterceptor(downloadTask)).download(downloadTask.getServerUrl(), "bytes=" + downloadTask.getCurrentSize() + "-");
        observe().subscribe(downloadObserver);


    }


    /* compose 操作符 介于 map onErrorResumeNext */
    private Observable compose() {
        if (lifecycle != null) {
            if (activityEvent != null || fragmentEvent != null) {
                //两个同时存在,以 activity 为准
                if (activityEvent != null && fragmentEvent != null) {
                    return map().compose(lifecycle.bindUntilEvent(activityEvent));
                }
                if (activityEvent != null) {
                    return map().compose(lifecycle.bindUntilEvent(activityEvent));
                }
                if (fragmentEvent != null) {
                    return map().compose(lifecycle.bindUntilEvent(fragmentEvent));
                }
            } else {
                return map().compose(lifecycle.bindToLifecycle());
            }
        }
        return map();
    }


    private Observable map() {
        if (downloadTask != null) {
            return apiObservable.map(new DownloadResponseFunction(downloadTask));
        } else {
            return apiObservable.map(new HttpResponseFunction(clazz));
        }
    }


    //    /*线程设置*/
    public Observable observe() {
        return compose().doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                if (baseObserver != null) {
                    if (baseObserver instanceof HttpObserver)
                        ((HttpObserver) baseObserver).onCancel();
                } else if (uploadObserver != null) {
                    if (uploadTask != null) {
                        uploadTask.setState(UploadTask.State.CANCEL);
                    } else {
                        multipartUploadTask.setState(UploadTask.State.CANCEL);
                        for (UploadTask uploadTask : multipartUploadTask.getUploadTasks()) {
                            uploadTask.setState(UploadTask.State.CANCEL);
                        }
                    }
                    uploadObserver.onCancel();
                } else if (downloadObserver != null) {
                    downloadTask.setState(DownloadTask.State.PAUSE);
                    downloadObserver.onPause();
                }
            }
        }).onErrorResumeNext(new ErrorResponseFunction <>()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /*获取基础URL*/
    private String getBaseUrl() {
        //如果没有重新指定URL则是用默认配置
        return TextUtils.isEmpty(baseUrl) ? getConfig().getBaseUrl() : baseUrl;
    }

    /*ApiUrl处理*/
    private String disposeApiUrl() {
        return TextUtils.isEmpty(apiUrl) ? "" : apiUrl;
    }

    /*处理Header*/
    private void disposeHeader() {

        /*header空处理*/
        if (header == null) {
            header = new TreeMap <>();
        }

        //添加基础 Header
        Map <String, Object> baseHeader = getConfig().getBaseHeader();
        if (baseHeader != null && baseHeader.size() > 0) {
            header.putAll(baseHeader);
        }

        if (!header.isEmpty()) {
            //处理header中文或者换行符出错问题
            for (String key : header.keySet()) {
                header.put(key, getHeaderValueEncoded(header.get(key)));
            }
        }

    }

    /*处理 Parameter*/
    private void disposeParameter() {

        /*空处理*/
        if (parameter == null) {
            parameter = new TreeMap <>();
        }

        //添加基础 Parameter
        Map <String, Object> baseParameter = getConfig().getBaseParameter();
        if (baseParameter != null && baseParameter.size() > 0) {
            parameter.putAll(baseParameter);
        }
    }


    /**
     * Builder
     * 构造Request所需参数，按需设置
     */
    public static final class Builder {
        /*请求方式*/
        Method method;
        /*请求参数*/
        Map <String, Object> parameter;
        /*header*/
        Map <String, Object> header;
        /*json参数*/
        RequestBody requestBody;
        /*LifecycleProvider*/
        LifecycleProvider lifecycle;
        /*ActivityEvent*/
        ActivityEvent activityEvent;
        /*FragmentEvent*/
        FragmentEvent fragmentEvent;
        /*标识请求的TAG*/
        String tag;
        /*单个上传任务*/
        UploadTask uploadTask;
        /*上传任务列表*/
        MultipartUploadTask multipartUploadTask;
        /*基础URL*/
        String baseUrl;
        /*apiUrl*/
        String apiUrl;
        /*下载任务*/
        DownloadTask downloadTask;
        /*entity*/
        Class <?> clazz;

        public Builder() {
        }

        /*GET*/
        public RxHttp.Builder get() {
            this.method = Method.GET;
            return this;
        }

        /*POST*/
        public RxHttp.Builder post() {
            this.method = Method.POST;
            return this;
        }

        /*DELETE*/
        public RxHttp.Builder delete() {
            this.method = Method.DELETE;
            return this;
        }

        /*PUT*/
        public RxHttp.Builder put() {
            this.method = Method.PUT;
            return this;
        }

        /*基础URL*/
        public RxHttp.Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /*API URL*/
        public RxHttp.Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /* 增加 Parameter 不断叠加参数 包括基础参数 */
        public RxHttp.Builder addParameter(Map <String, Object> parameter) {
            if (this.parameter == null) {
                this.parameter = new TreeMap <>();
            }
            this.parameter.putAll(parameter);
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数*/
        public RxHttp.Builder setParameter(Map <String, Object> parameter) {
            this.parameter = parameter;
            return this;
        }

        /* 增加 Header 不断叠加 Header 包括基础 Header */
        public RxHttp.Builder addHeader(Map <String, Object> header) {
            if (this.header == null) {
                this.header = new TreeMap <>();
            }
            this.header.putAll(header);
            return this;
        }

        /*设置 Header 会覆盖 Header 包括基础参数*/
        public RxHttp.Builder setHeader(Map <String, Object> header) {
            this.header = header;
            return this;
        }

        /*设置RequestBody*/
        public RxHttp.Builder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        /*LifecycleProvider*/
        public RxHttp.Builder lifecycle(LifecycleProvider lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        /*ActivityEvent*/
        public RxHttp.Builder activityEvent(ActivityEvent activityEvent) {
            this.activityEvent = activityEvent;
            return this;
        }

        /*FragmentEvent*/
        public RxHttp.Builder fragmentEvent(FragmentEvent fragmentEvent) {
            this.fragmentEvent = fragmentEvent;
            return this;
        }

        /*tag*/
        public RxHttp.Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public RxHttp.Builder entity(Class <?> clazz) {
            this.clazz = clazz;
            return this;
        }

        /*单个文件*/
        public RxHttp.Builder uploadTask(UploadTask uploadTask) {
            this.uploadTask = uploadTask;
            return this;
        }

        /*文件集合*/
        public RxHttp.Builder multipartUploadTask(MultipartUploadTask multipartUploadTask) {
            this.multipartUploadTask = multipartUploadTask;
            return this;
        }

        /*下载任务*/
        public RxHttp.Builder downloadTask(DownloadTask downloadTask) {
            this.downloadTask = downloadTask;
            return this;
        }


        public RxHttp build() {
            return new RxHttp(this);
        }


    }

    /**
     * 获取 encode 后 Header 值
     * 备注: OkHttp Header 中的 value 不支持 null, \n 和 中文 等特殊字符
     * 后台解析中文 Header 值需要decode（这个后台处理，前端不用理会）
     *
     * @param value
     * @return
     */
    public static Object getHeaderValueEncoded(Object value) {
        if (value == null) return "null";
        if (value instanceof String) {
            String strValue = ((String) value).replace("\n", "");//换行符
            for (int i = 0, length = strValue.length(); i < length; i++) {
                char c = strValue.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    try {
                        return URLEncoder.encode(strValue, "UTF-8");//中文处理
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            }
            return strValue;
        } else {
            return value;
        }
    }

    /**
     * 获取BaseUrl
     * 备注:根据完整URL获取BasUrl
     *
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }

    public static RxConfig getConfig() {
        return RxConfig.get();
    }

}

