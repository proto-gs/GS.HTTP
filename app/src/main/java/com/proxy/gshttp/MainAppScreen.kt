
package com.proxy.gshttp

import androidx.compose.foundation.clickable
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.text.trim

interface MonochromeColors {
    val bgPrimary: Color
    val bgSecondary: Color
    val bgElevated: Color
    val textPrimary: Color
    val textSecondary: Color
    val accent: Color
    val line: Color
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    themeSetting: String,
    onThemeChange: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isInfoDialogVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    val systemLang = java.util.Locale.getDefault().language
    val initialLang = if (systemLang == "ru") "ru" else "en"
    var currentLanguage by remember { mutableStateOf(initialLang) }

    LaunchedEffect(Unit) {
        try {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            currentLanguage = sharedPref.getString("app_lang", initialLang) ?: initialLang
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val strings = remember(currentLanguage) {
        mapOf(
            "info" to if (currentLanguage == "ru") "Информация" else "Information",
            "dev_site" to if (currentLanguage == "ru") "Сайт разработчика" else "Developer Website",
            "settings" to if (currentLanguage == "ru") "Настройки" else "Settings",
            "settings_title" to if (currentLanguage == "ru") "Настройки приложения" else "App Settings",
            "network_section" to if (currentLanguage == "ru") "Сеть и подключение" else "Network & Connection",
            "ssl_section" to if (currentLanguage == "ru") "Безопасность и SSL" else "Security & SSL",
            "developer" to if (currentLanguage == "ru") "Разработчик: Георгий Смердов" else "Developer: Georgy Smerdov",
            "version" to if (currentLanguage == "ru") "Версия" else "Version",
            "downloaded_from" to if (currentLanguage == "ru") "Скачано: GitHub" else "Downloaded from: GitHub",
            "privacy_policy" to if (currentLanguage == "ru") "Политика конфиденциальности" else "Privacy Policy",
            "source_code" to if (currentLanguage == "ru") "Исходный код" else "Source Code",

            "nav_home" to if (currentLanguage == "ru") "Главная" else "Home",
            "nav_scanner" to if (currentLanguage == "ru") "Сканер URL" else "URL",

            "btn_scan" to if (currentLanguage == "ru") "Запустить скан" else "Run scan",
            "btn_back" to if (currentLanguage == "ru") "← Вернуться" else "← Back",
            "btn_open_browser" to if (currentLanguage == "ru") "Открыть в браузере" else "Open in browser",
            "btn_open_browser_emoji" to if (currentLanguage == "ru") "Открыть сайт в браузере" else "Open site in browser",
            "btn_search_data" to if (currentLanguage == "ru") "Поиск по данным ответа" else "Search response data",
            "btn_done" to if (currentLanguage == "ru") "Готово" else "Done",

            "placeholder_url" to if (currentLanguage == "ru") "Проверить URL" else "Check URL",
            "search_log_placeholder" to if (currentLanguage == "ru") "Поиск текста внутри лога..." else "Search text inside log...",

            "status_error" to if (currentLanguage == "ru") "Ошибка" else "Error",
            "status_invalid" to if (currentLanguage == "ru") "Некорректный ввод" else "Invalid input",
            "status_ssl" to if (currentLanguage == "ru") "Безопасно (SSL)" else "Secure (SSL)",
            "status_http" to if (currentLanguage == "ru") "Небезопасно (HTTP)" else "Unsecure (HTTP)",
            "status_no_server" to if (currentLanguage == "ru") "Сервер недоступен" else "Server unreachable",

            "auto_redirect" to if (currentLanguage == "ru") "Авто-редирект" else "Auto-redirect",
            "auto_redirect_sub" to if (currentLanguage == "ru") "Следовать перенаправлениям сайтов" else "Follow website redirects",
            "timeout_label" to if (currentLanguage == "ru") "Таймаут запроса" else "Request Timeout",
            "verify_ssl" to if (currentLanguage == "ru") "Проверять SSL" else "Verify SSL",
            "verify_ssl_sub" to if (currentLanguage == "ru") "строгая проверка сертификата" else "strict certificate check",
            "ignore_ssl" to if (currentLanguage == "ru") "Игнорировать SSL ошибки" else "Ignore SSL Errors",
            "ignore_ssl_sub" to if (currentLanguage == "ru") "самоподписанные сертификаты" else "self-signed certificates",
            "user_agent" to if (currentLanguage == "ru") "User-Agent" else "User-Agent",
            "user_agent_default" to if (currentLanguage == "ru") "По умолчанию" else "Default",
            "clear_history" to if (currentLanguage == "ru") "Очистить историю ввода" else "Clear input history",

            "theme_btn" to if (currentLanguage == "ru") "Тема оформления" else "App Theme",
            "theme_system" to if (currentLanguage == "ru") "Как в системе" else "System default",
            "theme_light" to if (currentLanguage == "ru") "Светлая" else "Light",
            "theme_dark" to if (currentLanguage == "ru") "Тёмная" else "Dark",

            "lang_btn" to if (currentLanguage == "ru") "Язык" else "Language",
            "lang_title" to if (currentLanguage == "ru") "Выберите язык" else "Select Language",

            "history_title" to if (currentLanguage == "ru") "История запросов" else "Request history",
            "history_empty" to if (currentLanguage == "ru") "История пока пуста" else "History is empty",
            "inspector_title" to if (currentLanguage == "ru") "Данные ответа сервера" else "Server response data",

            "not_found" to if (currentLanguage == "ru") "Ничего не найдено" else "Nothing found",
            "search_error" to if (currentLanguage == "ru") "Ошибка поиска (error)" else "Search error",
            "search_too_big" to if (currentLanguage == "ru") "Ошибка: Лог слишком большой" else "Error: Log is too large",
            "cookies_empty" to if (currentLanguage == "ru") "Куки отсутствуют" else "No cookies present",

            "cancel" to if (currentLanguage == "ru") "Отмена" else "Cancel"
        )
    }

    var currentView by remember { mutableStateOf("welcome") }
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var isSettingsSheetOpen by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var requestTimeout by remember { mutableStateOf(10f) }
    var verifySslSetting by remember { mutableStateOf(true) }
    var ignoreSslSetting by remember { mutableStateOf(false) }

    var isThemeDialogOpen by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var resText by remember { mutableStateOf("") }
    var resTextColor by remember { mutableStateOf(Color.Unspecified) }
    var safeText by remember { mutableStateOf("") }
    var safeTextColor by remember { mutableStateOf(Color.Unspecified) }
    var isLoading by remember { mutableStateOf(false) }

    var searchHistory by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        try {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val savedHistory = sharedPref.getStringSet("local_search_history", emptySet())
            searchHistory = savedHistory?.toSet() ?: emptySet()
        } catch (e: Exception) {
            e.printStackTrace()
            searchHistory = emptySet()
        }
    }

    var responseBodyText by remember { mutableStateOf("") }
    var responseHeadersText by remember { mutableStateOf("") }
    var responseCookiesText by remember { mutableStateOf("") }
    var lastValidUrl by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }

    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val infoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val inspectorSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var isHistorySheetOpen by remember { mutableStateOf(false) }
    var isResponseInspectorSheetOpen by remember { mutableStateOf(false) }

