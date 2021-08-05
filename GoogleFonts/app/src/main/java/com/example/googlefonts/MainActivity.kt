package com.example.googlefonts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var textPreview:TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FontAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fontViewModel = ViewModelProvider(this).get(FontViewModel::class.java)

        textPreview = findViewById(R.id.text_preview)
        recyclerView = findViewById(R.id.recyclerview)
        adapter = FontAdapter(fontViewModel)
        recyclerView.adapter = adapter
        fontViewModel.fontList.observe(this){
            adapter.submitList(it)
        }

        fontViewModel.currentTypeFace.observe(this){
            textPreview.typeface = it
        }
    }
}