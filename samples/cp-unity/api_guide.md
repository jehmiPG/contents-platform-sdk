# Unity SDK API Guide

### Permission
* CP SDK는 필요한 기능을 수행하기 위해 아래의 Permission을 필요로 합니다.
* 해당 퍼미션을 SDK에 포함하고 있으며 Permission이 거부되었을 경우 동작 하지 않을 수 있습니다.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> 
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

### 유저 등록하기
> 사용자가 로그인을 하였을 때, 발급된 UserId를 JCP에 등록요청을 합니다.
> 등록된 UserId는 사용자 식별용도로 사용됩니다.
> 해당앱 유저 관리에 사용됩니다. 앱 유저 생성 또는 변경 시 호출해야 합니다.

```csharp
using Jehmi.Unity;
public class Main : MonoBehaviour {
    // When user was been Login Completed.
    private void LoginCompleted(string userId) {
        JCP.RegisterUserId(userId, new LoginCallback(onSuccessRegisterUserId, onError));
    }
 
    private void onError(ErrorResult error)
    {
        JCP.ShowToast("-- onError : " + error.Message());
    }
 
    private void onSuccessRegisterUserId()
    {
        JCP.ShowToast("++ onSuccess RegisterUserId");
        Debug.Log("++ OnSuccess");
    }
}
```
##### Request Params
| Name   |   Mandatory  |   Type   | Description |
|:------:|:---------:|:---------:|:-----------|
| userId |     Y     |  string | 로그인시 발급받은 유저의 ID |
| callback |    N    |  LoginCallback | 결과를 전달받을 콜백 |

##### LoginCallback
| Name  |   Type   | Description |
|:------:|:---------:|:-----------|
| OnSuccess |            | 성공시 호출된다. |
| OnError | ErrorResult  | 실패시 호출된다. |

### 결제 요청하기
* JCP에 결제요청을 합니다. 결제를 요청하기에 앞서 아래의 과정이 필요합니다.
* step 1. 결제별로 고유한 값인 `transactionId` 를 발급합니다. (개발사 자체생성)
* step 2. 발급된 `transactionId`를 Jehmi Billing Server에 token값을 요청합니다. (server to server)
* step 3 Jehmi Billing Server로 부터 발급받은 `hashData`과 `jehmiAuthToken`를 결제정보와 함께 SDK에 보냅니다.

```csharp
    private void requestPurchase() {
        JCP.Purchase(
            "your hash data",
            "input received jehmi auth token",
            "input your billing transaction token",
            "your user id",
            "100",
            "THA",
            "THB",
            "your item id",
            "item name",        
            "ENG",
            "UTC",
            "http://backurl.com",
            new PurchaseCallback(onSuccess, onPending, onError));
    }
 
    private void onSuccess()
    {
        Debug.Log("++ OnSuccess");
        JCP.ShowToast("++ onSuccess Purchase!");
    }
 
    private void onPending()
    {
        Debug.Log("++ onPending");
        JCP.ShowToast("++ Purchase is get Pending status.");
    }
 
    private void onError(ErrorResult error)
    {
        Debug.Log("++ onError : " + error.Message());
        JCP.ShowToast(error.Message());
    }
}
```
##### Request Params
| Name   |   Mandatory  |   Type   | Description |
|:------:|:---------:|:---------:|:-----------|
| hashData |     Y     |  string | 정상요청인지를 확인하기 위한 데이터의 hash |
| jehmiAuthToken |    Y    |  string | CP Application에서 결제시 발급한 transactionId별로 Billing 서버에서 발급된 값 |
| transactionId |     Y     |  string | 결제별 Unique 값 |
| userId |    Y    |  string | 	Application별 유저당 Unique 값 |
| amount |     Y     |  string | 금액 |
| country |    Y    |  string | 국가 코드 (default 개발사 설정) ISO 3166 ALPHA-3 Code |
| currency |     Y     |  string | 통화 코드 (default 없음) ISO 4217 currency code. ISO ALPHA-3 Code |
| itemId |    Y    |  string | 아이템 Unique 식별자 |
| itemName |     Y     |  string | 아이템 이름 (결제시 노출될 수 있음) |
| language |    N    |  string | 언어 코드(default ENG) ISO 639-2/T Code. ISO ALPHA-3 Code |
| timeZone |     N     |  string | 	거래별 해당 지역의 시간대코드로 별도로 설정해서 요청 가능 Jehmi 는 UTC 기준으로 모든 서비스가 실행. 참조용으로 로컬 시간대코드가 필요한 경우 지정가능 (default UTC) |
| backUrl |    N    |  string | 결제완료시 Billing → App Server로 성공, 실패결과 호출(화면없음) |
| callback |     N     |  PurchaseCallback | 결제결과를 전달받을 콜백 |

