package com.jehmi.cp.sample.dummy;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/v1/authRequest")
    Observable<DummyPaymentInfo> dummyPaymentInfo(@Body Map<String, String> body);
}
