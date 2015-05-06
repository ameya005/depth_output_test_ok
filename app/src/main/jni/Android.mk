
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := asus_xiton_depth_output_apk_test
LOCAL_SRC_FILES := asus_xiton_depth_output_apk_test.c
LOCAL_LDLIBS := -llog


include $(BUILD_SHARED_LIBRARY)
