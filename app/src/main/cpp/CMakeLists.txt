cmake_minimum_required(VERSION 3.22.1)
project("pooll8")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv/sdk/native/jni)
find_package(OpenCV REQUIRED)

add_library(native-lib SHARED native-lib.cpp detector.cpp)
target_include_directories(native-lib PRIVATE ${OpenCV_INCLUDE_DIRS})
target_link_libraries(native-lib ${OpenCV_LIBS} android log)
