package com.github.eajon.Function;

import com.github.eajon.RxHttp;
import com.github.eajon.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 服务器结果处理函数
 *
 * @author wengyijiong
 */
public class HttpResponseFunction implements Function <JsonElement, Object> {


    private Class clazz;

    public HttpResponseFunction(Class <?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object apply(@NonNull JsonElement response) throws Exception {
        //打印服务器回传结果
        LogUtils.e(RxHttp.getConfig().getLogTag(),response.toString());
        /*此处不再处理业务相关逻辑交由开发者重写httpCallback*/
        if (null != clazz) {
            return new Gson().fromJson(response, clazz);
        } else {
            return new Gson().toJson(response);
        }
    }
}