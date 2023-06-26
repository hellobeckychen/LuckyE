package com.example.luckye

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.luckye.model.ResultMessage

class MainViewModel : ViewModel() {
    val username: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val questionData: MutableLiveData<ArrayList<ResultMessage>> = MutableLiveData()
    val 答案A: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val 答案B: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val 答案C: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val 答案D: MutableLiveData<String> by lazy { MutableLiveData<String>() }

}