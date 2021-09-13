package com.denofprogramming.mycoroutineapplication.ui.main


import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.denofprogramming.mycoroutineapplication.network.MockNetworkService
import com.denofprogramming.mycoroutineapplication.repository.image.CoroutineImageRepository
import com.denofprogramming.mycoroutineapplication.repository.time.DefaultClock
import com.denofprogramming.mycoroutineapplication.shared.Resource
import com.denofprogramming.mycoroutineapplication.shared.uilt.logMessage
import kotlinx.coroutines.*

/**
 *
 * In this Project, I want to cover the following Topics;
 *  1) Coroutine Scope
 *  2) Coroutine Job
 *  3) launch
 *  4) suspend
 *  5) Main-Safe
 *  6) withContext
 *
 *  Let's see how these Kotlin Coroutine topics fit into an Android app.
 *
 *  At the end of completing the TO DO's the application will compile and run, give it a try!
 *
 */
class MainViewModel : ViewModel() {


    private val _clock = DefaultClock.build()

    private val _imageRepository =
        CoroutineImageRepository.build(MockNetworkService.build())

    val image: LiveData<Resource<Bitmap>> get() = _image

    private val _image = MutableLiveData<Resource<Bitmap>>()

    val currentTimeTransformed = _clock.time.switchMap {
        val timeFormatted = MutableLiveData<String>()
        val time = _clock.timeStampToTime(it)
        logMessage("currentTimeTransformed switchMap time is $time")
        timeFormatted.value = time
        timeFormatted
    }


    //TODO 2 - Create a Coroutine Job


    //TODO 1 - Create a Coroutine Scope



    init {
        startClock()
    }

    fun onButtonClicked() {
        logMessage("Start onButtonClicked()")
        //TODO 3 - We need to launch a Coroutine
        loadImage()

    }

    fun onCancelClicked() {
        _imageRepository.cancel()
    }

    //TODO 4 - Lets modify this method so its supports Coroutines
    private fun loadImage() {
        logMessage("Start loadImage()")
        val imageResource = try {
            _imageRepository.fetchImage(_imageRepository.nextImageId())
        } catch (e: Exception) {
            logMessage("loadImage() exception $e")
            Resource.error(e.localizedMessage ?: "No Message")
        }
        showImage(imageResource)
    }

    //TODO 5 - Lets modify this method to support Coroutines and make it Main-safe!
    private fun showImage(imageResource: Resource<Bitmap>) {
        logMessage("showingImage...")
        _image.value = imageResource
    }

    private fun startClock() {
        logMessage("Start startClock()")
        _clock.start()
    }


    //TODO 6 - We need to think about cancelling all Coroutines when the ViewModel is dismantled.
    override fun onCleared() {
        super.onCleared()
    }


}