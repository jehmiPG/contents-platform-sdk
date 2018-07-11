package com.jehmi.cp.sample.dummy;

import com.jehmi.cp.JCP;
import com.jehmi.cp.constant.StringKeys;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DummyApi {

    private static ApiService service;

    public static ApiService get() {
        if (service == null) {
            final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    final Request original = chain.request();
                    final Request.Builder requestBuilder = original.newBuilder();

                    requestBuilder.addHeader("Connection", "Keep-Alive")
                            .addHeader("User-Agent", JCP.finalUserAgent())
                            .addHeader(StringKeys.partnerAppId, JCP.partnerAppId());

                    return chain.proceed(requestBuilder.build());
                }
            });

            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);

            service = new Retrofit.Builder()
                    .baseUrl("https://alpha-api-cp-test.jehmi.com")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build().create(ApiService.class);
        }

        return service;
    }
}
