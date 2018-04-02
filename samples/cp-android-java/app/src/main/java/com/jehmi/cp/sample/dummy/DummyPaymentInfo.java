package com.jehmi.cp.sample.dummy;

import com.google.gson.annotations.SerializedName;
import com.jehmi.cp.constant.StringKeys;

public class DummyPaymentInfo {

    @SerializedName(StringKeys.hashData)
    public String hashData;

    @SerializedName(StringKeys.jehmiAuthToken)
    public String jehmiAuthToken;

    @SerializedName(StringKeys.transactionId)
    public String transactionId;

    @SerializedName(StringKeys.amount)
    public String amount;

    @SerializedName(StringKeys.country)
    public String country;

    @SerializedName(StringKeys.language)
    public String language;

    @SerializedName(StringKeys.currency)
    public String currency;

    @SerializedName(StringKeys.timeZone)
    public String timeZone;

    public DummyPaymentInfo() {
    }
}
