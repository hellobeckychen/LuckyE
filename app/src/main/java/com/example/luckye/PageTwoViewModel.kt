package com.example.luckye

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageTwoViewModel : ViewModel() {
    val username: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val 答案A: MutableLiveData<String> by lazy { MutableLiveData<String>("選項") }
    val 答案B: MutableLiveData<String> by lazy { MutableLiveData<String>("選項") }
    val 答案C: MutableLiveData<String> by lazy { MutableLiveData<String>("選項") }
    val 答案D: MutableLiveData<String> by lazy { MutableLiveData<String>("選項") }

}