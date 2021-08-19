package com.example.googlefonts

interface FontRepository {
    suspend fun fetchFont(): MutableList<String>
}