#ifndef POOLL8_DETECTOR_H
#define POOLL8_DETECTOR_H
#include <opencv2/opencv.hpp>
#include <vector>

struct DetectedBall {
    float x, y, radius;
    int colorCode;
};

std::vector<DetectedBall> detectBalls(const cv::Mat& rgbaFrame, int minRadius, int maxRadius);

#endif
