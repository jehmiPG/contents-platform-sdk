/****************************************************************************
 Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.
 
 http://www.cocos2d-x.org
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#include "HelloWorldScene.h"
#include "SimpleAudioEngine.h"
#include "external/json/rapidjson.h"
#include "external/json/document.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "libnav", __VA_ARGS__)

using namespace jcp;
using namespace cocos2d;
USING_NS_CC;

Scene *HelloWorld::createScene() {
    return HelloWorld::create();
}

// Print useful error message instead of segfaulting when files are not there.
static void problemLoading(const char *filename) {
    printf("Error while loading: %s\n", filename);
    printf("Depending on how you compiled you might have to add 'Resources/' in front of filenames in HelloWorldScene.cpp\n");
}

static jclass globalJcpClass = nullptr;
void initJcpClass() {
    JNIEnv* env = JniHelper::getEnv();
    jclass clazz = env->FindClass(jcp::ClassName);
    globalJcpClass = (jclass) env->NewGlobalRef(clazz);
    env->DeleteLocalRef(clazz);

    jcp::setEngineVersion(env, globalJcpClass, cocos2dVersion());
}

void onRegisterUserIDSuccessCallback() {
    HelloWorld::showToast("register success.");
}

void onRegisterUserIDErrorCallback(std::shared_ptr<ErrorResult> errorResult) {
    LOGD("%s", errorResult->getMessage().c_str());
    HelloWorld::showToast(errorResult->getMessage());
}

void onPurchaseItemSuccess() {
    HelloWorld::showToast("====== purchase success.");
}

void onPurchaseItemPending() {
    HelloWorld::showToast("====== purchase pending.");
}

void onPurchaseItemError(std::shared_ptr<ErrorResult> errorResult) {
    LOGD("%s", errorResult->getMessage().c_str());
    HelloWorld::showToast(errorResult->getMessage());
}

void onGetAppInfoSuccess(std::shared_ptr<AppInfo> appInfo) {
    char result[1024] = {0,};
    sprintf(result, "partnerAppId: %s\npackageName: %s\nappVersionName: %s\nappVersionCode: %d\nminAppVersionCode: %d\nrequirementOsVersion: %s",
            appInfo->getPartnerAppId().c_str(), appInfo->getPackageName().c_str(), appInfo->getAppVersionName().c_str(),
            appInfo->getAppVersionCode(), appInfo->getMinAppVersionCode(), appInfo->getRequirementOsVersion().c_str());
    HelloWorld::showToast(result);
}

void onGetAppInfoError(std::shared_ptr<ErrorResult> errorResult) {
    LOGD("%s", errorResult->getMessage().c_str());
    HelloWorld::showToast(errorResult->getMessage());
}

void onDownloadApkComplete(std::shared_ptr<std::string> path) {
    LOGD("apk downloaded: %s", path->c_str());
    HelloWorld::showToast(std::string("apk downloaded: ").append(*path.get()));
}

void onDownloadApkError(std::shared_ptr<ErrorResult> errorResult) {
    LOGD("%s", errorResult->getMessage().c_str());
    HelloWorld::showToast(errorResult->getMessage());
}

// on "init" you need to initialize your instance
bool HelloWorld::init() {
    //////////////////////////////
    // 1. super init first
    if (!Scene::init()) {
        return false;
    }

    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    /////////////////////////////
    // 2. add a menu item with "X" image, which is clicked to quit the program
    //    you may modify it.

    // add a "close" icon to exit the progress. it's an autorelease object
    auto closeItem = MenuItemImage::create(
            "CloseNormal.png",
            "CloseSelected.png",
            CC_CALLBACK_1(HelloWorld::menuCloseCallback, this));

    if (closeItem == nullptr ||
        closeItem->getContentSize().width <= 0 ||
        closeItem->getContentSize().height <= 0) {
        problemLoading("'CloseNormal.png' and 'CloseSelected.png'");
    } else {
        float x = origin.x + visibleSize.width - closeItem->getContentSize().width / 2;
        float y = origin.y + closeItem->getContentSize().height / 2;
        closeItem->setPosition(Vec2(x, y));
    }

    // create menu, it's an autorelease object
    auto menu = Menu::create(closeItem, NULL);
    menu->setPosition(Vec2::ZERO);
    this->addChild(menu, 1);

    /////////////////////////////
    // 3. add your codes below...
    int yOffset = 0;
    const int fontSize = 25;
    const int margin = 36;

    auto registerUserIdButton = MenuItemFont::create("REGISTER USERID", CC_CALLBACK_1(HelloWorld::onRegisterUserId, this));
    registerUserIdButton->setPosition(Vec2(visibleSize.width / 2, visibleSize.height - yOffset));
    registerUserIdButton->setFontNameObj("fonts/Marker Felt.ttf");
    registerUserIdButton->setFontSizeObj(fontSize);
    yOffset += margin;

    auto purchaseButton = MenuItemFont::create("PURCHASE ITEM", CC_CALLBACK_1(HelloWorld::onPurchaseItem, this));
    purchaseButton->setPosition(Vec2(visibleSize.width / 2, visibleSize.height - yOffset));
    purchaseButton->setFontNameObj("fonts/Marker Felt.ttf");
    purchaseButton->setFontSizeObj(fontSize);
    yOffset += margin;

    auto appInfoButton = MenuItemFont::create("GET APPLICATION INFO", CC_CALLBACK_1(HelloWorld::onGetAppInfo, this));
    appInfoButton->setPosition(Vec2(visibleSize.width / 2, visibleSize.height - yOffset));
    appInfoButton->setFontNameObj("fonts/Marker Felt.ttf");
    appInfoButton->setFontSizeObj(fontSize);
    yOffset += margin;

    auto downloadApkButton = MenuItemFont::create("DOWNLOAD APK", CC_CALLBACK_1(HelloWorld::onDownloadApk, this));
    downloadApkButton->setPosition(Vec2(visibleSize.width / 2, visibleSize.height - yOffset));
    downloadApkButton->setFontNameObj("fonts/Marker Felt.ttf");
    downloadApkButton->setFontSizeObj(fontSize);
    yOffset += margin;

    auto quitButton = MenuItemFont::create("QUIT", CC_CALLBACK_1(HelloWorld::menuCloseCallback, this));
    quitButton->setPosition(Vec2(visibleSize.width / 2, visibleSize.height - yOffset));
    quitButton->setFontNameObj("fonts/Marker Felt.ttf");
    quitButton->setFontSizeObj(fontSize);

    auto actionMenu = Menu::create(registerUserIdButton, purchaseButton, appInfoButton, downloadApkButton, quitButton, NULL);
    actionMenu->setPosition(0, 0);

    this->addChild(actionMenu);

    // init jcp sdk
    initJcpClass();

    return true;
}

const std::string USER_ID = "test_cp_123";

void HelloWorld::onRegisterUserId(Ref* pSender) {
    if (!registerUserID) registerUserID = new RegisterUserID(USER_ID);
    registerUserID->onSuccess = onRegisterUserIDSuccessCallback;
    registerUserID->onError = onRegisterUserIDErrorCallback;

    registerUserID->request(JniHelper::getEnv(), globalJcpClass);
}

void HelloWorld::onPurchaseItem(Ref* pSender) {
    network::HttpRequest *request = new network::HttpRequest();
    request->setUrl("https://alpha-api-cp-test.jehmi.com/v1/authRequest");
    request->setRequestType(network::HttpRequest::Type::POST);

    std::vector<std::string> headers;
    std::string userAgent = "User-Agent:";
    std::string partnerAppId = "partnerAppId:";

    userAgent.append(*getUserAgent(JniHelper::getEnv(), globalJcpClass).get());
    partnerAppId.append(*getPartnerAppId(JniHelper::getEnv(), globalJcpClass).get());

    headers.push_back("Connection:Keep-Alive");
    headers.push_back("Content-Type: application/json; charset=UTF-8");
    headers.push_back(userAgent);
    headers.push_back(partnerAppId);
    request->setHeaders(headers);

    char postData[256] = {0,};
    sprintf(postData, "{\"country\":\"THA\",\"amount\":\"10000\",\"language\":\"ENG\",\"currency\":\"THB\",\"userId\":\"%s\"}", USER_ID.c_str());
    request->setRequestData(postData, strlen(postData));

    request->setResponseCallback(CC_CALLBACK_2(HelloWorld::onHttpRequestCompleted, this));
    network::HttpClient::getInstance()->send(request);
    request->release();
}

void HelloWorld::onGetAppInfo(Ref* pSender) {
    if (!getAppInfo) getAppInfo = new GetAppInfo();
    getAppInfo->onSuccess = onGetAppInfoSuccess;
    getAppInfo->onError = onGetAppInfoError;

    getAppInfo->request(JniHelper::getEnv(), globalJcpClass);
}

void HelloWorld::onDownloadApk(Ref* pSender) {
    if (!downloadApk) downloadApk = new DownloadApk("Sample", "Sample.apk", false);
    downloadApk->onComplete = onDownloadApkComplete;
    downloadApk->onError = onDownloadApkError;

    downloadApk->request(JniHelper::getEnv(), globalJcpClass);
}

void HelloWorld::showToast(std::string message) {
    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t,
                                       "org/cocos2dx/cpp/AppActivity",
                                       "showToast",
                                       "(Ljava/lang/String;)V")) {
        jstring stringArg1 = t.env->NewStringUTF(message.c_str());
        t.env->CallStaticVoidMethod(t.classID,
                                    t.methodID,
                                    stringArg1);

        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg1);
    }
}

void HelloWorld::onHttpRequestCompleted(network::HttpClient *sender, network::HttpResponse *response) {
    std::vector<char> *buffer = response->getResponseData();
    std::string data = std::string(buffer->data(), buffer->size());

    if (response->getResponseCode() != 200) {
        std::string errorMsg = "AuthRequest Failed: ";
        showToast(errorMsg.append(data));
        return;
    }

    rapidjson::Document doc;
    doc.Parse<0>(data.c_str());
    if (doc.HasParseError()) {
        std::string errorMsg = "Json Parse Error: ";
        showToast(errorMsg.append(data));
        return;
    }

    const int status = doc["status"].GetInt();
    if (status != 200) {
        std::string errorMsg = "AuthRequest Failed: ";
        showToast(errorMsg.append(doc["message"].GetString()));
        return;
    }

    std::string hashData = doc["hashData"].GetString();
    std::string jehmiAuthToken = doc["jehmiAuthToken"].GetString();
    std::string transactionId = doc["transactionId"].GetString();
    std::string amount = doc["amount"].GetString();
    std::string country = doc["country"].GetString();
    std::string language = doc["language"].GetString();
    std::string currency = doc["currency"].GetString();
    std::string timeZone = doc["timeZone"].GetString();
    std::string itemId = "Item001";
    std::string itemName = "100 Ruby";
    std::string backUrl = "";

    purchaseItem = new PurchaseItem(hashData,
                                    jehmiAuthToken,
                                    transactionId,
                                    USER_ID,
                                    amount,
                                    country,
                                    language,
                                    currency,
                                    timeZone,
                                    itemId,
                                    itemName,
                                    backUrl);

    purchaseItem->testMode = true;
    purchaseItem->onSuccess = onPurchaseItemSuccess;
    purchaseItem->onPending = onPurchaseItemPending;
    purchaseItem->onError = onPurchaseItemError;

    purchaseItem->request(JniHelper::getEnv(), globalJcpClass);
}

void HelloWorld::menuCloseCallback(Ref *pSender) {
    //Close the cocos2d-x game scene and quit the application
    Director::getInstance()->end();

#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif

    /*To navigate back to native iOS screen(if present) without quitting the application  ,do not use Director::getInstance()->end() and exit(0) as given above,instead trigger a custom event created in RootViewController.mm as below*/

    //EventCustom customEndEvent("game_scene_close_event");
    //_eventDispatcher->dispatchEvent(&customEndEvent);


}
