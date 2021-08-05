package com.example.googlefonts

import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class FontAdapter(var viewModel: FontViewModel) :
    ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {
    lateinit private var mHandler: Handler

    inner class StrViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.str_container_textview)
        val cardView: CardView = view.findViewById(R.id.str_container_cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.string_container, parent, false)
        return StrViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StrViewHolder -> {
                val str = getItem(position)
                holder.apply {
                    textView.text = str
                    cardView.setOnClickListener {
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
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        FontsContractCompat
                            .requestFont(itemView.context, request, callback, mHandler)
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
