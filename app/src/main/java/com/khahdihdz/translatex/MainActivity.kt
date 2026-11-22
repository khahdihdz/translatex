package com.khahdihdz.translatex

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private data class Language(val tag: String, val label: String)
private val languages = listOf(
    Language("auto", "Tự nhận diện / Auto"), Language("vi", "Tiếng Việt"), Language("en", "English"),
    Language("zh", "中文 (简体)"), Language("zh-TW", "中文 (繁體)"), Language("ja", "日本語"),
    Language("ko", "한국어"), Language("th", "ไทย"), Language("fr", "Français"),
    Language("de", "Deutsch"), Language("es", "Español"), Language("pt", "Português"),
    Language("ru", "Русский"), Language("id", "Bahasa Indonesia"), Language("hi", "हिन्दी"),
    Language("ar", "العربية"), Language("it", "Italiano"), Language("nl", "Nederlands"),
    Language("tr", "Türkçe"), Language("pl", "Polski"), Language("uk", "Українська")
)
private val styles = listOf("Natural", "Fluent", "Literal", "Professional", "Casual", "Technical")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TranslateXApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TranslateXApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var source by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var sourceTag by remember { mutableStateOf("auto") }
    var targets by remember { mutableStateOf(setOf("vi")) }
    var style by remember { mutableStateOf("Natural") }
    var mode by remember { mutableStateOf("AI") }
    var apiKey by remember { mutableStateOf(SecureKeyStore.read(context)) }
    var showSettings by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Dịch tự nhiên bằng AI · Translate naturally with AI") }
    var history by remember { mutableStateOf(listOf<String>()) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) scope.launch {
            loading = true
            status = "Đang nhận diện văn bản trong ảnh…"
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val text = try { recognizer.process(image).await().text } finally { recognizer.close() }
                if (text.isBlank()) status = "Không tìm thấy văn bản trong ảnh."
                else { source = text; status = "Đã nhận diện văn bản. Bạn có thể chỉnh sửa trước khi dịch." }
            } catch (e: Exception) {
                status = "Không đọc được ảnh: ${e.localizedMessage ?: "Lỗi không xác định"}"
            } finally { loading = false }
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF3157D5), secondary = androidx.compose.ui.graphics.Color(0xFF5868A8))) {
        Scaffold(topBar = {
            TopAppBar(title = { Column {
                Text("TranslateX", fontWeight = FontWeight.Bold)
                Text("AI Translator · Dịch đa ngôn ngữ", style = MaterialTheme.typography.labelSmall)
            } }, actions = { TextButton(onClick = { showSettings = true }) { Text("Cài đặt") } })
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Dịch tự nhiên, hiểu đúng ngữ cảnh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Natural, context-aware translation across languages.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                LanguageDropdown("Ngôn ngữ nguồn", sourceTag) { sourceTag = it }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Chọn ngôn ngữ đích (có thể chọn nhiều)", style = MaterialTheme.typography.titleSmall)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    languages.filter { it.tag != "auto" }.forEach { lang ->
                        FilterChip(selected = lang.tag in targets, onClick = {
                            targets = if (lang.tag in targets) targets - lang.tag else targets + lang.tag
                        }, label = { Text(lang.label) })
                    }
                }
                var styleExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = styleExpanded, onExpandedChange = { styleExpanded = it }) {
                    OutlinedTextField(value = style, onValueChange = {}, readOnly = true, label = { Text("Văn phong") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(styleExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = styleExpanded, onDismissRequest = { styleExpanded = false }) {
                        styles.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { style = option; styleExpanded = false }) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = mode == "AI", onClick = { mode = "AI" }, label = { Text("Gemini AI") })
                    FilterChip(selected = mode == "Offline", onClick = { mode = "Offline" }, label = { Text("Offline ML Kit") })
                }
                OutlinedTextField(value = source, onValueChange = { source = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                    label = { Text("Văn bản cần dịch / Text to translate") }, placeholder = { Text("Nhập hoặc dán nội dung tại đây…") }, minLines = 5)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Image, contentDescription = null); Spacer(Modifier.width(6.dp)); Text("Dịch ảnh / OCR")
                    }
                    Button(onClick = {
                        if (source.isBlank() || targets.isEmpty()) status = "Nhập nội dung và chọn ít nhất một ngôn ngữ đích."
                        else if (mode == "AI" && apiKey.isBlank()) { showSettings = true; status = "Thêm Gemini API Key để sử dụng AI." }
                        else scope.launch {
                            loading = true; result = ""; status = "Đang dịch…"
                            try {
                                val detected = if (sourceTag == "auto") LanguageIdentification.getClient().identifyLanguage(source).await() else sourceTag
                                val sourceName = languages.firstOrNull { it.tag == detected }?.label ?: detected
                                val output = mutableListOf<String>()
                                targets.forEach { target ->
                                    val targetName = languages.firstOrNull { it.tag == target }?.label ?: target
                                    val translated = if (mode == "Offline") OfflineTranslator.translate(source, detected, target) else GeminiTranslator.translate(apiKey, source, sourceName, targetName, style)
                                    output += "【$targetName】\n$translated"
                                }
                                result = output.joinToString("\n\n")
                                status = "Hoàn tất ${targets.size} bản dịch."
                                history = (listOf(source.take(80)) + history).take(20)
                            } catch (e: Exception) { status = e.localizedMessage ?: "Không thể dịch lúc này." }
                            finally { loading = false }
                        }
                    }, enabled = !loading, modifier = Modifier.weight(1f)) {
                        if (loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Dịch bằng AI")
                    }
                }
                Text(status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                if (result.isNotBlank()) Card {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Kết quả dịch", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("TranslateX", result)); status = "Đã sao chép kết quả."
                            }) { Icon(Icons.Default.ContentCopy, contentDescription = null); Text("Copy") }
                        }
                        Text(result)
                    }
                }
                if (history.isNotEmpty()) {
                    Text("Gần đây", style = MaterialTheme.typography.titleSmall)
                    history.forEach { item -> TextButton(onClick = { source = item }) { Text(item, maxLines = 1) } }
                }
                Text("Gemini AI cần API Key cá nhân và Internet. Offline dùng mô hình ML Kit tải về theo từng cặp ngôn ngữ; danh sách hỗ trợ có thể khác AI online. OCR xử lý trên thiết bị.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (showSettings) AlertDialog(onDismissRequest = { showSettings = false }, title = { Text("Cài đặt AI") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("API Key được mã hóa bằng Android Keystore và chỉ lưu trên thiết bị.")
                OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("Gemini API Key") }, singleLine = true)
                TextButton(onClick = { apiKey = ""; SecureKeyStore.save(context, "") }) { Text("Xóa API Key") }
                Text("Lấy khóa tại aistudio.google.com", style = MaterialTheme.typography.bodySmall)
            }
        }, confirmButton = { TextButton(onClick = { SecureKeyStore.save(context, apiKey); showSettings = false }) { Text("Lưu") } },
            dismissButton = { TextButton(onClick = { showSettings = false }) { Text("Đóng") } })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(label: String, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val current = languages.firstOrNull { it.tag == selected }?.label ?: selected
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(value = current, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { lang -> DropdownMenuItem(text = { Text(lang.label) }, onClick = { onSelect(lang.tag); expanded = false }) }
        }
    }
}
