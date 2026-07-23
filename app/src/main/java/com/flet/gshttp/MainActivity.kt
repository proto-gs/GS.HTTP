package com.flet.gshttp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flet.gshttp.ui.theme.MyApplicationTheme
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Divider
import androidx.compose.material.icons.filled.History



const val VERSION = "1.0.4"
const val SDK_ID = "YOUR_SDK_ID"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var appThemeSetting by remember { mutableStateOf("system") }

            val isDarkTheme = when (appThemeSetting) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainAppScreen(
                        themeSetting = appThemeSetting,
                        onThemeChange = { appThemeSetting = it }
                    )
                }
            }
        }
    }
}
fun trackEvent(context: Context, scope: CoroutineScope, eventName: String) {
    scope.launch(Dispatchers.IO) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var lvid = sharedPref.getString("user_lvid", null)

        if (lvid == null) {
            lvid = UUID.randomUUID().toString()
            sharedPref.edit().putString("user_lvid", lvid).apply()
        }

        val url = "https://my.com"
        val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build()

        val jsonArray = JSONArray().apply {
            put(JSONObject().apply {
                put("idApp", SDK_ID)
                put("customEventName", eventName)
                put("lvid", lvid)
                put("ts", System.currentTimeMillis() / 1000)
            })
        }

        val rootJson = JSONObject().apply { put("obj", jsonArray) }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = rootJson.toString().toRequestBody(mediaType)

        val request = Request.Builder().url(url).post(body).build()
        try { client.newCall(request).execute().close() } catch (e: Exception) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    themeSetting: String,
    onThemeChange: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    val systemLang = java.util.Locale.getDefault().language
    val initialLang = if (systemLang == "ru") "ru" else "en"
    var currentLanguage by remember { mutableStateOf(initialLang) }

    LaunchedEffect(Unit) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        currentLanguage = sharedPref.getString("app_lang", initialLang) ?: initialLang
    }

    val strings = remember(currentLanguage) {
        mapOf(

            "info" to if (currentLanguage == "ru") "Информация" else "Information",
            "dev_site" to if (currentLanguage == "ru") "Сайт разработчика" else "Developer Website",
            "settings" to if (currentLanguage == "ru") "Настройки" else "Settings",
            "developer" to if (currentLanguage == "ru") "Разработчик: Георгий Смердов" else "Developer: Georgy Smerdov",
            "version" to if (currentLanguage == "ru") "Версия" else "Version",
            "downloaded_from" to if (currentLanguage == "ru") "Скачано: RuStore" else "Downloaded from: RuStore",
            "privacy_policy" to if (currentLanguage == "ru") "Политика конфиденциальности" else "Privacy Policy",
            "source_code" to if (currentLanguage == "ru") "Исходный код" else "Source Code",




            "btn_start" to if (currentLanguage == "ru") "ПРИСТУПИТЬ" else "GET STARTED",
            "btn_scan" to if (currentLanguage == "ru") "ЗАПУСТИТЬ СКАН" else "RUN SCAN",
            "btn_back" to if (currentLanguage == "ru") "← ВЕРНУТЬСЯ" else "← BACK",
            "btn_open_browser" to if (currentLanguage == "ru") "ОТКРЫТЬ В БРАУЗЕРЕ" else "OPEN IN BROWSER",
            "btn_open_browser_emoji" to if (currentLanguage == "ru") "ОТКРЫТЬ САЙТ В БРАУЗЕРЕ 🌐" else "OPEN SITE IN BROWSER 🌐",
            "btn_search_data" to if (currentLanguage == "ru") "ПОИСК ПО ДАННЫМ ОТВЕТА" else "SEARCH RESPONSE DATA",


            "placeholder_url" to if (currentLanguage == "ru") "Проверить URL" else "Check URL",
            "search_log_placeholder" to if (currentLanguage == "ru") "Поиск текста внутри лога..." else "Search text inside log...",


            "status_error" to if (currentLanguage == "ru") "ОШИБКА" else "ERROR",
            "status_invalid" to if (currentLanguage == "ru") "НЕКОРРЕКТНЫЙ ВВОД" else "INVALID INPUT",
            "status_ssl" to if (currentLanguage == "ru") "БЕЗОПАСНО (SSL)" else "SECURE (SSL)",
            "status_http" to if (currentLanguage == "ru") "НЕБЕЗОПАСНО (HTTP)" else "UNSECURE (HTTP)",
            "status_no_server" to if (currentLanguage == "ru") "СЕРВЕР НЕДОСТУПЕН" else "SERVER UNREACHABLE",


            "auto_redirect" to if (currentLanguage == "ru") "Авто-редирект" else "Auto-redirect",
            "auto_redirect_sub" to if (currentLanguage == "ru") "Следовать перенаправлениям сайтов" else "Follow website redirects",
            "clear_history" to if (currentLanguage == "ru") "Очистить историю ввода" else "Clear input history",


            "theme_btn" to if (currentLanguage == "ru") "Тема оформления" else "App Theme",
            "theme_system" to if (currentLanguage == "ru") "Как в системе" else "System default",
            "theme_light" to if (currentLanguage == "ru") "Светлая" else "Light",
            "theme_dark" to if (currentLanguage == "ru") "Тёмная" else "Dark",


            "lang_btn" to if (currentLanguage == "ru") "Язык / Language" else "Language / Язык",
            "lang_title" to if (currentLanguage == "ru") "Выберите язык" else "Select Language",


            "history_title" to if (currentLanguage == "ru") "ИСТОРИЯ ЗАПРОСОВ" else "REQUEST HISTORY",
            "history_empty" to if (currentLanguage == "ru") "История пока пуста" else "History is empty",
            "inspector_title" to if (currentLanguage == "ru") "ДАННЫЕ ОТВЕТА СЕРВЕРА" else "SERVER RESPONSE DATA",


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
    var followRedirectsSetting by remember { mutableStateOf(true) }
    var isThemeDialogOpen by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var resText by remember { mutableStateOf("") }
    var resTextColor by remember { mutableStateOf(Color.White) }
    var safeText by remember { mutableStateOf("") }
    var safeTextColor by remember { mutableStateOf(Color.White) }
    var isLoading by remember { mutableStateOf(false) }

    var selectedMethod by remember { mutableStateOf("GET") }
    var isMethodMenuExpanded by remember { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf(setOf<String>()) }


    LaunchedEffect(Unit) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedHistory = sharedPref.getStringSet("local_search_history", emptySet()) ?: emptySet()
        searchHistory = savedHistory
    }


    var responseBodyText by remember { mutableStateOf("") }
    var responseHeadersText by remember { mutableStateOf("") }
    var responseCookiesText by remember { mutableStateOf("") }
    var lastValidUrl by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var activeSearchTab by remember { mutableStateOf("BODY") }

    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val infoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val inspectorSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var isHistorySheetOpen by remember { mutableStateOf(false) }
    var isResponseInspectorSheetOpen by remember { mutableStateOf(false) }

    var isWelcomePressed by remember { mutableStateOf(false) }
    var isScanPressed by remember { mutableStateOf(false) }


    val welcomeScale by animateFloatAsState(
        targetValue = if (isWelcomePressed) 0.95f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            stiffness = 450f,
            dampingRatio = 0.75f
        ),
        label = "welcome_scale"
    )

    val scanScale by animateFloatAsState(
        targetValue = if (isScanPressed) 0.9f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            stiffness = 450f,
            dampingRatio = 0.75f
        ),
        label = "scan_scale"
    )


    LaunchedEffect(Unit) {
        trackEvent(context, scope, "app_open")
    }

    val isDark = when (themeSetting) {
        "dark" -> true
        "light" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val textColorPrimary = if (isDark) Color.White else Color(0xFF1C1B1F)
    val textColorSecondary = if (isDark) Color.Gray else Color(0xFF5E5E62)
    val settingsIconColor =
        if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
    val dropdownBgColor = if (isDark) Color(0xFF16161A) else Color(0xFFF3F3F7)
    val dropdownTextColor = if (isDark) Color.White else Color.Black

    val switchView: (String) -> Unit = { target ->
        scope.launch {
            if (target == "main") trackEvent(context, scope, "view_scan_screen")
            currentView = target
        }
    }
    val runScan: () -> Unit = {
        val url = urlInput.trim()
        val hasSpaces = url.contains(" ")
        val isInvalidProtocol = (url.startsWith("https:/") && !url.startsWith("https://")) ||
                (url.startsWith("http:/") && !url.startsWith("http://"))

        if (url.isEmpty() || hasSpaces || isInvalidProtocol) {
            resText = strings["status_error"] ?: "ERROR"
            resTextColor = Color(0xFFFF1744)
            safeText = strings["status_invalid"] ?: "INVALID INPUT"
            safeTextColor = Color(0xFFFF1744)
            isLoading = false
        } else {
            trackEvent(context, scope, "run_scan_action")
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
                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putStringSet("local_search_history", updatedHistory).apply()
            }

            scope.launch(Dispatchers.IO) {
                val fullUrl = if (url.startsWith("http")) url else "https://$url"
                try {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .followRedirects(followRedirectsSetting)
                        .build()

                    val requestBuilder = Request.Builder().url(fullUrl)
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
                        resText = "HTTP $code"
                        resTextColor = if (code < 400) Color(0xFF00E676) else Color(0xFFFFB74D)
                        val isHttps = response.request.url.isHttps
                        safeText =
                            if (isHttps) strings["status_ssl"] ?: "" else strings["status_http"]
                                ?: ""
                        safeTextColor = if (isHttps) Color(0xFF00E676) else Color(0xFFFF1744)
                        lastValidUrl = fullUrl
                        responseBodyText = response.body?.string() ?: ""
                        responseHeadersText =
                            response.headers.joinToString("\n") { "${it.first}: ${it.second}" }
                        val cookies = response.headers("Set-Cookie")
                        responseCookiesText =
                            if (cookies.isNotEmpty()) cookies.joinToString("\n") else strings["cookies_empty"]
                                ?: ""
                    }
                } catch (e: IllegalArgumentException) {
                    resText = strings["status_error"] ?: "ERROR"
                    resTextColor = Color(0xFFFF1744)
                    safeText = strings["status_invalid"] ?: "INVALID INPUT"
                    safeTextColor = Color(0xFFFF1744)
                } catch (e: IOException) {
                    resText = strings["status_error"] ?: "ERROR"
                    resTextColor = Color(0xFFFF1744)
                    safeText = strings["status_no_server"] ?: "SERVER UNREACHABLE"
                    safeTextColor = Color.Gray
                } finally {
                    isLoading = false
                }
            }
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
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
                            tint = settingsIconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(dropdownBgColor),
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    strings["info"] ?: "Information",
                                    color = dropdownTextColor
                                )
                            },
                            onClick = { isMenuExpanded = false; isBottomSheetOpen = true }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    strings["dev_site"] ?: "Developer Website",
                                    color = dropdownTextColor
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                trackEvent(context, scope, "click_dev_site")
                                val intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://gs-ht.ru"))
                                context.startActivity(intent)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    strings["settings"] ?: "Settings",
                                    color = dropdownTextColor
                                )
                            },
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
                            .background(if (isDark) Color.White else Color(0xFF1C1B1F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "GS",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.Black else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "GS HTTP",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = textColorPrimary
                    )
                    Text(
                        text = "ENGINE BY G. SMERDOV",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColorSecondary,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    AnimatedButton(
                        text = strings["btn_start"] ?: "GET STARTED",
                        textColor = Color.White,
                        bgColor = Color(0xFF2979FF),
                        scale = welcomeScale,
                        onPressDown = { isWelcomePressed = true },
                        onPressUp = { isWelcomePressed = false }
                    ) { switchView("main") }


                    Spacer(modifier = Modifier.height(32.dp))
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
                            tint = settingsIconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { isSettingsSheetOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = settingsIconColor,
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
                        contentColor = Color(0xFF2979FF),
                        indicator = { tabPositions ->
                            if (currentMethodIndex < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    modifier = with(TabRowDefaults) {
                                        Modifier.tabIndicatorOffset(tabPositions[currentMethodIndex])
                                    },
                                    color = Color(0xFF2979FF),
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
                                        color = if (isSelected) textColorPrimary else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(35.dp))


                    TextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        placeholder = { Text(strings["placeholder_url"] ?: "Check URL", color = Color.Gray.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF2979FF),
                            unfocusedIndicatorColor = Color.LightGray
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
                        .padding(bottom = 32.dp),
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color.Black),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.width(320.dp).height(56.dp)
                        ) {
                            Text(strings["btn_open_browser"] ?: "OPEN IN BROWSER", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { isResponseInspectorSheetOpen = true },
                            modifier = Modifier.width(320.dp).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF1E1E24) else Color(0xFF141416), contentColor = Color(0xFFE2E2E6)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(strings["btn_search_data"] ?: "SEARCH RESPONSE DATA", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (isLoading) {
                        Box(modifier = Modifier.height(56.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF2979FF), strokeWidth = 4.dp, modifier = Modifier.size(36.dp))
                        }
                    } else {

                        AnimatedButton(
                            text = strings["btn_scan"] ?: "RUN SCAN",
                            textColor = Color.White,
                            bgColor = Color(0xFF2979FF),
                            scale = scanScale,
                            onPressDown = { isScanPressed = true },
                            onPressUp = { isScanPressed = false }
                        ) { runScan() }
                    }
                    TextButton(onClick = { switchView("welcome") }) {
                        Text(strings["btn_back"] ?: "← BACK", color = Color(0xFF2979FF), fontSize = 14.sp)
                    }
                }
            }
        }





        if (isBottomSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isBottomSheetOpen = false },
                sheetState = infoSheetState,
                containerColor = dropdownBgColor,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(textColorPrimary.copy(alpha = 0.1f))
                    )
                    Spacer(modifier = Modifier.height(15.dp))


                    Text(
                        text = strings["info"] ?: "INFORMATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = dropdownTextColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))


                    Text(
                        text = strings["developer"] ?: "Developer: Georgy Smerdov",
                        fontSize = 14.sp,
                        color = dropdownTextColor.copy(alpha = 0.7f)
                    )


                    Text(
                        text = "${strings["version"] ?: "Version"}: $VERSION",
                        fontSize = 12.sp,
                        color = dropdownTextColor
                    )


                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings["downloaded_from"] ?: "Downloaded from: RuStore",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = dropdownTextColor.copy(alpha = 0.6f)
                    )


                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = if (isDark) Color(0xFF333338) else Color(0xFFD0D0D5)
                    )
                    Spacer(modifier = Modifier.height(12.dp))


                    TextButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gs-ht.ru"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    ) {
                        Text(
                            text = strings["privacy_policy"] ?: "Privacy Policy",
                            color = Color(0xFF2979FF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }


                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = if (isDark) Color(0xFF333338) else Color(0xFFD0D0D5)
                    )
                    Spacer(modifier = Modifier.height(12.dp))


                    TextButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(

                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_github),
                                contentDescription = "GitHub",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings["source_code"] ?: "Source Code",
                                color = Color(0xFF2979FF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }





        if (isSettingsSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isSettingsSheetOpen = false },
                sheetState = settingsSheetState,
                containerColor = dropdownBgColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(width = 36.dp, height = 4.dp).clip(RoundedCornerShape(10.dp)).background(textColorPrimary.copy(alpha = 0.15f)))
                    Spacer(modifier = Modifier.height(24.dp))


                    Text(
                        text = strings["settings"] ?: "SETTINGS",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {

                            Text(text = strings["auto_redirect"] ?: "Auto-redirect", color = dropdownTextColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = strings["auto_redirect_sub"] ?: "Follow website redirects", color = textColorSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = followRedirectsSetting,
                            onCheckedChange = { followRedirectsSetting = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2979FF),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = if(isDark) Color(0xFF1A1A1A) else Color(0xFFE0E0E4)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        onClick = { isThemeDialogOpen = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if(isDark) Color(0xFF2A2A30) else Color(0xFFE0E0E6), contentColor = dropdownTextColor)
                    ) {
                        val themeText = when(themeSetting) {
                            "dark" -> strings["theme_dark"] ?: "Dark"
                            "light" -> strings["theme_light"] ?: "Light"
                            else -> strings["theme_system"] ?: "System default"
                        }
                        Text(text = "${strings["theme_btn"] ?: "App Theme"}: $themeText", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        onClick = { isLanguageDialogOpen = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if(isDark) Color(0xFF2A2A30) else Color(0xFFE0E0E6), contentColor = dropdownTextColor)
                    ) {
                        val langText = if (currentLanguage == "ru") "Русский" else "English"
                        Text(text = "${strings["lang_btn"] ?: "Language"}: $langText", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        onClick = {
                            urlInput = ""
                            resText = ""
                            safeText = ""
                            searchHistory = emptySet()
                            val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            sharedPref.edit().remove("local_search_history").apply()
                            isSettingsSheetOpen = false
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if(isDark) Color(0xFF222226) else Color(0xFFFFEBEE), contentColor = Color(0xFFFF1744))
                    ) {
                        Text(text = strings["clear_history"] ?: "Clear input history", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isHistorySheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isHistorySheetOpen = false },
                sheetState = historySheetState,
                containerColor = dropdownBgColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(width = 40.dp, height = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(textColorPrimary.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = strings["history_title"] ?: "REQUEST HISTORY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    if (searchHistory.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            // Локализованный текст пустой истории
                            Text(
                                text = strings["history_empty"] ?: "History is empty",
                                fontSize = 14.sp
                            )
                        }


                } else {

                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(searchHistory.toList()) { _, historyUrl ->
                                Button(
                                    onClick = {
                                        urlInput = historyUrl
                                        isHistorySheetOpen = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDark) Color(0xFF222226) else Color(
                                            0xFFEFEFF4
                                        ),
                                        contentColor = dropdownTextColor
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🕒", modifier = Modifier.padding(end = 12.dp))
                                        Text(
                                            text = historyUrl,
                                            maxLines = 1,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
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
                containerColor = dropdownBgColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(width = 40.dp, height = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(textColorPrimary.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    Text(
                        strings["inspector_title"] ?: "SERVER RESPONSE DATA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = dropdownTextColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        onClick = {
                            try {
                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(lastValidUrl))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E676),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            strings["btn_open_browser_emoji"] ?: "OPEN SITE IN BROWSER 🌐",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDark) Color(0xFF1E1E24) else Color(0xFFE8E8ED),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("BODY", "HEADERS", "COOKIES").forEach { tab ->
                            val isSelected = activeSearchTab == tab
                            TextButton(
                                onClick = { activeSearchTab = tab },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) (if (isDark) Color(0xFF3A3A45) else Color.White) else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Text(
                                    tab,
                                    color = if (isSelected) textColorPrimary else textColorSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))


                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = {
                            Text(
                                strings["search_log_placeholder"] ?: "Search text inside log..."
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val currentTextData = when (activeSearchTab) {
                        "BODY" -> responseBodyText
                        "HEADERS" -> responseHeadersText
                        else -> responseCookiesText
                    }


                    val filteredText = if (searchQuery.isEmpty()) {
                        currentTextData
                    } else {
                        try {
                            if (currentTextData.length > 500_000) {
                                strings["search_too_big"] ?: "Error: Log is too large"
                            } else {
                                currentTextData.lines()
                                    .filter { it.contains(searchQuery, ignoreCase = true) }
                                    .joinToString("\n")
                            }
                        } catch (e: Exception) {
                            strings["search_error"] ?: "error"
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(
                                if (isDark) Color(0xFF16161A) else Color(0xFFF0F0F4),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF333338) else Color(0xFFD0D0D5),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = if (filteredText.trim()
                                            .isEmpty() && searchQuery.isNotEmpty()
                                    ) (strings["not_found"] ?: "Nothing found") else filteredText,
                                    color = textColorPrimary,
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
                title = { Text("Выберите тему", color = dropdownTextColor) },
                text = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = themeSetting == "system",
                                onClick = { onThemeChange("system"); isThemeDialogOpen = false })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Как в системе", color = dropdownTextColor)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = themeSetting == "light",
                                onClick = { onThemeChange("light"); isThemeDialogOpen = false })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Светлая", color = dropdownTextColor)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = themeSetting == "dark",
                                onClick = { onThemeChange("dark"); isThemeDialogOpen = false })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Тёмная", color = dropdownTextColor)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isThemeDialogOpen = false }) {
                        Text(
                            "Отмена",
                            color = Color(0xFF2979FF)
                        )
                    }
                },
                containerColor = dropdownBgColor,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }



if (isLanguageDialogOpen) {
    AlertDialog(
        onDismissRequest = { isLanguageDialogOpen = false },
        title = { Text(strings["lang_title"] ?: "", color = dropdownTextColor) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    RadioButton(
                        selected = currentLanguage == "ru",
                        onClick = {
                            currentLanguage = "ru"
                            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putString("app_lang", "ru").apply()
                            isLanguageDialogOpen = false
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Русский", color = dropdownTextColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    RadioButton(
                        selected = currentLanguage == "en",
                        onClick = {
                            currentLanguage = "en"
                            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putString("app_lang", "en").apply()
                            isLanguageDialogOpen = false
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("English", color = dropdownTextColor)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { isLanguageDialogOpen = false }) { Text(strings["cancel"] ?: "", color = Color(0xFF2979FF)) }
        },
        containerColor = dropdownBgColor,
        shape = RoundedCornerShape(28.dp)
    )
}
}




@Composable
fun AnimatedButton(
    text: String,
    textColor: Color,
    bgColor: Color,
    scale: Float,
    onPressDown: () -> Unit,
    onPressUp: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(56.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    onPressDown()
                    tryAwaitRelease()
                    onPressUp()
                    onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
    }
}