    var isScanPressed by remember { mutableStateOf(false) }
    var activeSearchTab by remember { mutableStateOf("Body") }

    var userAgentInput by remember { mutableStateOf("GS.HTTP/1.0") }
    var requestTimeoutText by remember { mutableStateOf("10") }
    var followRedirectsSetting by remember { mutableStateOf(true) }
    var selectedMethod by remember { mutableStateOf("GET") }

    val scanScale by animateFloatAsState(
        targetValue = if (isScanPressed) 0.9f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(stiffness = 450f, dampingRatio = 0.75f),
        label = "scan_scale"
    )

    LaunchedEffect(Unit) {
        try {
            trackEvent(context, scope, "app_open")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            userAgentInput = sharedPref.getString("user_agent_setting", "GS.HTTP/1.0") ?: "GS.HTTP/1.0"
            requestTimeoutText = sharedPref.getString("timeout_setting", "10") ?: "10"
            followRedirectsSetting = sharedPref.getBoolean("redirects_setting", true)
            selectedMethod = sharedPref.getString("method_setting", "GET") ?: "GET"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isDark = when (themeSetting) {
        "dark" -> true
        "light" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val palette = if (isDark) {
        object : MonochromeColors {
            override val bgPrimary = Color(0xFF000000)
            override val bgSecondary = Color(0xFF111111)
            override val bgElevated = Color(0xFF1E1E1E)
            override val textPrimary = Color(0xFFFFFFFF)
            override val textSecondary = Color(0xFF888888)
            override val accent = Color(0xFFFFFFFF)
            override val line = Color(0xFF333333)
        }
    } else {
        object : MonochromeColors {
            override val bgPrimary = Color(0xFFFFFFFF)
            override val bgSecondary = Color(0xFFF4F4F4)
            override val bgElevated = Color(0xFFE5E5E5)
            override val textPrimary = Color(0xFF000000)
            override val textSecondary = Color(0xFF777777)
            override val accent = Color(0xFF000000)
            override val line = Color(0xFFDDDDDD)
        }
    }

    resTextColor = palette.textPrimary
    safeTextColor = palette.textSecondary

    val switchView: (String) -> Unit = { target ->
        scope.launch {
            try {
                if (target == "main") trackEvent(context, scope, "view_scan_screen")
                currentView = target
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val runScan: () -> Unit = {
        searchQuery = ""
        val url = urlInput.trim()
        val hasSpaces = url.contains(" ")
        val isInvalidProtocol = (url.startsWith("https:/") && !url.startsWith("https://")) ||
                (url.startsWith("http:/") && !url.startsWith("http://"))

        if (url.isEmpty() || hasSpaces || isInvalidProtocol) {
            resText = strings["status_error"] ?: "Error"
            safeText = strings["status_invalid"] ?: "Invalid input"
            isLoading = false
        } else {
            try {
                trackEvent(context, scope, "run_scan_action")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isLoading = true
            resText = ""
            safeText = ""
            responseBodyText = ""
            responseHeadersText = ""
            responseCookiesText = ""
            lastValidUrl = ""

            val updatedHistory = (setOf(url) + searchHistory).take(5).toSet()
            searchHistory = updatedHistory

            scope.launch(Dispatchers.IO) {
                try {
                    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPref.edit().putStringSet("local_search_history", updatedHistory).apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            scope.launch(Dispatchers.IO) {
                val fullUrl = if (url.startsWith("http")) url else "https://$url"
                val currentAgent = if (userAgentInput.isBlank()) "GS.HTTP/1.0" else userAgentInput
                val timeoutLong = requestTimeout.toLong().coerceAtLeast(1L)

                try {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(timeoutLong, TimeUnit.SECONDS)
                        .readTimeout(timeoutLong, TimeUnit.SECONDS)
                        .followRedirects(followRedirectsSetting)
                        .build()

                    val requestBuilder = Request.Builder()
                        .url(fullUrl)
                        .header("User-Agent", currentAgent)

                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val emptyBody = ByteArray(0).toRequestBody(mediaType)

                    when (selectedMethod) {
                        "GET" -> requestBuilder.get()
                        "POST" -> requestBuilder.post(emptyBody)
                        "PUT" -> requestBuilder.put(emptyBody)
                        "HEAD" -> requestBuilder.head()
                    }

                    client.newCall(requestBuilder.build()).execute().use { response ->
                        val code = response.code
                        val isHttps = response.request.url.isHttps
                        val body = response.body?.string() ?: ""
                        val headers = response.headers.joinToString("\n") { "${it.first}: ${it.second}" }
                        val cookiesList = response.headers("Set-Cookie")
                        val cookies = if (cookiesList.isNotEmpty()) cookiesList.joinToString("\n") else strings["cookies_empty"] ?: ""

                        withContext(Dispatchers.Main) {
                            resText = "HTTP $code"
                            safeText = if (isHttps) strings["status_ssl"] ?: "" else strings["status_http"] ?: ""
                            responseBodyText = body
                            responseHeadersText = headers
                            responseCookiesText = cookies
                            lastValidUrl = fullUrl
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    withContext(Dispatchers.Main) {
                        resText = strings["status_error"] ?: "Error"
                        safeText = strings["status_invalid"] ?: "Invalid input"
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        resText = strings["status_error"] ?: "Error"
                        safeText = strings["status_no_server"] ?: "Server unreachable"
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        resText = strings["status_error"] ?: "Error"
                        safeText = strings["status_error"] ?: "Error"
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                    }
                }
            }
        }
    }

    var dragAmountSum by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bgPrimary)
            .pointerInput(currentView) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAmountSum = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragAmountSum += dragAmount
                    },
                    onDragEnd = {
                        val threshold = 50f
                        if (dragAmountSum > threshold) {
                            if (currentView == "main") {
                                switchView("welcome")
                            }
                        } else if (dragAmountSum < -threshold) {
                            if (currentView == "welcome") {
                                switchView("main")
                            }
                        }
                    }
                )
            }
    ) {
        if (currentView == "welcome") {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 8.dp, end = 24.dp)
                ) {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Показать меню",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.bgSecondary)
                            .border(1.dp, palette.line, RoundedCornerShape(16.dp)),
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings["info"] ?: "Information", color = palette.textPrimary) },
                            onClick = { isMenuExpanded = false; isInfoDialogVisible = true }
                        )
                        DropdownMenuItem(
                            text = { Text(strings["dev_site"] ?: "Developer Website", color = palette.textPrimary) },
                            onClick = {
                                isMenuExpanded = false
                                try {
                                    trackEvent(context, scope, "click_dev_site")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gs-ht.ru"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings["settings"] ?: "Settings", color = palette.textPrimary) },
                            onClick = { isMenuExpanded = false; isSettingsSheetOpen = true }
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(320.dp)
                        .align(Alignment.Center)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(palette.textPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "GS",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.bgPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "GS HTTP",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = palette.textPrimary
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else if (currentView == "main") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isHistorySheetOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { isSettingsSheetOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val methodsList = listOf("GET", "POST", "HEAD", "PUT")
                    val currentMethodIndex = methodsList.indexOf(selectedMethod).coerceAtLeast(0)
                    TabRow(
                        selectedTabIndex = currentMethodIndex,
                        containerColor = Color.Transparent,
                        contentColor = palette.textPrimary,
                        indicator = { tabPositions ->
                            if (currentMethodIndex < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    modifier = with(TabRowDefaults) {
                                        Modifier.tabIndicatorOffset(tabPositions[currentMethodIndex])
                                    },
                                    color = palette.textPrimary,
                                    height = 3.dp
                                )
                            }
                        },
                        divider = {}
                    ) {
                        methodsList.forEachIndexed { index, method ->
                            val isSelected = currentMethodIndex == index
                            Tab(
                                selected = isSelected,
                                onClick = { selectedMethod = method },
                                text = {
                                    Text(
                                        text = method,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) palette.textPrimary else palette.textSecondary
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(35.dp))

                    TextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        placeholder = { Text(strings["placeholder_url"] ?: "Check URL", color = palette.textSecondary.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = palette.textPrimary,
                            unfocusedIndicatorColor = palette.line,
                            focusedTextColor = palette.textPrimary,
                            unfocusedTextColor = palette.textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(50.dp))

                    if (!isLoading && resText.isNotEmpty()) {
                        Text(resText, fontSize = 28.sp, fontWeight = FontWeight.Black, color = resTextColor, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(safeText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = safeTextColor, textAlign = TextAlign.Center)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLoading && lastValidUrl.isNotEmpty()) {
                        Button(
                            onClick = {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(lastValidUrl))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) { e.printStackTrace() }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = palette.bgElevated, contentColor = palette.textPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.width(320.dp).height(56.dp).border(1.dp, palette.line, RoundedCornerShape(16.dp))
                        ) {
                            Text(strings["btn_open_browser"] ?: "Open in browser", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { isResponseInspectorSheetOpen = true },
                            modifier = Modifier.width(320.dp).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.bgSecondary, contentColor = palette.textPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(strings["btn_search_data"] ?: "Search response data", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (isLoading) {
                        Box(modifier = Modifier.height(56.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = palette.textPrimary, strokeWidth = 4.dp, modifier = Modifier.size(36.dp))
                        }
                    } else {
                        AnimatedButton(
                            text = strings["btn_scan"] ?: "Run scan",
                            textColor = palette.bgPrimary,
                            bgColor = palette.textPrimary,
                            scale = scanScale,
                            onPressDown = { isScanPressed = true },
                            onPressUp = { isScanPressed = false }
                        ) { runScan() }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(32.dp))
                    .background(
                        color = palette.bgSecondary,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(1.dp, palette.line, RoundedCornerShape(32.dp))
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isHomeSelected = currentView == "welcome"
                Button(
                    onClick = { switchView("welcome") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHomeSelected) palette.bgElevated else Color.Transparent,
                        contentColor = if (isHomeSelected) palette.textPrimary else palette.textSecondary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings["nav_home"] ?: "Home",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                val isScannerSelected = currentView == "main"
                Button(
                    onClick = { switchView("main") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScannerSelected) palette.bgElevated else Color.Transparent,
                        contentColor = if (isScannerSelected) palette.textPrimary else palette.textSecondary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings["nav_scanner"] ?: "URL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

        if (isInfoDialogVisible) {
            InfoBottomSheetDialog(
                version = VERSION,
                sheetState = infoSheetState,
                strings = strings,
                bgColor = palette.bgSecondary,
                textColorPrimary = palette.textPrimary,
                textColorSecondary = palette.textSecondary,
                lineColor = palette.line,
                uriHandler = uriHandler,
                onDismiss = {
                    isInfoDialogVisible = false
                }
            )
        }

        if (isSettingsSheetOpen) {
            LaunchedEffect(Unit) {
                try {
                    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    followRedirectsSetting = sharedPref.getBoolean("redirects_setting", true)
                    requestTimeout = sharedPref.getFloat("timeout_setting", 10f)
                    verifySslSetting = sharedPref.getBoolean("verify_ssl_setting", true)
                    ignoreSslSetting = sharedPref.getBoolean("ignore_ssl_setting", false)
                    userAgentInput = sharedPref.getString("user_agent_setting", "GS.HTTP/1.0") ?: "GS.HTTP/1.0"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            ModalBottomSheet(
                onDismissRequest = { isSettingsSheetOpen = false },
                sheetState = settingsSheetState,
                containerColor = palette.bgSecondary,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.textPrimary.copy(alpha = 0.2f))
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings["settings_title"] ?: "App Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = palette.textPrimary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(strings["network_section"] ?: "", fontSize = 12.sp, color = palette.textSecondary, fontWeight = FontWeight.SemiBold)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = strings["auto_redirect"] ?: "", color = palette.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    Text(text = strings["auto_redirect_sub"] ?: "", color = palette.textSecondary, fontSize = 12.sp)
                                }
                                Switch(
                                    checked = followRedirectsSetting,
                                    onCheckedChange = { newValue ->
                                        followRedirectsSetting = newValue
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putBoolean("redirects_setting", newValue)
                                                    .apply()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = palette.bgPrimary,
                                        checkedTrackColor = palette.textPrimary,
                                        uncheckedThumbColor = palette.textSecondary,
                                        uncheckedTrackColor = palette.bgElevated
                                    )
                                )
                            }
                        }

                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "${strings["timeout_label"]}: ${requestTimeout.toInt()}с", color = palette.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                Slider(
                                    value = requestTimeout,
                                    onValueChange = { newValue ->
                                        requestTimeout = newValue
                                    },
                                    onValueChangeFinished = {
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putFloat("timeout_setting", requestTimeout)
                                                    .apply()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    },
                                    valueRange = 1f..30f,
                                    steps = 29
                                )
                            }
                        }
                        item { Divider(color = palette.line, thickness = 0.5.dp) }
                        item {
                            Text(strings["ssl_section"] ?: "", fontSize = 12.sp, color = palette.textSecondary, fontWeight = FontWeight.SemiBold)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = strings["verify_ssl"] ?: "", color = palette.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    Text(text = strings["verify_ssl_sub"] ?: "", color = palette.textSecondary, fontSize = 12.sp)
                                }
                                Switch(
                                    checked = verifySslSetting,
                                    onCheckedChange = { newValue ->
                                        verifySslSetting = newValue
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putBoolean("verify_ssl_setting", newValue)
                                                    .apply()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = palette.bgPrimary,
                                        checkedTrackColor = palette.textPrimary,
                                        uncheckedThumbColor = palette.textSecondary,
                                        uncheckedTrackColor = palette.bgElevated
                                    )
                                )
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = strings["ignore_ssl"] ?: "", color = palette.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    Text(text = strings["ignore_ssl_sub"] ?: "", color = palette.textSecondary, fontSize = 12.sp)
                                }
                                Switch(
                                    checked = ignoreSslSetting,
                                    onCheckedChange = { newValue ->
                                        ignoreSslSetting = newValue
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putBoolean("ignore_ssl_setting", newValue)
                                                    .apply()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = palette.bgPrimary,
                                        checkedTrackColor = palette.textPrimary,
                                        uncheckedThumbColor = palette.textSecondary,
                                        uncheckedTrackColor = palette.bgElevated
                                    )
                                )
                            }
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = strings["user_agent"] ?: "User-Agent",
                                        color = palette.textPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = "По умолчанию",
                                        color = palette.accent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.clickable {
                                            userAgentInput = "GS.HTTP/1.0"
                                            scope.launch(Dispatchers.IO) {
                                                try {
                                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                                        .edit()
                                                        .putString("user_agent_setting", "GS.HTTP/1.0")
                                                        .apply()
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    )
                                }

                                TextField(
                                    value = userAgentInput,
                                    onValueChange = { newValue ->
                                        userAgentInput = newValue
                                        val finalAgent = if (newValue.isBlank()) "GS.HTTP/1.0" else newValue
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString("user_agent_setting", finalAgent)
                                                    .apply()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    },
                                    placeholder = { Text("GS.HTTP/1.0", color = palette.textSecondary.copy(alpha = 0.7f)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = palette.bgPrimary,
                                        unfocusedContainerColor = palette.bgPrimary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = palette.textPrimary,
                                        unfocusedTextColor = palette.textPrimary
                                    )
                                )
                            }
                        }

                        item { Divider(color = palette.line, thickness = 0.5.dp) }

                        item {
                            Button(
                                onClick = { isThemeDialogOpen = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.bgElevated, contentColor = palette.textPrimary)
                            ) {
                                val themeText = when(themeSetting) {
                                    "dark" -> strings["theme_dark"] ?: "Dark"
                                    "light" -> strings["theme_light"] ?: "Light"
                                    else -> strings["theme_system"] ?: "System default"
                                }
                                Text(text = "${strings["theme_btn"] ?: "App Theme"}: $themeText", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                        item {
                            Button(
                                onClick = { isLanguageDialogOpen = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.bgElevated, contentColor = palette.textPrimary)
                            ) {
                                val langText = if (currentLanguage == "ru") "Русский" else "English"
                                Text(text = "${strings["lang_btn"] ?: "Language"}: $langText", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                        item {
                            Button(
                                onClick = {
                                    urlInput = ""
                                    resText = ""
                                    safeText = ""
                                    searchHistory = emptySet()
                                    try {
                                        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                        sharedPref.edit().remove("local_search_history").apply()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    isSettingsSheetOpen = false
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.bgElevated, contentColor = palette.textPrimary)
                            ) {
                                Text(text = strings["clear_history"] ?: "Clear input history", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { isSettingsSheetOpen = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.textPrimary, contentColor = palette.bgPrimary)
                    ) {
                        Text(text = strings["btn_done"] ?: "Done", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        if (isHistorySheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isHistorySheetOpen = false },
                sheetState = historySheetState,
                containerColor = palette.bgSecondary,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(10.dp)).background(palette.textPrimary.copy(alpha = 0.2f)))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = strings["history_title"] ?: "Request history", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(20.dp))
                    if (searchHistory.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(text = strings["history_empty"] ?: "History is empty", fontSize = 14.sp, color = palette.textSecondary)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(searchHistory.toList()) { _, historyUrl ->
                                Button(
                                    onClick = { urlInput = historyUrl; isHistorySheetOpen = false },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.bgElevated, contentColor = palette.textPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = palette.textSecondary,
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .size(20.dp)
                                        )
                                        Text(
                                            text = historyUrl,
                                            maxLines = 1,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = palette.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isResponseInspectorSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isResponseInspectorSheetOpen = false },
                sheetState = inspectorSheetState,
                containerColor = palette.bgSecondary,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(10.dp)).background(palette.textPrimary.copy(alpha = 0.2f)))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(strings["inspector_title"] ?: "Server response data", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(lastValidUrl))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) { e.printStackTrace() }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = palette.bgElevated, contentColor = palette.textPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).border(1.dp, palette.line, RoundedCornerShape(12.dp))
                    ) {
                        Text(strings["btn_open_browser_emoji"] ?: "Open site in browser", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().background(palette.bgElevated, RoundedCornerShape(8.dp)).padding(2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Body", "Headers", "Cookies").forEach { tab ->
                            val isSelected = activeSearchTab == tab
                            TextButton(
                                onClick = { activeSearchTab = tab },
                                modifier = Modifier.weight(1f).background(if (isSelected) palette.bgPrimary else Color.Transparent, RoundedCornerShape(6.dp))
                            ) {
                                Text(tab, color = if (isSelected) palette.textPrimary else palette.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text(strings["search_log_placeholder"] ?: "Search text inside log...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = palette.bgPrimary,
                            unfocusedContainerColor = palette.bgPrimary,
                            focusedTextColor = palette.textPrimary,
                            unfocusedTextColor = palette.textPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val currentTextData = when (activeSearchTab) {
                        "Body" -> responseBodyText
                        "Headers" -> responseHeadersText
                        "Cookies" -> responseCookiesText
                        else -> ""
                    }
                    val filteredText = if (searchQuery.isEmpty()) {
                        currentTextData
                    } else {
                        try {
                            if (currentTextData.length > 500_000) {
                                strings["search_too_big"] ?: "Error: Log is too large"
                            } else {
                                currentTextData.lines().filter { it.contains(searchQuery, ignoreCase = true) }.joinToString("\n")
                            }
                        } catch (e: Exception) { strings["search_error"] ?: "error" }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f).background(palette.bgPrimary, RoundedCornerShape(16.dp))
                            .border(1.dp, palette.line, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = if (filteredText.trim().isEmpty()) {
                                        if (searchQuery.isNotEmpty()) (strings["not_found"] ?: "Nothing found")
                                        else if (activeSearchTab == "Cookies") (strings["cookies_empty"] ?: "No cookies")
                                        else if (activeSearchTab == "Body") (if (currentLanguage == "ru") "Тело ответа пустое" else "Response body is empty")
                                        else (if (currentLanguage == "ru") "Нет данных" else "No data")
                                    } else filteredText,
                                    color = palette.textPrimary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (isThemeDialogOpen) {
            AlertDialog(
                onDismissRequest = { isThemeDialogOpen = false },
                title = { Text("Выберите тему", color = palette.textPrimary) },
                text = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            RadioButton(
                                selected = themeSetting == "system",
                                onClick = { onThemeChange("system"); isThemeDialogOpen = false },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.textPrimary, unselectedColor = palette.textSecondary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Как в системе", color = palette.textPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            RadioButton(
                                selected = themeSetting == "light",
                                onClick = { onThemeChange("light"); isThemeDialogOpen = false },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.textPrimary, unselectedColor = palette.textSecondary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Светлая", color = palette.textPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        RadioButton(
                            selected = themeSetting == "dark",
                            onClick = { onThemeChange("dark"); isThemeDialogOpen = false },
                            colors = RadioButtonDefaults.colors(selectedColor = palette.textPrimary, unselectedColor = palette.textSecondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Тёмная", color = palette.textPrimary)
                    }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isThemeDialogOpen = false }) { Text("Отмена", color = palette.textPrimary) }
                },
                containerColor = palette.bgSecondary,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }

    if (isLanguageDialogOpen) {
        AlertDialog(
            onDismissRequest = { isLanguageDialogOpen = false },
            title = { Text(strings["lang_title"] ?: "", color = palette.textPrimary) },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        RadioButton(
                            selected = currentLanguage == "ru",
                            onClick = {
                                currentLanguage = "ru"
                                try {
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putString("app_lang", "ru").apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                isLanguageDialogOpen = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = palette.textPrimary, unselectedColor = palette.textSecondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Русский", color = palette.textPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        RadioButton(
                            selected = currentLanguage == "en",
                            onClick = {
                                currentLanguage = "en"
                                try {
                                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putString("app_lang", "en").apply()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                isLanguageDialogOpen = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = palette.textPrimary, unselectedColor = palette.textSecondary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("English", color = palette.textPrimary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isLanguageDialogOpen = false }) { Text(strings["cancel"] ?: "", color = palette.textPrimary) }
            },
            containerColor = palette.bgSecondary,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

