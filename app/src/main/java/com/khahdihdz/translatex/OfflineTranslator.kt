package com.khahdihdz.translatex

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object OfflineTranslator {
    suspend fun translate(text: String, sourceTag: String, targetTag: String): String {
        val source = TranslateLanguage.fromLanguageTag(sourceTag)
            ?: throw IllegalArgumentException("Ngôn ngữ nguồn chưa được ML Kit hỗ trợ offline: $sourceTag")
        val target = TranslateLanguage.fromLanguageTag(targetTag)
            ?: throw IllegalArgumentException("Ngôn ngữ đích chưa được ML Kit hỗ trợ offline: $targetTag")
        val translator = Translation.getClient(
            TranslatorOptions.Builder().setSourceLanguage(source).setTargetLanguage(target).build()
        )
        return try {
            translator.downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
            translator.translate(text).await()
        } finally {
            translator.close()
        }
    }
}
