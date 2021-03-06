package com.example.googlefonts

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
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
                            viewModel.getFont(itemView.context,str)
                        } else {
                            val lowerStr = str.replace(" ", "_").toLowerCase(Locale.ROOT)
                            viewModel.currentTypeFace.value =
                                try {
                                    Typeface.createFromAsset(activity.assets, "$lowerStr.ttf")
                                } catch (e: Exception) {
                                    Log.e("No Internet Connection", "error: $e")
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


fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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
                } else {
                    val info = connectivityManager.activeNetworkInfo
                    if (info!=null && info.isConnected) {
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.isConnected){
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                            return true
                        } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.isConnected){
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                            return true
                        } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)!!.isConnected){
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                            return true
                        }
                    }
                }
    }
    return false
}
