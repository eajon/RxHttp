package com.github.eajon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.github.eajon.enums.RequestMethod;
import com.github.eajon.enums.RequestType;
import com.github.eajon.exception.ApiException;
import com.github.eajon.interceptor.DownloadInterceptor;
import com.github.eajon.interceptor.RequestInterceptor;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.task.BaseTask;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.task.UploadTask;
import com.github.eajon.upload.UploadRequestBody;
import com.github.eajon.util.GsonUtils;
import com.github.eajon.util.NetUtils;
import com.github.eajon.util.RetrofitUtils;
import com.github.eajon.util.RxBusUtils;
import com.github.eajon.util.RxUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    /*请求标志 rxBus发射标识*/
    private String tag;
    /*请求类型*/
    private RequestType requestType;
    /*请求方式*/
    private RequestMethod requestMethod;
    /*请求参数*/
    private Map<String, Object> parameter;
    /*header*/
    private Map<String, Object> header;
    /*json参数*/
    private RequestBody requestBody;
    /*LifecycleProvider*/
    private LifecycleProvider lifecycle;
    /*ActivityEvent*/
    private ActivityEvent activityEvent;
    /*FragmentEvent*/
    private FragmentEvent fragmentEvent;
    /*上传或者下载任务*/
    private BaseTask task;
    /*基础URL*/
    private String baseUrl;
    /*apiUrl*/
    private String apiUrl;
    /*entity*/
    private Type type;
    /*HTTP回调*/
    private HttpObserver httpObserver;
    /*RxDialog提供Context*/
    private Context dialogContext;
    /*dialog提示*/
    private String message;
    /*dialog是否默认可以取消*/
    private boolean cancelable;
    /*自定义对话框*/
    private Dialog dialog;
    /*是否是粘性消息*/
    private boolean isStick;
    /*cacheKey*/
    private String cacheKey;
    /*重试次数*/
    private int retryTime;

    /*构造函数*/
    private RxHttp(Builder builder) {
        this.tag = builder.tag;
        this.parameter = builder.parameter;
        this.header = builder.header;
        this.lifecycle = builder.lifecycle;
        this.activityEvent = builder.activityEvent;
        this.fragmentEvent = builder.fragmentEvent;
        this.task = builder.task;
        this.baseUrl = builder.baseUrl;
        this.apiUrl = builder.apiUrl;
        this.requestMethod = builder.requestMethod;
        this.type = builder.type;
        this.requestBody = builder.requestBody;
        this.dialogContext = builder.dialogContext;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        this.dialog = builder.dialog;
        this.isStick = builder.isStick;
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

    /*builder复制函数,非全部复制*/
    public RxHttp.Builder newBuilder() {
        return new RxHttp.Builder(this);
    }


    public static RxConfig getConfig() {
        return RxConfig.get();
    }

    /*Http请求*/
    public Disposable request(HttpObserver httpObserver) {
        this.httpObserver = httpObserver;
        if (httpObserver == null) {
            throw new NullPointerException("Observer must not null!");
        } else {
            return request();
        }
    }

    /*Http请求*/
    public Disposable request() {

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
        } else if (task instanceof UploadTask || task instanceof MultiUploadTask) {
            this.requestType = RequestType.UPLOAD;
            return doUpload();
        } else {
            throw new NullPointerException("error task!");
        }
    }


    /*执行请求*/
    private Disposable doRequest() {

        Observable observable = null;

        /*请求方式处理*/
        if (requestMethod == null) {
            requestMethod = RequestMethod.POST;
        }
        switch (requestMethod) {
            case GET:
                observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).get(disposeApiUrl(), parameter, header);
                break;
            case POST:
                if (requestBody != null) {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), requestBody, header);
                } else {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), parameter, header);
                }
                break;
            case DELETE:
                if (requestBody != null) {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).delete(disposeApiUrl(), requestBody, header);
                } else {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).delete(disposeApiUrl(), parameter, header);
                }
                break;
            case PUT:
                if (requestBody != null) {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).put(disposeApiUrl(), requestBody, header);
                } else {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).put(disposeApiUrl(), parameter, header);
                }
                break;
            case PATCH:
                if (requestBody != null) {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).patch(disposeApiUrl(), requestBody, header);
                } else {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).patch(disposeApiUrl(), parameter, header);
                }
                break;
            case HEAD:
                observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).head(disposeApiUrl(), parameter, header);
                break;
        }
        return subscribe(observable);
    }

    /*执行文件上传*/
    private Disposable doUpload() {
        Observable observable;
        /*处理文件*/
        List<MultipartBody.Part> partList = new ArrayList<>();
        File file;
        RequestBody requestBody;
        if (task instanceof UploadTask) {
            UploadTask uploadTask = ( UploadTask ) task;
            file = uploadTask.getFile();
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(uploadTask.getName(), uploadTask.getFileName(), new UploadRequestBody(requestBody, tag, isStick, uploadTask));
            observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).upload(disposeApiUrl(), convertParameter(), header, part);
        } else {
            MultiUploadTask multiUploadTask = ( MultiUploadTask ) task;
            for (int i = 0; i < multiUploadTask.getUploadTasks().size(); i++) {
                UploadTask task = multiUploadTask.getUploadTasks().get(i);
                file = task.getFile();
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(task.getName(), task.getFileName(), new UploadRequestBody(requestBody, tag, isStick, task, multiUploadTask));
                partList.add(part);
            }
            observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).upload(disposeApiUrl(), convertParameter(), header, partList);
        }

        /*请求处理*/
        return subscribe(observable);
    }

    private Disposable doDownload() {

        DownloadTask downloadTask = ( DownloadTask ) task;
        /*请求方式处理*/
        if (requestMethod == null) {
            requestMethod = RequestMethod.GET;
        }

        Observable observable = RetrofitUtils.get().getRetrofit(getBaseUrl(), new DownloadInterceptor(tag, isStick, downloadTask), new RequestInterceptor(requestMethod, parameter, header, requestBody)).download(disposeApiUrl(), "bytes=" + downloadTask.getCurrentSize() + "-");

        /*请求处理*/
        return subscribe(observable);

    }


    @SuppressWarnings("unchecked")
    private Disposable subscribe(Observable observable) {
        if (httpObserver == null) {
            httpObserver = new HttpObserver() {
                @Override
                public void onSuccess(Object o) {
                    RxBusUtils.sendBus(tag, isStick, o);
                }

                @Override
                public void onError(ApiException t) {
                    RxBusUtils.sendBus(tag, isStick, t);
                }
            };
        }
        if (dialog != null || dialogContext != null) {
            dialogObserver(observable).subscribe(httpObserver);
        } else {
            observe(observable).subscribe(httpObserver);
        }
        return httpObserver;
    }


    //dialog
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
            public Observable<Object> apply(final Dialog progressDialog) throws Exception {
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
            public void accept(Dialog dialog) throws Exception {
                dialog.dismiss();
            }
        });
    }


    //构建数据发射器
    @SuppressWarnings("unchecked")
    public Observable observe(Observable observable) {
        return observable
                .compose(RxUtils.map(requestType, type))
                .compose(RxUtils.cache(requestType, type, cacheKey))
                .compose(RxUtils.lifeCycle(lifecycle, activityEvent, fragmentEvent))
                .compose(RxUtils.retryPolicy(retryTime))
                .compose(RxUtils.sendEvent(task, tag, isStick))
                .compose(RxUtils.io_main());

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
            header = new HashMap<>();
        }

        //添加基础 Header
        Map<String, Object> baseHeader = getConfig().getBaseHeader();
        if (baseHeader != null && baseHeader.size() > 0) {
            header.putAll(baseHeader);
        }

        if (!header.isEmpty()) {
            //处理header中文或者换行符出错问题
            for (String key : header.keySet()) {
                header.put(key, NetUtils.getHeaderValueEncoded(header.get(key)));
            }
        }

    }

    /*处理 Parameter*/
    private void disposeParameter() {
        /*空处理*/
        if (parameter == null) {
            parameter = new HashMap<>();
        }
        //添加基础 Parameter
        Map<String, Object> baseParameter = getConfig().getBaseParameter();
        if (baseParameter != null && baseParameter.size() > 0) {
            parameter.putAll(baseParameter);
        }
    }

    /*上传其他参数使用post提交 ,Parameter不是requestbody的话，需要转为requestbody*/
    private Map<String, RequestBody> convertParameter() {
        Map<String, RequestBody> map = new HashMap<>();
        for (String key : parameter.keySet()) {
            if (!(parameter.get(key) instanceof RequestBody)) {
                map.put(key, RequestBody.create(null, String.valueOf(parameter.get(key))));
            }
        }
        return map;
    }


    /**
     * Builder
     * 构造Request所需参数，按需设置
     */
    public static final class Builder {

        /*请求标志 rxBus发射标识*/
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
        /*apiUrl*/
        String apiUrl;
        /*entity*/
        Type type;
        /*RxDialog提供Context*/
        Context dialogContext;
        /*dialog提示*/
        String message;
        /*dialog是否默认可以取消*/
        boolean cancelable;
        /*自定义progressDialog*/
        Dialog dialog;
        /*是否是粘性消息*/
        boolean isStick;
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
            this.apiUrl = rxHttp.apiUrl;
            this.type = rxHttp.type;
            this.parameter = rxHttp.parameter;
            this.header = rxHttp.header;
            this.requestBody = rxHttp.requestBody;
            this.isStick = rxHttp.isStick;
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


        /*基础URL*/
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /*API URL*/
        public Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
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

        /* 增加 Parameter 不断叠加参数 包括基础参数 自动转化为Map*/
        public Builder addObjectParameter(Object object) {
            if (this.parameter == null) {
                this.parameter = new HashMap<>();
            }
            this.parameter.putAll(GsonUtils.objectToMap(object));
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数*/
        public Builder setParameter(Map<String, Object> parameter) {
            this.parameter = parameter;
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数 自动转化为Map*/
        public Builder setObjectParameter(Object object) {
            this.parameter = GsonUtils.objectToMap(object);
            ;
            return this;
        }

        /* 增加 Header 不断叠加 Header 包括基础 Header */
        public Builder addHeader(Map<String, Object> header) {
            if (this.header == null) {
                this.header = new HashMap<>();
            }
            this.header.putAll(header);
            return this;
        }

        /* 增加 Header 不断叠加 Header 包括基础 Header 自动转化为Map*/
        public Builder addObjectHeader(Object object) {
            if (this.header == null) {
                this.header = new HashMap<>();
            }
            this.header.putAll(GsonUtils.objectToMap(object));
            return this;
        }

        /*设置 Header 会覆盖 Header 包括基础参数*/
        public Builder setHeader(Map<String, Object> header) {
            this.header = header;
            return this;
        }

        /*设置 Header 会覆盖 Header 包括基础参数 自动转化为Map*/
        public Builder setObjectHeader(Object object) {
            this.header = GsonUtils.objectToMap(object);
            return this;
        }


        /*设置RequestBody*/
        public Builder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        /*LifecycleProvider*/
        public Builder lifecycle(LifecycleProvider lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        /*ActivityEvent*/
        public Builder activityEvent(ActivityEvent activityEvent) {
            this.activityEvent = activityEvent;
            return this;
        }

        /*FragmentEvent*/
        public Builder fragmentEvent(FragmentEvent fragmentEvent) {
            this.fragmentEvent = fragmentEvent;
            return this;
        }

        /*返回的实体类型，对下载无效*/
        public Builder entity(Type type) {
            this.type = type;
            return this;
        }

        /*下载上传任务*/
        public Builder task(BaseTask baseTask) {
            this.task = baseTask;
            return this;
        }

        /*阻塞对话框*/
        public Builder withDialog(Context context) {
            this.dialogContext = context;
            this.message = "";
            this.cancelable = true;
            return this;
        }

        /*阻塞对话框*/
        public Builder withDialog(Context context, String message) {
            this.dialogContext = context;
            this.message = message;
            this.cancelable = true;
            return this;
        }

        /*阻塞对话框*/
        public Builder withDialog(Context context, String message, boolean cancelable) {
            this.dialogContext = context;
            this.message = message;
            this.cancelable = cancelable;
            return this;
        }

        /*阻塞对话框*/
        public Builder withDialog(Dialog dialog) {
            this.dialog = dialog;
            return this;
        }


        /*是否是粘性消息*/
        public Builder isStick(boolean isStick) {
            this.isStick = isStick;
            return this;
        }


        /*cacheKey*/
        public Builder cacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        /*重试次数*/
        public Builder retryTime(int retryTime) {
            this.retryTime = retryTime;
            return this;
        }

        public RxHttp build() {
            return new RxHttp(this);
        }


    }


}

