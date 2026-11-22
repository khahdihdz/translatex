# TranslateX

AI-powered multilingual text and image translator for Android.

## Features
- Natural contextual translation using Gemini API (personal API key).
- Multi-target translation.
- Image OCR on-device with Google ML Kit Text Recognition (Latin script in this first build).
- Personal Gemini API key encrypted with Android Keystore.
- Translation styles: Natural, Fluent, Literal, Professional, Casual and Technical.
- Vietnamese / English landing page on GitHub Pages.

## Build
Requires JDK 17, Android SDK Platform 36 and Gradle 8.13.

```bash
gradle :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Gemini API
1. Create a personal key at https://aistudio.google.com/app/apikey.
2. Open TranslateX > Cài đặt and save the key.
3. Text is sent to Google Gemini only after the user requests online translation.
4. Free tier quotas, data handling and model availability are controlled by Google and may change.

The key is encrypted with Android Keystore and is not bundled into the APK. Do not use a personal key on shared devices.

## GitHub Pages
Enable GitHub Pages in repository Settings > Pages > Build and deployment > GitHub Actions. The site is in `docs/`.

## Current scope
This first implementation includes online AI text translation, multiple target languages, language selection, local image OCR, encrypted API-key storage, and automated APK/Pages workflows. Persistent history, offline ML Kit translation, camera capture, non-Latin OCR model selection and image-overlay reconstruction remain follow-up work; they are not represented as completed features.
