package com.example.luckye

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageThreeViewModel : ViewModel() {
    val score: MutableLiveData<String> by lazy { MutableLiveData<String>() }
}