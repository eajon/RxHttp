package com.eajon.my.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    public TokenInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuilder = oldRequest.newBuilder();
        if (isTokenInvalid()) {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder();
            //添加参数
            builder.add("username", "admin");
            builder.add("password", "123456");
            Request request = new Request.Builder()
                    .url("http://172.17.12.42:8080/api/test/login")
                    .post(builder.build())
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
//            Token token = new Gson().fromJson(response.body().string(), Token.class);
//            newRequestBuilder.addHeader("X-Access-Token", token.getResult().getToken());
        }
        return chain.proceed(newRequestBuilder.build());
    }

    private boolean isTokenInvalid() {
        return true;
    }
}
