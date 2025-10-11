package com.hearsilent.quickpay.libs.helper

import com.hearsilent.quickpay.callback.VersionCallback
import com.hearsilent.quickpay.extensions.indices
import com.hearsilent.quickpay.models.VersionModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit


object ApiHelper {

    private var mClient: OkHttpClient? = null

    private fun init(): OkHttpClient {
        val client = OkHttpClient().newBuilder().followRedirects(true)
            .followSslRedirects(true).connectTimeout(7, TimeUnit.SECONDS)
            .writeTimeout(7, TimeUnit.SECONDS).readTimeout(7, TimeUnit.SECONDS).build()
        return client.newBuilder().build()
    }

    private val client: OkHttpClient
        get() {
            if (mClient == null) mClient = init()
            return mClient!!
        }

    const val DEFAULT_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 15; Pixel 8 Build/AP3A.241105.007; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/130.0.6723.107 Mobile Safari/537.36"

    private val userAgentString: String
        get() {
            val ua = System.getProperty("http.agent")
            return ua ?: DEFAULT_USER_AGENT
        }

    fun checkVersion(currentVersion: String?, callback: VersionCallback) {
        val request = Request.Builder().header("User-Agent", userAgentString)
            .url("https://api.github.com/repos/hearsilent/QuickPay/releases").get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.code == 200) {
                        val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                        val body = responseBodyCopy.string()
                        val jsonArray = JSONArray(body)

                        val latestJsonObject = jsonArray.getJSONObject(0)
                        val latestVersionModel = VersionModel(
                            latestJsonObject.getString("tag_name"),
                            latestJsonObject.getJSONArray("assets").getJSONObject(0)
                                .getString("browser_download_url"),
                            latestJsonObject.getString("body")
                        )

                        var currentVersionModel: VersionModel? = null
                        for (i in jsonArray.indices) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val version = jsonObject.getString("tag_name")
                            if (version == "v$currentVersion") {
                                currentVersionModel = VersionModel(
                                    version,
                                    jsonObject.getJSONArray("assets").getJSONObject(0)
                                        .getString("browser_download_url"),
                                    jsonObject.getString("body")
                                )
                                break
                            }
                        }

                        callback.onSuccess(currentVersionModel, latestVersionModel)
                    } else {
                        callback.onFail()
                    }
                } catch (_: Exception) {
                    callback.onFail()
                }
            }
        })
    }

}