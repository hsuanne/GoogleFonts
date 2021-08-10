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
import com.example.googlefonts.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var textPreview:TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FontAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fontViewModel = ViewModelProvider(this).get(FontViewModel::class.java)

        textPreview = binding.textPreview
        recyclerView = binding.recyclerview
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
            println("currentTypeFace")
        }
    }
}