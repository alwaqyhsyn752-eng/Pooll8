#include <jni.h>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include "detector.h"

#define LOG_TAG "NativeLib"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_aimassist_cv_NativeBridge_nativeDetectBalls(
    JNIEnv* env, jobject, jobject buffer,
    jint width, jint height, jint rowStride,
    jint minRadius, jint maxRadius) {

    void* data = env->GetDirectBufferAddress(buffer);
    if (!data) { LOGE("Direct buffer null"); return env->NewFloatArray(0); }

    cv::Mat frame(height, width, CV_8UC4, data, rowStride);
    auto balls = detectBalls(frame, minRadius, maxRadius);

    jfloatArray out = env->NewFloatArray(balls.size() * 4);
    if (!out) return nullptr;

    std::vector<float> flat;
    flat.reserve(balls.size() * 4);
    for (const auto& b : balls) {
        flat.push_back(b.x); flat.push_back(b.y);
        flat.push_back(b.radius); flat.push_back((float)b.colorCode);
    }
    env->SetFloatArrayRegion(out, 0, flat.size(), flat.data());
    return out;
}
