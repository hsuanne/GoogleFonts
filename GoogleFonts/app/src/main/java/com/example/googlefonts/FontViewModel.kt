package com.example.googlefonts

import android.graphics.Typeface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import okhttp3.*

const val KEY = "AIzaSyDibhZWtnHZqR5nSz54Bk9s10j9pTQgBnA"


class FontViewModel : ViewModel() {
    var fontList = MutableLiveData<List<String>>()
    val itemList = mutableListOf<String>()
    var currentTypeFace = MutableLiveData<Typeface>()

    init {
        if (itemList.isEmpty()) {
            fetchFontList()
        }
    }

    private fun fetchFontList() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModelScope.launch {
                val fetchList = fetchJson()
                fontList.value = fetchList
            }.join()
        }
    }

    suspend fun fetchJson(): MutableList<String> {
        println("Attempting to Fetch fonts")

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