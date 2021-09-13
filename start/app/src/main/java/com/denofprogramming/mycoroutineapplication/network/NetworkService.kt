package com.denofprogramming.mycoroutineapplication.network

import android.graphics.Bitmap

interface NetworkService {


    fun cancel()

    //TODO 0 - Note this is a Mock simulated Library, that supports Coroutines.
    suspend fun getImage(id: String): Bitmap?

}