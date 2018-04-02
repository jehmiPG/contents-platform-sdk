using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Text;

public class Api {
    public enum Method
    {
        GET, POST
    };

    public static IEnumerator Request<T>(RequestResponsable<T> request)
    {
        Debug.LogFormat("++ API Request({0})", request.getUrl());
        WWW result = null;
        switch (request.getMethod())
        {
            case Method.POST:
                result = POST(request);
                break;
            default:
                result = GET(request);
                break;
        }
        yield return result;

        try {
            if (result.error != null) {
                throw new Exception(result.error);
            }

            T obj = request.Convert(result.bytes);
            request.onSucceed(obj);
        } catch (Exception e) {
            request.onFailure(e);
        }
    }

    private static WWW GET<T>(RequestResponsable<T> request)
    {
        WWW www = new WWW(request.getUrl(), null, request.getHeaders());
        return www;
    }

    private static WWW POST<T>(Requestable<T> request)
    {
        byte[] data = Encoding.UTF8.GetBytes(Json.Serialize(request.getParams()));

        Debug.Log("++ Data : " + Encoding.Default.GetString(data));
        WWW www = new WWW(request.getUrl(), data, request.getHeaders());
        return www;
    }
}
