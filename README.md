# Pooll8

تطبيق Android تعليمي لكشف الكرات وحساب المسارات عبر الرؤية الحاسوبية.

## ⚠️ تنبيه قانوني
مشروع تعليمي فقط. يُمنع استخدامه للغش في أي لعبة لا تملك ترخيصاً لتعديلها.

## الميزات
- التقاط الشاشة (MediaProjection)
- كشف الكرات (OpenCV + NDK)
- حساب المسارات (Billiard Physics)
- Overlay شفاف (WindowManager)

## البناء
1. حمّل OpenCV Android SDK 4.9.0: https://opencv.org/releases/
2. ضعه في `app/src/main/cpp/opencv/`
3. افتح في Android Studio → Run

## البنية
- `capture/` — MediaProjection + ImageReader
- `cv/` — OpenCV عبر JNI
- `physics/` — Billiard Physics
- `overlay/` — WindowManager Overlay
- `service/` — Foreground Service

## الترخيص
MIT — للأغراض التعليمية.
