package com.jehmi.cp.sample.dummy

import com.jehmi.cp.JCP
import com.jehmi.cp.constant.StringKeys
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    data class DummyPaymentInfo(
            val hashData: String,
            val jehmiAuthToken: String,
            val transactionId: String,
            val amount: String,
            val country: String,
            val language: String,
            val currency: String,
            val timeZone: String
    )

    @POST("/api/authRequest")
    fun dummyPaymentInfo(@Body body: Map<String, String>): Observable<DummyPaymentInfo>

    companion object {
        private var service: ApiService? = null

        fun get(): ApiService {
            if (service == null) {
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor { chain ->
                    val original = chain.request()

                    val requestBuilder = original.newBuilder()
                            .addHeader("Connection", "Keep-Alive")
                            .addHeader("User-Agent", JCP.finalUserAgent())
                            .addHeader(StringKeys.apiKey, JCP.apiKey())
                            .addHeader(StringKeys.appKey, JCP.appKey())
                    
                    chain.proceed(requestBuilder.build())
                }

                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(loggingInterceptor)

                service = Retrofit.Builder()
                        .baseUrl("https://sandbox-cp-test.jehmi.com")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build().create(ApiService::class.java)
            }

            return service!!
        }
    }
}