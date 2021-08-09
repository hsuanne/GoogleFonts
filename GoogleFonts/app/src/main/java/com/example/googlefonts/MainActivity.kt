package com.example.googlefonts

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var textPreview:TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FontAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fontViewModel = ViewModelProvider(this).get(FontViewModel::class.java)

        textPreview = findViewById(R.id.text_preview)
        recyclerView = findViewById(R.id.recyclerview)
        adapter = FontAdapter(fontViewModel, this)
        recyclerView.adapter = adapter

        if(!isOnline(this)){
            val mFamilyNameSet = mutableListOf<String>()
            mFamilyNameSet.addAll(listOf(*resources.getStringArray(R.array.family_names)))
            adapter.submitList(mFamilyNameSet)
        }

        fontViewModel.fontList.observe(this){
            adapter.submitList(it)
        }

        fontViewModel.currentTypeFace.observe(this){
            textPreview.typeface = it
        }
    }
}