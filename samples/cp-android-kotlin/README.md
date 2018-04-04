# cp-android-kotlin
Kotlin 용 샘플 입니다.

## Download

### Gradle

Root `build.gradle` 에 아래와 같이 설정하세요. [보기](./build.gradle)

```
allprojects {
    repositories {
        jcenter()
        maven { url 'http://nexus.jehmi.com/nexus/content/groups/public/' }
    }
}
```

모듈의 `build.gradle` 에 아래와 같이 설정하세요. [보기](app/build.gradle)

```
dependencies {
    implementation 'com.jehmi:cp-sandbox:0.1.0'
}
```

### AAR
[다운로드](../../archive)



## Usage

### 키 추가
발급 받은 api, app key 를  strings.xml (경로 예시: /app/src/main/res/values/strings.xml) 에 추가합니다.</br>
App Scheme과 Host는 스토어에 해당앱 설치유무를 판별할때 사용됩니다.
* scheme의 규칙은 'jcp'를 prefix로 하여 appKey를 뒤에 붙이는 형태입니다. appkey가 1234라고 하였을때 scheme은 'jcp1234'가 됩니다.
```xml
<string name="api_key">발급 받은 ApiKey</string>
<string name="app_key">발급 받은 AppKey</string>
<string name="jcp_scheme">jcp + 발급 받은 AppKey</string>
<string name="jcp_host">execute</string>
```

AndroidManifest.xml 메타데이터 에 키값과 Intent Filter를 입력합니다.
* Intent Filter는 android.intent.category.LAUNCHER 속성이 포함된 Activity에 추가해야합니다.
```xml
<application android:label="@string/app_name" ...>
    ...
    <activity
            android:name=".MainActivity"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:launchMode="singleTask"
            android:windowSoftInputMode="adjustResize|stateAlwaysHidden">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
 
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
 
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
 
                <data
                    android:host="@string/jcp_host"
                    android:scheme="@string/jcp_scheme" />
            </intent-filter>
        </activity>
 
    <meta-data android:name="com.jehmi.cp.ApiKey" android:value="@string/api_key"/>
    <meta-data android:name="com.jehmi.cp.AppKey" android:value="@string/app_key"/>
    ...
</application>
```

### 퍼미션

사용하기 전에 먼저 인터넷과 외부 저장소 사용 권한이 필요합니다.
AndroidManifest.xml 에 퍼미션을 추가해 주세요.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### 런타임 퍼미션 받기

사용예시는 MainActivity 샘플코드를 확인해주세요.
안드로이드 6.0 이상 부터는 런타임 퍼미션을 받아야 합니다. [참고](https://developer.android.com/training/permissions/requesting.html)


### [API](../../README.md)

#### 유저 등록하기

해당앱 유저 관리에 사용됩니다. 앱 유저 생성 또는 변경 시 호출해야 합니다.

```kotlin
val userId = "user_123"; // CP 로그인 후에 발급된 유저 아이디.

JCP.registerUserId(userId, object : ILoginCallback {
    override fun onSuccess() {
        // 성공 처리.
    }

    override fun onError(error: ErrorResult?) {
        // 에러 처리.
    }
})
```

#### 결제 요청하기

CP Server 를 통해서 결제 데이터를 얻어온다. 아래 코드는 더미 데이터 입니다.

```kotlin
object PurchaseData {
    const val hashData = ""
    const val jehmiAuthToken = ""
    const val transactionId = ""
    const val amount = "10000"
    const val country = "THA"
    const val language = "ENG"
    const val currency = "THB"
    const val timeZone = "UTC"
    const val itemId = "ABCD"
    const val itemName = "100 Ruby"
    const val backUrl = ""
}
```

```kotlin
val userId = "user_123"; // CP 로그인 후에 발급된 유저 아이디.

JCP.purchase(this,
        PurchaseData.hashData,
        PurchaseData.jehmiAuthToken,
        PurchaseData.transactionId,
        userId,
        PurchaseData.amount,
        PurchaseData.country,
        PurchaseData.language,
        PurchaseData.currency,
        PurchaseData.timeZone,
        PurchaseData.itemId,
        PurchaseData.itemName,
        PurchaseData.backUrl,
        object : IPurchaseCallback {
            override fun onSuccess() {
                // 결제 성공.
            }

            override fun onPending() {
                // 결제 지연.
            }

            override fun onError(error: ErrorResult?) {
                // 결제 실패.
            }
        })      
```

#### 어플리케이션 정보얻기


```kotlin
JCP.getAppInfo(object : IResultCallback<AppInfo> {
    override fun onSuccess(appInfo: AppInfo?) {
        // appInfo 정보 사용.
    }

    override fun onError(error: ErrorResult?) {
        // 에러 처리.
    }
})
```

#### 어플리케이션 다운로드

```kotlin
val apkTitle = "Sample";
val apkDesc = "CP Application";

JCP.downloadApk(apkTitle, apkDesc, false) { error -> 
    // 에러 처리.
}
```


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
