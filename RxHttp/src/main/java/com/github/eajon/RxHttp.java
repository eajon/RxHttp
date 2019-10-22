package com.github.eajon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.eajon.body.UploadRequestBody;
import com.github.eajon.enums.RequestMethod;
import com.github.eajon.enums.RequestType;
import com.github.eajon.exception.HttpMethodException;
import com.github.eajon.interceptor.DownloadResponseInterceptor;
import com.github.eajon.interceptor.HttpRequestInterceptor;
import com.github.eajon.model.RequestEntity;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.task.BaseTask;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.task.UploadTask;
import com.github.eajon.util.GsonUtils;
import com.github.eajon.util.JacksonUtils;
import com.github.eajon.util.EncodeUtils;
import com.github.eajon.util.ReflectUtils;
import com.github.eajon.util.RetrofitUtils;
import com.github.eajon.util.RxUtils;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Http请求类
 *
 * @author wengyijiong
 */
public class RxHttp {
    /**
     * 请求标志
     */
    private String tag;
    /**
     * 请求类型
     */
    private RequestType requestType;
    /**
     * 请求方式
     */
    private RequestMethod requestMethod;
    /**
     * 请求参数
     */
    private Map<String, Object> parameter;
    /**
     * header
     */
    private Map<String, Object> header;
    /**
     * json参数
     */
    private RequestBody requestBody;
    /**
     * LifecycleProvider
     */
    private LifecycleProvider lifecycle;
    /**
     * ActivityEvent
     */
    private ActivityEvent activityEvent;
    /**
     * FragmentEvent
     */
    private FragmentEvent fragmentEvent;
    /**
     * 上传或者下载任务
     */
    private BaseTask task;
    /**
     * 基础URL
     */
    private String baseUrl;
    /**
     * api
     */
    private String api;
    /**
     * entity
     */
    private Type type;
    /**
     * HTTP回调
     */
    private HttpObserver httpObserver;
    /**
     * RxDialog提供Context
     */
    private Context dialogContext;
    /**
     * dialog提示
     */
    private String message;
    /**
     * dialog是否默认可以取消
     */
    private boolean cancelable;
    /**
     * 自定义对话框
     */
    private Dialog dialog;
    /**
     * progressBar
     */
    private View view;
    /**
     * cacheKey
     */
    private String cacheKey;
    /**
     * 重试次数
     */
    private int retryTime;


    /**
     * 构造函数
     */
    private RxHttp(Builder builder) {
        this.tag = builder.tag;
        this.parameter = builder.parameter;
        this.header = builder.header;
        this.lifecycle = builder.lifecycle;
        this.activityEvent = builder.activityEvent;
        this.fragmentEvent = builder.fragmentEvent;
        this.task = builder.task;
        this.baseUrl = builder.baseUrl;
        this.api = builder.api;
        this.requestMethod = builder.requestMethod;
        this.requestBody = builder.requestBody;
        this.dialogContext = builder.dialogContext;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        this.dialog = builder.dialog;
        this.view = builder.view;
        this.cacheKey = builder.cacheKey;
        this.retryTime = builder.retryTime;
    }

