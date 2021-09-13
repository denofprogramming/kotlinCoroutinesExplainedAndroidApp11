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
 * In this Demo, I want to over the following Topics;
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
        logMessage("currentTimeTransformed time is $time")
        timeFormatted.value = time
        timeFormatted
    }

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var _viewModelJob = Job()


    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [_viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val _uiScope = CoroutineScope(Dispatchers.Main + _viewModelJob)


    init {
        startClock()
    }

    fun onButtonClicked() {
        logMessage("Start onButtonClicked()")
        _uiScope.launch {
            loadImage()
        }
    }

    fun onCancelClicked() {
        _imageRepository.cancel()
    }

    private suspend fun loadImage() {
        logMessage("Start loadImage()")
        val imageResource = try {
            _imageRepository.fetchImage(_imageRepository.nextImageId())
        } catch (e: Exception) {
            logMessage("loadImage() exception $e")
            Resource.error(e.localizedMessage ?: "No Message")
        }
        showImage(imageResource)
    }

    private suspend fun showImage(imageResource: Resource<Bitmap>) {
        logMessage("showingImage...")
        withContext(Dispatchers.Main) {
            _image.value = imageResource
        }

    }

    private fun startClock() {
        logMessage("Start startClock()")
        _clock.start()
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        _uiScope.cancel()
       /*
        // or we could use...
       _viewModelJob.cancel()
       */
    }


}