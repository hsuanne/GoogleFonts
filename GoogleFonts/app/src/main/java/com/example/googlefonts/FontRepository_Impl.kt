package com.example.googlefonts

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException

class FontRepository_Impl:FontRepository {
    override suspend fun fetchFont(): MutableList<String> {
        println("Attempting to Fetch fonts")

        val itemList = mutableListOf<String>()

        val url = "https://www.googleapis.com/webfonts/v1/webfonts?key=$KEY"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val call = client.newCall(request)
        //requestCall: 非同步
        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response.body?.string()

                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<FontInfo>(body, object : TypeToken<FontInfo>() {}.type)
                        println("result_kind: " + result.kind)
                        println("result_items: " + result.items.size)
                        for (item in result.items) {
                            itemList.add(item.family)
                        }
                        it.resumeWith(Result.success(itemList))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }
}