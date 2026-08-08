package com.flet.gshttp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.flet.gshttp.ui.theme.MyApplicationTheme
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

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
