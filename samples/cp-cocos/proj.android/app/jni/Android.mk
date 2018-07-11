LOCAL_PATH := $(call my-dir)

#
# jehmi-cp-sdk-cpp-bridge Shared Library
#
include $(CLEAR_VARS)

LOCAL_MODULE            := jehmi-cp-sdk-cpp-bridge
LOCAL_SRC_FILES         := $(LOCAL_PATH)/../../../jehmi-cp-sdk-cpp-bridge/libs/$(TARGET_ARCH_ABI)/libjehmi-cp-sdk-cpp-bridge.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../../../jehmi-cp-sdk-cpp-bridge/includes

include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

$(call import-add-path,$(LOCAL_PATH)/../../../cocos2d)
$(call import-add-path,$(LOCAL_PATH)/../../../cocos2d/external)
$(call import-add-path,$(LOCAL_PATH)/../../../cocos2d/cocos)
$(call import-add-path,$(LOCAL_PATH)/../../../cocos2d/cocos/audio/include)

LOCAL_MODULE := MyGame_shared

LOCAL_MODULE_FILENAME := libMyGame

LOCAL_SRC_FILES := $(LOCAL_PATH)/hellocpp/main.cpp \
                   $(LOCAL_PATH)/../../../Classes/AppDelegate.cpp \
                   $(LOCAL_PATH)/../../../Classes/HelloWorldScene.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../Classes

# _COCOS_HEADER_ANDROID_BEGIN
# _COCOS_HEADER_ANDROID_END


LOCAL_STATIC_LIBRARIES := cocos2dx_static
LOCAL_SHARED_LIBRARIES := jehmi-cp-sdk-cpp-bridge

# _COCOS_LIB_ANDROID_BEGIN
# _COCOS_LIB_ANDROID_END

include $(BUILD_SHARED_LIBRARY)

$(call import-add-path, $(LOCAL_PATH)/../../../cocos2d)
$(call import-module, cocos)

# _COCOS_LIB_IMPORT_ANDROID_BEGIN
# _COCOS_LIB_IMPORT_ANDROID_END