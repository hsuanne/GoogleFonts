package com.example.googlefonts

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
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
    var fontList = MutableLiveData<List<String>?>()
    val itemList = mutableListOf<String>()
    var currentTypeFace = MutableLiveData<Typeface>()
    var isLoading = MutableLiveData<Boolean>()
    val fontRepository = FontRepository_Impl()

    init {
        if (itemList.isEmpty()) {
            fetchFontList()
        }
    }

    private fun fetchFontList() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModelScope.launch {
                fontList.value = fontRepository.fetchFont()
            }.join()
        }
    }

    fun getFont(context:Context, str:String){
        isLoading.value = true
        val handlerThread = HandlerThread("fonts")
        handlerThread.start()
        val mHandler = Handler(handlerThread.looper)

        val request = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            str,
            R.array.com_google_android_gms_fonts_certs
        )

        val callback = object : FontsContractCompat.FontRequestCallback() {
            override fun onTypefaceRetrieved(typeface: Typeface) {
                currentTypeFace.value = typeface
                isLoading.value = false
            }

            override fun onTypefaceRequestFailed(reason: Int) {
                Log.i("onTypefaceRequestFailed", "Failed reason: $reason")
//                                    Toast.makeText(
//                                        itemView.context,
//                                        "Failed reason: $reason",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
            }
        }
        FontsContractCompat
            .requestFont(context, request, callback, mHandler)
    }
}