    public String getTag() {
        return tag;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public HttpObserver getHttpObserver() {
        return httpObserver;
    }

    /**
     * 构建Http请求
     */
    public Observable getRequest() {
        /*header处理*/
        disposeHeader();
        /*Parameter处理*/
        disposeParameter();
        if (task == null) {
            this.requestType = RequestType.REQUEST;
            return doRequest();
        } else {
            throw new NullPointerException("not support task!");
        }
    }

    /**
     * builder复制函数,非全部复制
     */
    public RxHttp.Builder newBuilder() {
        return new RxHttp.Builder(this);
    }


    public static RxConfig getConfig() {
        return RxConfig.get();
    }

    /**
     * Http请求
     */
    public Disposable request(HttpObserver httpObserver) {

        if (httpObserver == null) {
            throw new NullPointerException("Observer must not null!");
        } else {
            this.httpObserver = httpObserver;
            /*处理泛型type*/
            type = ReflectUtils.getParameterizedType(httpObserver);
            return subscribe(createRequest());
        }
    }


    /**
     * 构建Http请求
     */
    private Observable createRequest() {
        /*header处理*/
        disposeHeader();
        /*Parameter处理*/
        disposeParameter();

        if (task == null) {
            this.requestType = RequestType.REQUEST;
            return doRequest();
        } else if (task instanceof DownloadTask) {
            this.requestType = RequestType.DOWNLOAD;
            return doDownload();
        } else if (task instanceof UploadTask ) {
            this.requestType = RequestType.UPLOAD;
            return doUpload((UploadTask)task);
        } else if (task instanceof MultiUploadTask ) {
            this.requestType = RequestType.UPLOAD;
            return doUpload((MultiUploadTask)task);
        } else {
            throw new NullPointerException("error task!");
        }
    }


    /**
     * 执行请求
     */
    private Observable doRequest() {
        /*请求方式处理*/
        if (requestMethod == null) {
            requestMethod = RequestMethod.POST;
        }
        return RetrofitUtils.getRetrofit(getBaseUrl(), new RequestEntity(requestMethod, parameter, header, requestBody)).request(disposeApiUrl());
    }

    /**
     * 执行文件上传OCT-STREAM
     */
    private Observable doUpload(UploadTask uploadTask) {
        /*请求方式处理*/
        if (requestMethod == null) {
            requestMethod = RequestMethod.POST;
        }
        /*处理requestBody,正常上传不含requestBody参数*/
        if (requestBody != null) {
            throw new IllegalArgumentException("requestBody is not allow here,please use task");
        }
        requestBody = new UploadRequestBody(RequestBody.create(MediaType.parse("application/octet-stream;charset=utf-8"), uploadTask.getFile()), httpObserver, uploadTask);
        switch (requestMethod) {
            case POST:
            case PUT:
            case PATCH:
                return RetrofitUtils.getRetrofit(getBaseUrl(), new RequestEntity(requestMethod, parameter, header, requestBody)).request(disposeApiUrl());
            default:
                throw new HttpMethodException();
        }
    }

    /**
     * 执行文件上传MultiPart
     */
    private Observable doUpload(MultiUploadTask multiUploadTask) {
        /*请求方式处理*/
        if (requestMethod == null) {
            requestMethod = RequestMethod.POST;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        /*处理文件*/
        builder.setType(MultipartBody.FORM);
        for (UploadTask uploadTask : multiUploadTask.getUploadTasks()) {
            addFilePart(builder, uploadTask);
        }
        /*处理参数*/
        for (String key : parameter.keySet()) {
            builder.addFormDataPart(key, String.valueOf(parameter.get(key)));
        }
        /*处理requestBody,正常上传不含requestBody参数*/
        if (requestBody != null) {
            throw new IllegalArgumentException("requestBody is not allow here");
        }
        requestBody = builder.build();
        switch (requestMethod) {
            case POST:
            case PUT:
            case PATCH:
                return RetrofitUtils.getRetrofit(getBaseUrl(), new RequestEntity(requestMethod, null, header, requestBody)).request(disposeApiUrl());
            default:
                throw new HttpMethodException();
        }
    }

    private Observable doDownload() {
        /*请求方式处理*/
        if (requestMethod == null) {
            requestMethod = RequestMethod.GET;
        }
        DownloadTask downloadTask = ( DownloadTask ) task;
        switch (requestMethod) {
            case GET:
            case POST:
                return RetrofitUtils.getRetrofit(getBaseUrl(), new HttpRequestInterceptor(new RequestEntity(requestMethod, parameter, header, requestBody)), new DownloadResponseInterceptor(httpObserver, downloadTask)).download(disposeApiUrl(), "bytes=" + downloadTask.getCurrentSize() + "-");
            default:
                throw new HttpMethodException();
        }
    }


    @SuppressWarnings("unchecked")
    private Disposable subscribe(Observable observable) {
        if (dialog != null || dialogContext != null) {
            dialogObserver(observable).subscribe(httpObserver);
        } else if (view != null) {
            viewObserver(observable).subscribe(httpObserver);
        } else {
            observe(observable).subscribe(httpObserver);
        }
        return httpObserver;
    }


    /**
     * 设置DIALOG
     */
    @SuppressWarnings("unchecked")
    private Observable dialogObserver(final Observable observable) {
        return Observable.using(new Callable<Dialog>() {
            @Override
            public Dialog call() {
                if (dialog != null) {
                    dialog.show();
                    return dialog;
                }
                return ProgressDialog.show(dialogContext, null, message, true, cancelable);
            }
        }, new Function<Dialog, Observable<Object>>() {
            @Override
            public Observable<Object> apply(final Dialog progressDialog) {
                final BehaviorSubject<Boolean> dialogSubject = BehaviorSubject.create();
                return observe(observable).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialogSubject.onNext(true);
                            }
                        });
                    }
                }).takeUntil(dialogSubject);
            }
        }, new Consumer<Dialog>() {
            @Override
            public void accept(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置ProgressBar
     */
    @SuppressWarnings("unchecked")
    private Observable viewObserver(final Observable observable) {
        return Observable.using(new Callable<View>() {
            @Override
            public View call() {
                view.setVisibility(View.VISIBLE);
                return view;
            }
        }, new Function<View, Observable<Object>>() {
            @Override
            public Observable<Object> apply(final View view) {
                return observe(observable);
            }
        }, new Consumer<View>() {
            @Override
            public void accept(View view) {
                view.setVisibility(View.INVISIBLE);
            }
        });
    }


    /**
     * 构建数据发射器
     */
    @SuppressWarnings("unchecked")
    public Observable observe(Observable observable) {
        return observable
                .compose(RxUtils.map(requestType, type))
                .compose(RxUtils.cache(requestType, type, cacheKey))
                .compose(RxUtils.retryPolicy(retryTime))
                .compose(RxUtils.lifeCycle(lifecycle, activityEvent, fragmentEvent))
                .compose(RxUtils.sendState(task, httpObserver))
                .compose(RxUtils.ioMain());

    }


    /**
     * 获取BaseUrl
     */
    private String getBaseUrl() {
        //如果没有重新指定URL则是用默认配置
        return TextUtils.isEmpty(baseUrl) ? getConfig().getBaseUrl() : baseUrl;
    }

    /**
     * ApiUrl处理
     */
    private String disposeApiUrl() {
        return TextUtils.isEmpty(api) ? "" : api;
    }

    /**
     * 处理Header
     */
    private void disposeHeader() {

        /*header空处理*/
        if (header == null) {
            header = new HashMap<>();
        }

        /*添加基础Header*/
        Map<String, Object> baseHeader = getConfig().getBaseHeader();
        if (baseHeader != null && baseHeader.size() > 0) {
            header.putAll(baseHeader);
        }

        if (!header.isEmpty()) {
            /*处理Header字符问题*/
            for (String key : header.keySet()) {
                header.put(key, EncodeUtils.getHeaderValueEncoded(header.get(key)));
            }
        }

    }

    /**
     * 处理 Parameter
     */
    private void disposeParameter() {
        /*空处理*/
        if (parameter == null) {
            parameter = new HashMap<>();
        }
        /*添加基础 Parameter*/
        Map<String, Object> baseParameter = getConfig().getBaseParameter();
        if (baseParameter != null && baseParameter.size() > 0) {
            parameter.putAll(baseParameter);
        }
    }


    /**
     * 上传增加文件参数
     */
    private void addFilePart(MultipartBody.Builder builder, UploadTask uploadTask) {
        RequestBody requestBody = RequestBody.create(null, uploadTask.getFile());
        builder.addFormDataPart(uploadTask.getName(), EncodeUtils.getHeaderValueEncoded(uploadTask.getFileName()).toString(), new UploadRequestBody(requestBody, httpObserver, uploadTask));
    }


    /**
     * Builder
     * 构造Request所需参数，按需设置
     */
    public static final class Builder {

        /*请求标志 */
        String tag;
        /*请求方式*/
        RequestMethod requestMethod;
        /*请求参数*/
        Map<String, Object> parameter;
        /*header*/
        Map<String, Object> header;
        /*json参数*/
        RequestBody requestBody;
        /*LifecycleProvider*/
        LifecycleProvider lifecycle;
        /*ActivityEvent*/
        ActivityEvent activityEvent;
        /*FragmentEvent*/
        FragmentEvent fragmentEvent;
        /*上传或者下载任务*/
        BaseTask task;
        /*基础URL*/
        String baseUrl;
        /*api*/
        String api;
        /*RxDialog提供Context*/
        Context dialogContext;
        /*dialog提示*/
        String message;
        /*dialog是否默认可以取消*/
        boolean cancelable;
        /*自定义progressDialog*/
        Dialog dialog;
        /*progressBar*/
        private View view;
        /*cacheKey*/
        String cacheKey;
        /*重试次数，仅针对部分http连接问题进行重试，默认每隔0.5秒*/
        int retryTime;


        public Builder() {

        }

        Builder(RxHttp rxHttp) {
            this.tag = rxHttp.tag;
            this.requestMethod = rxHttp.requestMethod;
            this.activityEvent = rxHttp.activityEvent;
            this.fragmentEvent = rxHttp.fragmentEvent;
            this.baseUrl = rxHttp.baseUrl;
            this.api = rxHttp.api;
            this.parameter = rxHttp.parameter;
            this.header = rxHttp.header;
            this.requestBody = rxHttp.requestBody;
            this.cacheKey = rxHttp.cacheKey;
        }

        /*GET*/
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        /*GET*/
        public Builder get() {
            this.requestMethod = RequestMethod.GET;
            return this;
        }

        /*POST*/
        public Builder post() {
            this.requestMethod = RequestMethod.POST;
            return this;
        }

        /*DELETE*/
        public Builder delete() {
            this.requestMethod = RequestMethod.DELETE;
            return this;
        }

        /*PUT*/
        public Builder put() {
            this.requestMethod = RequestMethod.PUT;
            return this;
        }

        /*PATCH*/
        public Builder patch() {
            this.requestMethod = RequestMethod.PATCH;
            return this;
        }

        /*HEAD*/
        public Builder head() {
            this.requestMethod = RequestMethod.HEAD;
            return this;
        }

        /*GET*/
        public Builder get(String api) {
            this.requestMethod = RequestMethod.GET;
            this.api=api;
            return this;
        }

        /*POST*/
        public Builder post(String api) {
            this.requestMethod = RequestMethod.POST;
            this.api=api;
            return this;
        }

        /*DELETE*/
        public Builder delete(String api) {
            this.requestMethod = RequestMethod.DELETE;
            this.api=api;
            return this;
        }

        /*PUT*/
        public Builder put(String api) {
            this.requestMethod = RequestMethod.PUT;
            this.api=api;
            return this;
        }

        /*PATCH*/
        public Builder patch(String api) {
            this.requestMethod = RequestMethod.PATCH;
            this.api=api;
            return this;
        }

        /*HEAD*/
        public Builder head(String api) {
            this.requestMethod = RequestMethod.HEAD;
            this.api=api;
            return this;
        }


        /*基础URL*/
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /*API URL*/
        public Builder api(String api) {
            this.api = api;
            return this;
        }

        /* 增加 Parameter 不断叠加参数 包括基础参数 */
        public Builder addParameter(Map<String, Object> parameter) {
            if (this.parameter == null) {
                this.parameter = new HashMap<>();
            }
            this.parameter.putAll(parameter);
            return this;
        }

        /*增加 Parameter 不断叠加参数 包括基础参数 自动转化为Map*/
        @SuppressWarnings(value = "unchecked")
        public Builder addTypeParameter(Object object) {
            if (this.parameter == null) {
                this.parameter = new HashMap<>();
            }
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    this.parameter.putAll(JacksonUtils.getMapper().convertValue(object, Map.class));
                    break;
                case FASTJSON:
                    this.parameter.putAll(JSONObject.parseObject((JSON.toJSON(object)).toString()));
                    break;
                case GSON:
                default:
                    this.parameter.putAll(GsonUtils.object2Map(object));
                    break;
            }
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数*/
        public Builder setParameter(Map<String, Object> parameter) {
            this.parameter = parameter;
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数 自动转化为Map*/
        @SuppressWarnings(value = "unchecked")
        public Builder setTypeParameter(Object object) {
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    this.parameter = JacksonUtils.getMapper().convertValue(object, Map.class);
                    break;
                case FASTJSON:
                    this.parameter = JSONObject.parseObject((JSON.toJSON(object)).toString());
                    break;
                case GSON:
                default:
                    this.parameter = GsonUtils.object2Map(object);
                    break;
            }
            return this;
        }

        /*  增加 Header 不断叠加 Header 包括基础 Header */
        public Builder addHeader(Map<String, Object> header) {
            if (this.header == null) {
                this.header = new HashMap<>();
            }
            this.header.putAll(header);
            return this;
        }

        /* 增加 Header 不断叠加 Header 包括基础 Header 自动转化为Map*/
        @SuppressWarnings(value = "unchecked")
        public Builder addTypeHeader(Object object) {
            if (this.header == null) {
                this.header = new HashMap<>();
            }
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    this.header.putAll(JacksonUtils.getMapper().convertValue(object, Map.class));
                    break;
                case FASTJSON:
                    this.header.putAll(JSONObject.parseObject((JSON.toJSON(object)).toString()));
                    break;
                case GSON:
                default:
                    this.header.putAll(GsonUtils.object2Map(object));
                    break;
            }
            return this;
        }

        /* 设置 Header 会覆盖 Header 包括基础参数*/
        public Builder setHeader(Map<String, Object> header) {
            this.header = header;
            return this;
        }

        /* 设置 Header 会覆盖 Header 包括基础参数 自动转化为Map*/
        @SuppressWarnings(value = "unchecked")
        public Builder setTypeHeader(Object object) {
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    this.header = JacksonUtils.getMapper().convertValue(object, Map.class);
                    break;
                case FASTJSON:
                    this.header = JSONObject.parseObject((JSON.toJSON(object)).toString());
                    break;
                case GSON:
                default:
                    this.header = GsonUtils.object2Map(object);
                    break;
            }
            return this;
        }


        /* 设置RequestBody*/
        public Builder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        /* LifecycleProvider*/
        public Builder lifecycle(LifecycleProvider lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        /* ActivityEvent*/
        public Builder activityEvent(ActivityEvent activityEvent) {
            this.activityEvent = activityEvent;
            return this;
        }

        /* FragmentEvent*/
        public Builder fragmentEvent(FragmentEvent fragmentEvent) {
            this.fragmentEvent = fragmentEvent;
            return this;
        }

        /* 下载上传任务*/
        public Builder task(BaseTask baseTask) {
            this.task = baseTask;
            return this;
        }

        /* 阻塞对话框*/
        public Builder withDialog(Context context) {
            this.dialogContext = context;
            this.message = "";
            this.cancelable = true;
            return this;
        }

        /* 阻塞对话框*/
        public Builder withDialog(Context context, String message) {
            this.dialogContext = context;
            this.message = message;
            this.cancelable = true;
            return this;
        }

        /* 阻塞对话框*/
        public Builder withDialog(Context context, String message, boolean cancelable) {
            this.dialogContext = context;
            this.message = message;
            this.cancelable = cancelable;
            return this;
        }


        /* 阻塞对话框*/
        public Builder withDialog(Dialog dialog) {
            this.dialog = dialog;
            return this;
        }

        /* progressBar*/
        public Builder withView(View view) {
            this.view = view;
            return this;
        }


        /* cacheKey*/
        public Builder cacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        /* 重试次数*/
        public Builder retryTime(int retryTime) {
            this.retryTime = retryTime;
            return this;
        }


        public RxHttp build() {
            return new RxHttp(this);
        }


    }


}

