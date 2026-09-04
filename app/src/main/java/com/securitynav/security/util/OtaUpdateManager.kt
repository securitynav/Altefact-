package com.securitynav.security.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.securitynav.security.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class OtaUpdateManager(private val context: Context) {

    private val serverUrl = "${BuildConfig.RENDER_SERVER_URL}update/check"

    data class UpdateInfo(
        val isUpdateAvailable: Boolean,
        val versionCode: Int,
        val versionName: String,
        val downloadUrl: String,
        val changelog: String
    )

    // 1. Consultar actualización al servidor Render
    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val url = URL("$serverUrl?currentVersion=${context.packageManager.getPackageInfo(context.packageName, 0).versionCode}")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }

            if (connection.responseCode == 200) {
                val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonResponse)
                
                val latestVersion = json.getInt("latestVersionCode")
                val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionCode

                UpdateInfo(
                    isUpdateAvailable = latestVersion > currentVersion,
                    versionCode = latestVersion,
                    versionName = json.getString("latestVersionName"),
                    downloadUrl = json.getString("apkUrl"),
                    changelog = json.getString("changelog")
                )
            } else {
                UpdateInfo(false, 0, "", "", "")
            }
        } catch (e: Exception) {
            UpdateInfo(false, 0, "", "", "")
        }
    }

    // 2. Descargar e Instalar APK desde el servidor Render
    suspend fun downloadAndInstallApk(downloadUrl: String, onProgress: (Int) -> Unit) = withContext(Dispatchers.IO) {
        val apkFile = File(context.getExternalFilesDir(null), "securitynav_update.apk")
        val url = URL(downloadUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        val fileLength = connection.contentLength
        connection.inputStream.use { input ->
            apkFile.outputStream().use { output ->
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    if (fileLength > 0) {
                        onProgress((total * 100 / fileLength).toInt())
                    }
                    output.write(data, 0, count)
                }
            }
        }

        // Trigger de Instalación del Sistema
        installApk(apkFile)
    }

    private fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else {
                Uri.fromFile(file)
            }
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
