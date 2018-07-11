using System.Collections.Generic;
using UnityEngine.Networking;
using System;
using UnityEngine;
using System.Text;
using Jehmi.Unity;

public class AuthToken : RequestResponsable<AuthResponse>
{
    public delegate void ResponseDelegate(AuthResponse result, Exception e);
    Dictionary<string, string> headers = new Dictionary<string, string>();
    Dictionary<string, string> param = new Dictionary<string, string>();
    private ResponseDelegate callback = null;

    public AuthToken(string partnerAppId, string userId, string amount, string country, string language, string currency, ResponseDelegate callback) {
        headers.Add("Content-Type", "application/json");
        headers.Add("charset", "UTF-8");
        headers.Add(KeySet.partnerAppId, partnerAppId);

        param.Add(KeySet.userId, userId);
        param.Add(KeySet.amount, amount);
        param.Add(KeySet.country, country);
        param.Add(KeySet.language, language);
        param.Add(KeySet.currency, currency);

        this.callback = callback;
    }

    public Dictionary<string, string> getHeaders()
    {
        return headers;
    }

    public Api.Method getMethod()
    {
        return Api.Method.POST;
    }

    public Dictionary<string, string> getParams()
    {
        return param;
    }

    public string getUrl()
    {
        return "https://sandbox-cp-test.jehmi.com/api/v1/authRequest";
    }

    public AuthResponse Convert(byte[] datas)
    {
        string resultStr = Encoding.Default.GetString(datas);
        Debug.Log(resultStr);
        return JsonUtility.FromJson<AuthResponse>(resultStr);
    }

    public void onFailure(Exception e)
    {
        JCP.ShowToast("++ Get AuthToken Failure.\n" + e.Message);
        Debug.Log(e.Message);
        callback(null, e);
    }

    public void onSucceed(AuthResponse authRespones)
    {
        JCP.ShowToast("Get AuthToken Complete.\n Response : " + authRespones);
        callback(authRespones, null);
    }
}
