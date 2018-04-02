using UnityEngine;
using System.Collections;
using System;

[Serializable]
public class AuthResponse
{
    public string jehmiAuthToken;
    public string transactionId;
    public string amount;
    public string country;
    public string language;
    public string currency;
    public string timeZone;
    public string hashData;

	public override string ToString()
	{
        return "[jehmiAuthToken = " + jehmiAuthToken + ", " +
            "transactionId = " + transactionId + ", " +
            "amount = " + amount + ", " +
            "country = " + country + ", " +
            "language = " + language + ", " +
            "currency = " + currency + ", " +
            "timeZone = " + timeZone + ", " +
            "hashData = " + hashData + "]";
	}
}
