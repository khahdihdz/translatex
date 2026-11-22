# TranslateX Web

TranslateX is a mobile-first, installable web translator hosted on GitHub Pages. This repository is now web-only; the Android APK project and APK release workflows have been removed.

## Brand assets\n- App icon: `docs/assets/icon.svg`\n- Social sharing preview (1200×630): `docs/assets/social-preview.svg`\n- Open Graph and Twitter Card metadata is configured in `docs/index.html`.\n\n## Features
- Responsive translator UI inspired by the TranslateX mobile demo.
- Gemini AI translation with a user-provided API key.
- Free fallback translation through MyMemory API.
- Multiple languages and translation styles.
- Image OCR in the browser with Tesseract.js (English and Vietnamese OCR packs).
- Copy translation, browser text-to-speech, voice input where supported.
- Local translation history and PWA installation.
- Vietnamese / English interface toggle.

## Deploy
GitHub Pages deploys the `docs/` directory using the workflow at `.github/workflows/pages.yml`.

In repository Settings → Pages, set Source to **GitHub Actions**. Every push to `main` that changes `docs/**` deploys the webapp.

Live site: https://khahdihdz.github.io/translatex/

## Gemini AI
1. Create a personal API key at https://aistudio.google.com/app/apikey.
2. Open TranslateX → Cài đặt Gemini API.
3. Paste the key and save. It is stored in browser localStorage on that device.
4. Select Gemini AI and translate.

The key is sent directly from the browser to Google's Gemini API. TranslateX has no backend and does not proxy requests. Do not save a personal key on shared devices. Google controls quotas, model availability, and data handling. The configured model is `gemini-2.5-flash`.

## Free mode
Free mode uses MyMemory Translation API. It has request and text-size limits and may be unavailable or rate-limited. This mode does not require an API key.

## OCR and privacy
Tesseract.js runs in the browser to extract text from images. Image files are not uploaded to TranslateX. If you then choose to translate the extracted text, that text is sent to the selected translation provider. See [Privacy Policy](docs/privacy.html).

## PWA
The site includes a web manifest and service worker. On supported browsers, use the browser menu → Add to Home screen / Install app. The application shell is cached; online translation and OCR language assets require internet access.
