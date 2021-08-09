package com.example.googlefonts

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class FontAdapter(var viewModel: FontViewModel, var activity: MainActivity) :
    ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {
    private lateinit var mHandler: Handler
    private lateinit var tf: Typeface

    inner class StrViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.str_container_textview)
        val cardView: CardView = view.findViewById(R.id.str_container_cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.string_container, parent, false)
        return StrViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StrViewHolder -> {
                val str = getItem(position)


                holder.apply {
                    textView.text = str

                    cardView.setOnClickListener {
                        if (isOnline(activity)) {
                            val handlerThread = HandlerThread("fonts")
                            handlerThread.start()
                            mHandler = Handler(handlerThread.looper)

                            val request = FontRequest(
                                "com.google.android.gms.fonts",
                                "com.google.android.gms",
                                str,
                                R.array.com_google_android_gms_fonts_certs
                            )

                            val callback = object : FontsContractCompat.FontRequestCallback() {
                                override fun onTypefaceRetrieved(typeface: Typeface) {
                                    viewModel.currentTypeFace.value = typeface
                                }

                                override fun onTypefaceRequestFailed(reason: Int) {
                                    Toast.makeText(
                                        itemView.context,
                                        "Failed reason: $reason",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            FontsContractCompat
                                .requestFont(itemView.context, request, callback, mHandler)
                        } else {
                            println(str)
                            val lowerStr = str.replace(" ", "_").toLowerCase(Locale.ROOT)
                            viewModel.currentTypeFace.value =
                                try {
                                    Typeface.createFromAsset(activity.assets, "$lowerStr.ttf")
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        itemView.context,
                                        "error: $e",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Typeface.createFromAsset(activity.assets, "abeezee.ttf")
                                }
                        }
                    }
                }
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}
