# contents-platform-sdk #
Jehmi Content Platform (이하 JCP)에서 제공하는 Tracking, Biiling, Store API SDK 입니다.


## API 소개

### 1. User Id 등록 요청 (JCP.registerUserId)
* 사용자가 로그인을 하였을 때, 발급된 UserId를 JCP에 등록요청을 합니다.
* 등록된 UserId는 사용자 식별용도로 사용됩니다.
```java
JCP.registerUserId(String userId, ILoginCallback callback)
```

#### Request Params
| Name   |   Mandatory  |   Type   | Description |
|:------:|:---------:|:---------:|:-----------|
| userId |     Y     |  String | 로그인시 발급받은 유저의 ID |
| callback |    N    |  ILoginCallback | 결과를 전달받을 콜백 |

#### ILoginCallback
| Name  |   Type   | Description |
|:------:|:---------:|:-----------|
| OnSuccess |            | 성공시 호출된다. |
| OnError | ErrorResult  | 실패시 호출된다. |


### 2. 결제요청 (JCP.purchase)
* JCP에 결제요청을 합니다.
* step 3-1. 결제별로 고유한 값인 transactionId 를 발급합니다. (개발사 자체생성)
* step 3-2. 발급된 transactionId를 Jehmi Billing Server에 token값을 요청합니다. (server to server)
* step 3-3 Jehmi Billing Server로 부터 발급받은 jehmiAuthToken과 hashData를 결제정보와 함께 SDK에 보냅니다.
```java
JCP.purchase(Context context, 
             String hashData,
             String jehmiAuthToken, 
             String transactionId,
             String userId,
             String amount,
             String country,
             String language,
             String currency,
             String timeZone,
             String itemId,
             String itemName,
             String backUrl,
             IPurchaseCallback callback)
```

#### Request Params
| Name   |   Mandatory  |   Type   | Description |
|:------:|:---------:|:---------:|:-----------|
| hashData |     Y     |  String | 정상요청인지를 확인하기 위한 데이터의 hash |
| jehmiAuthToken |    Y    |  String | CP Application에서 결제시 발급한 transactionId별로 Billing 서버에서 발급된 값 |
| transactionId |     Y     |  String | 결제별 Unique 값 |
| userId |    Y    |  String | 	Application별 유저당 Unique 값 |
| amount |     Y     |  String | 금액 |
| country |    Y    |  String | 국가 코드 (default 개발사 설정) ISO 3166 ALPHA-3 Code |
| currency |     Y     |  String | 통화 코드 (default 없음) ISO 4217 currency code. ISO ALPHA-3 Code |
| itemId |    Y    |  String | 아이템 Unique 식별자 |
| itemName |     Y     |  String | 아이템 이름 (결제시 노출될 수 있음) |
| language |    N    |  String | 언어 코드(default ENG) ISO 639-2/T Code. ISO ALPHA-3 Code |
| timeZone |     N     |  String | 	거래별 해당 지역의 시간대코드로 별도로 설정해서 요청 가능 Jehmi 는 UTC 기준으로 모든 서비스가 실행. 참조용으로 로컬 시간대코드가 필요한 경우 지정가능 (default UTC) |
| backUrl |    N    |  String | 결제완료시 Billing → App Server로 성공, 실패결과 호출(화면없음) |
| callback |     N     |  IPurchaseCallback | 결제결과를 전달받을 콜백 |

#### IPurchaseCallback
| Name   |  Type   | Description |
|:------:|:---------:|:-----------|
| OnSuccess |            | 결제 성공시 호출된다. |
| OnPending |             | PG사 결제 지연상태로 결제가 대기상태. (결제가 추후 성공이나 실패되었을경우  CP App Server로 결과가 전달됩니다) |
| OnError   | ErrorResult | 결제실패시 에러를 전달받는다. |


### 3. 앱 정보요청 (JCP.getAppInfo)
* 등록된 앱에 대한 정보를 요청합니다.
* 응답으로 전달받은 정보를 토대로, 앱을 업데이트 요청을 하실 수 있습니다.
```java
JCP.getAppInfo(IResultCallback<AppInfo> callback)
```

#### IResultCallback
| Name  |   Type   | Description |
|:------:|:---------:|:-----------|
| OnSuccess |   AppInfo  | 성공시 호출된다. |
| OnError | ErrorResult  | 실패시 호출된다. |

#### AppInfo
| Name   |   Type   | Description |
|:------:|:---------:|:-----------|
| cpId |  String | cp 식별자 (cp별 고유값) |
| cpAppId |  Long | cp 앱 식별자 (앱별 고유값) |
| cpAppKey |  String |  |
| signingKey |  String |  |
| packageName |  String | Application package name |
| apkBinaryFileId |  String |  |
| apkVersionName |  String | Android version name |
| apkVersionCode |  String | Android version code |
| apiLevel |  Int | Android software api level |
| apkFileName |  String | Apk file name  |
| apkFileSize |  Long | file size |
| requirementOsVersion |  String | 최소지원 android os version  |


### 4. 다운로드 및 업데이트 요청 (JCP.downloadApk)
* 스토어에 등록되어있는 최신버전 앱의 다운로드를 요청합니다.
* 다운로드가 완료가 되면 자동으로 설치를 진행합니다.
* 다운로드요청 실패에 대한 반환만 존재하며, 실제 설치여부를 반환하지는 않습니다.
* android.permission.WRITE_EXTERNAL_STORAGE 퍼미션이 필요하며, 퍼미션이 없는경우 Error를 반환합니다.
```java
JCP.downloadApk(String title, String description, boolean isOnlyWifi, IErrorCallback callback)
```

#### Request Params
| Name   |   Mandatory  |   Type   | Description |
|:------:|:---------:|:---------:|:-----------|
| title |     Y     |  String | 다운로드시 Notification에 표시될 제목 |
| description |    Y    |  String | 다운로드시 Notification에 표시될 설명 |
| isOnlyWifi |     Y     |  Boolean | Wifi 에서만 다운로드를 할지 여부 |
| callback |    N    |  IErrorCallback | 다운로드 요청을 실패하였을경우 결과값을 전달합니다 (실제 설치여부를 리턴하진 않습니다.) |

#### IErrorCallback
| Name  |   Type   | Description |
|:------:|:---------:|:-----------|
| OnError | ErrorResult  | 실패시 호출된다. |



## [samples](samples) ##
java, kotlin, unity, cocos2d-x 샘플이 제공됩니다.

* [cp-android-java](samples/cp-android-java)
* [cp-android-kotlin](samples/cp-android-kotlin)
* [cp-unity](samples/cp-unity)
* [cp-cocos](samples/cp-cocos)



## [archive](archive) ##
Gradle 프로젝트가 아닐 경우, 해당 라이브러리를 직접 프로젝트 내에 libs로 복사하여 사용하세요.

* common.aar
* cp.aar
* unitypackage
* cpp-bridge.zip
