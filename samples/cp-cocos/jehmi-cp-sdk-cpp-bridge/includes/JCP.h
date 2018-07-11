//
// Created by Thomas Jeong on 2018. 4. 11..
//

#ifndef JEHMI_CPP_DEMO_JCP_H
#define JEHMI_CPP_DEMO_JCP_H

#include <string>
#include <functional>
#include <memory>
#include <jni.h>

#include "AppInfo.h"
#include "ErrorResult.h"

namespace jcp {

    using VoidCallback = std::function<void()>;
    using ErrorCallback = std::function<void(std::shared_ptr<ErrorResult>)>;

    using RegisterUserIDSuccessCallback = VoidCallback;
    using RegisterUserIDErrorCallback = ErrorCallback;

    using PurchaseItemSuccessCallback = VoidCallback;
    using PurchaseItemPendingCallback = VoidCallback;
    using PurchaseItemErrorCallback = ErrorCallback;

    using GetAppInfoSuccessCallback = std::function<void(std::shared_ptr<AppInfo>)>;
    using GetAppInfoErrorCallback = ErrorCallback;

    using DownloadApkCompleteCallback = std::function<void(std::shared_ptr<std::string>)>;
    using DownloadApkErrorCallback = ErrorCallback;

    extern const char* const ClassName;

    std::shared_ptr<std::string> getUserAgent(JNIEnv* env, jclass clazz);
    std::shared_ptr<std::string> getPartnerAppId(JNIEnv* env, jclass clazz);
    void setEngineVersion(JNIEnv* env, jclass clazz, std::string version);

    class RegisterUserID {
    public:
        RegisterUserID(std::string userID);
        ~RegisterUserID();

        void request(JNIEnv* env, jclass clazz);

        RegisterUserIDSuccessCallback onSuccess;
        RegisterUserIDErrorCallback onError;

    private:
        std::string userID;
    };

    class PurchaseItem {
    public:
        PurchaseItem(std::string hashData,
                     std::string jehmiAuthToken,
                     std::string transactionId,
                     std::string userId,
                     std::string amount,
                     std::string country,
                     std::string language,
                     std::string currency,
                     std::string timeZone,
                     std::string itemId,
                     std::string itemName,
                     std::string backUrl);
        ~PurchaseItem();

        void request(JNIEnv* env, jclass clazz);

        bool testMode;
        PurchaseItemSuccessCallback onSuccess;
        PurchaseItemPendingCallback onPending;
        PurchaseItemErrorCallback onError;

    private:
        std::string hashData;
        std::string jehmiAuthToken;
        std::string transactionId;
        std::string userId;
        std::string amount;
        std::string country;

        std::string language;
        std::string currency;
        std::string timeZone;
        std::string itemId;
        std::string itemName;
        std::string backUrl;
    };

    class GetAppInfo {
    public:
        GetAppInfo();
        ~GetAppInfo();

        void request(JNIEnv* env, jclass clazz);

        GetAppInfoSuccessCallback onSuccess;
        GetAppInfoErrorCallback onError;
    };

    class DownloadApk {
    public:
        DownloadApk(std::string title, std::string description, bool isOnlyWifi);
        ~DownloadApk();

        void request(JNIEnv* env, jclass clazz);

        DownloadApkCompleteCallback onComplete;
        DownloadApkErrorCallback onError;

    private:
        std::string title;
        std::string description;
        bool isOnlyWifi;
    };
}


#endif //JEHMI_CPP_DEMO_JCP_H
