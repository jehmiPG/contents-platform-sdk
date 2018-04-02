using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.Networking;

public interface Requestable<T> : Converable<T>
{
    /**
     * http 통신방법.
     * @return httpMethod. GET, POST, PUT, DELETE 등등.
     */
    Api.Method getMethod();

    /**
     * 요청할 target url.
     * @return 요청할 target url.
     */
    string getUrl();

    /**
     * http요청에 필요한 params.
     * @return http요청에 필요한 params.
     */
    Dictionary<string, string> getParams();

    /**
     * http요청에 필요한 headers.
     * @return http요청에 필요한 headers.
     */
    Dictionary<string, string> getHeaders();
}