##### PurchaseCallback
| Name   |  Type   | Description |
|:------:|:---------:|:-----------|
| OnSuccess |            | 결제 성공시 호출된다. |
| OnPending |             | PG사 결제 지연상태로 결제가 대기상태. (결제가 추후 성공이나 실패되었을경우  CP App Server로 결과가 전달됩니다) |
| OnError   | ErrorResult | 결제실패시 에러를 전달받는다. |

### 어플리케이션 정보얻기
* 등록된 앱에 대한 정보를 요청합니다.
* 응답으로 전달받은 정보를 토대로, 앱을 업데이트 요청을 하실 수 있습니다.
```csharp
using Jehmi.Unity;
public class Main : MonoBehaviour {
    private void GetAppInfo() {
        JCP.GetAppInfo(new ResultCallback<AppInfo>(onSuccess, onError));
    }
    private void onSuccess(AppInfo appInfo)
    {
        Debug.Log("++ OnSuccess : " + appInfo);
    }
 
    private void onError(ErrorResult error)
    {
        Debug.Log("++ onError : " + error.Message());
    }
}
```
##### ResultCallback
| Name  |   Type   | Description |
|:------:|:---------:|:-----------|
| OnSuccess |   AppInfo  | 성공시 호출된다. |
| OnError | ErrorResult  | 실패시 호출된다. |

##### AppInfo
| Name   |   Type   | Description |
|:------:|:---------:|:-----------|
| partnerAppId |  string | cp 식별자 (cp별 고유값) |
| packageName |  string | Application package name |
| apkVersionName |  string | Android version name |
| apkVersionCode |  int | Android version code |
| minAppVersionCode |  int | cp 앱이 지원하는 최소 지원버전 (admin 설정값) |
| requirementOsVersion |  string | 최소지원 android os version  |

### 어플리케이션 다운로드 및 설치
* 스토어에 등록되어있는 최신버전 앱의 다운로드를 요청합니다.
* 다운로드가 완료가 되면 자동으로 설치를 시도합니다.
* 실제 설치여부를 반환하지는 않습니다.
* android.permission.WRITE_EXTERNAL_STORAGE 퍼미션이 필요하며, 퍼미션이 없는경우 Error를 반환합니다.
```csharp
using Jehmi.Unity;
public class Main : MonoBehaviour {
    public void Start()
    {
        JCP.GetAppInfo(new ResultCallback<AppInfo>(onSuccess, onError));
    }
 
    private void CheckAndDownloadApk(int currentVersionCode, int lastestVersionCode) {
        if (currentVersionCode < lastestVersionCode)
        {
            string title = "Downloading My Application";
            string description = "Now downloading applicion. please wait for seconds";
            JCP.DownloadApk(title, description, false, new ErrorCallback(onErrorDelegate: onError));
        }
    }
 
    private void onSuccess(AppInfo appInfo)
    {
        Debug.Log("++ OnSuccess : " + appInfo);
        int currentVersionCode = 1; // your current version code
        int lastestVersionCode = appInfo.apkVersionCode;
        CheckAndDownloadApk(currentVersionCode, lastestVersionCode);
    }
 
    private void onError(ErrorResult error)
    {
        Debug.Log("++ onError : " + error.Message());
    }
}
```
##### Request Params
| Name   |   Mandatory  |   Type   | Description |
|:------:|:---------:|:---------:|:-----------|
| title |     Y     |  string | 다운로드시 Notification에 표시될 제목 |
| description |    Y    |  string | 다운로드시 Notification에 표시될 설명 |
| isOnlyWifi |     Y     |  bool | Wifi 에서만 다운로드를 할지 여부 |
| callback |    N    |  DownloadCallback | 다운로드 요청을 실패하였을경우 결과값을 전달합니다 (실제 설치여부를 리턴하진 않습니다.) |

##### ErrorCallback
| Name  |   Type   | Description |
|:------:|:---------:|:-----------|
| OnDownloadComplete |   | 다운로드 성공시 호출되며, 다운로드한 apk 파일의 uri를 전달합니다. |
| OnError | ErrorResult  | 실패시 호출된다. |

#### ErrorCode
ErrorResult 의 code 는 아래와 같습니다.

| Name              | Value | Description     |
| :---------------- |:-----:| :-------------- |
| UNKNOWN_ERROR     | -700  | 알 수 없는 에러    |
| PERMISSION_DENIED | -701  | 퍼미션 없음        |
| HTTP_ERROR        | -702  | http status 에러 |
| PURCHASE_FAILURE  | -703  | 결제실패          |
| NETWORK_ERROR     | -704  | 네트워크 연결실패   |
| INVALID_PARAMS    | -705  | 잘못된 파라미터     |
