#include "detector.h"
#include <android/log.h>

#define LOG_TAG "NativeDetector"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

std::vector<DetectedBall> detectBalls(const cv::Mat& rgbaFrame, int minRadius, int maxRadius) {
    std::vector<DetectedBall> result;
    cv::Mat bgr, gray;
    cv::cvtColor(rgbaFrame, bgr, cv::COLOR_RGBA2BGR);
    cv::cvtColor(bgr, gray, cv::COLOR_BGR2GRAY);
    cv::medianBlur(gray, gray, 5);

    std::vector<cv::Vec3f> circles;
    cv::HoughCircles(gray, circles, cv::HOUGH_GRADIENT, 1, minRadius * 2, 100, 30, minRadius, maxRadius);

    for (const auto& c : circles) {
        DetectedBall b;
        b.x = c[0]; b.y = c[1]; b.radius = c[2]; b.colorCode = 4;

        int cx = std::min(std::max(0, (int)c[0]), bgr.cols - 1);
        int cy = std::min(std::max(0, (int)c[1]), bgr.rows - 1);
        cv::Vec3b px = bgr.at<cv::Vec3b>(cy, cx);
        int brightness = (px[0] + px[1] + px[2]) / 3;
        if (brightness > 200) b.colorCode = 0;
        else if (brightness < 40) b.colorCode = 1;
        else b.colorCode = 2;

        result.push_back(b);
    }
    LOGI("Detected %zu balls", result.size());
    return result;
}
