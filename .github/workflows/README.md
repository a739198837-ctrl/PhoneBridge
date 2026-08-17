# Digital Clock — Android APK

هذا المشروع يحتوي على تطبيق ساعة رقمية Kivy جاهز للبناء إلى APK.

## الطريقة الأسهل

1. أنشئ مستودعًا جديدًا على GitHub.
2. ارفع جميع ملفات هذا المشروع.
3. افتح تبويب **Actions**.
4. اختر **Build Android APK**.
5. اضغط **Run workflow**.
6. بعد انتهاء البناء، افتح نتيجة التشغيل وحمّل Artifact باسم:
   `DigitalClock-APK`

## التشغيل محليًا

على Linux يمكن استخدام:

```bash
pip install buildozer
buildozer android debug
```

سيتم إنشاء APK داخل مجلد `bin/`.

ملاحظة:
التطبيق مصمم ليعمل بوضع ملء الشاشة، ويحاول منع إطفاء الشاشة أثناء التشغيل.
