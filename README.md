# TranslateX

AI-powered multilingual text and image translator for Android.

## Implemented in the first build
- Context-aware AI text translation with Google Gemini API.
- Multiple target languages in one translation request (processed sequentially).
- Translation styles: Natural, Fluent, Literal, Professional, Casual and Technical.
- On-device image-to-text OCR with Google ML Kit Text Recognition (Latin script in this build).
- Offline text translation with Google ML Kit Translation; required language models download on demand.
- Personal Gemini API key encrypted with Android Keystore.
- Vietnamese-first app UI and Vietnamese / English landing page.
- GitHub Actions workflows for APK builds and GitHub Pages deployment.

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

The key is encrypted with Android Keystore and is not bundled into the APK. Do not use a personal key on shared devices. The initial model configured is `gemini-2.5-flash`; if Google changes model availability, update the endpoint in `GeminiTranslator.kt`.

## Offline translation
Choose **Offline ML Kit** in the app. ML Kit downloads the source/target language models when first used. Only languages supported by ML Kit Translation can work offline; this list is separate from Gemini's online language coverage.

## Known limitations in this initial version
- OCR currently uses ML Kit Latin text recognition. CJK and other script-specific OCR models are not wired yet.
- Image overlay/replacing text in the original image is not implemented; OCR text is shown in the editor and can then be translated.
- Camera capture is not yet implemented; choose an existing image from Gallery.
- Recent history is session-only; Room-based persistent history and favorites are follow-up tasks.
- App interface is Vietnamese-first; full in-app English localization is a follow-up task.

## GitHub Pages
Enable GitHub Pages in repository Settings > Pages > Build and deployment > Source: **GitHub Actions**. The site is in `docs/`. After enabling Pages, re-run the Deploy Landing Page workflow or push a change under `docs/`.

## Privacy
OCR runs on-device. Text is sent to Google Gemini only when the user explicitly requests online AI translation. See [Privacy Policy](docs/privacy.html).
