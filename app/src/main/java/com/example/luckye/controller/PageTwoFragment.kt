package com.example.luckye.controller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.luckye.MainViewModel
import com.example.luckye.PageTwoViewModel
import com.example.luckye.R
import com.example.luckye.databinding.FragmentPageTwoBinding
import com.example.luckye.model.ResultMessage
import com.example.luckye.model.SendAnswer
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.*

class PageTwoFragment : Fragment() {
    private lateinit var binding: FragmentPageTwoBinding
    private lateinit var timer: Timer
    private var userName = ""
    private var questionNumber = ""
    private var newQuestionNumber = 0
    private var questionAnswer = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewModel: PageTwoViewModel by viewModels()
        binding = FragmentPageTwoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .asGif()
            .load(R.drawable.page2backgroundsmall) // 替換成你的 GIF 文件資源
            .into(object : SimpleTarget<GifDrawable>() {
                override fun onResourceReady(
                    resource: GifDrawable,
                    transition: Transition<in GifDrawable>?
                ) {
                    binding.imageViewBG2.setImageDrawable(resource)
                    resource.start() // 開始播放 GIF
                }
            })

        with(binding) {
            arguments?.let {
//                val questionData = it.getSerializable("questionData") as? ArrayList<ResultMessage>
                userName = it.getString("username").toString()
            }
            //用viewBinding的方式共享資料
            val sharedViewModel =
                ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
            val questionData = sharedViewModel.questionData.value
        }
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                timerAction()
            }
        }, 0, 5000)


    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            btLeftTop.setOnClickListener {
                questionAnswer = btLeftTop.text.toString()
            }
            btRightTop.setOnClickListener {
                questionAnswer = btRightTop.text.toString()
            }
            btLeftDown.setOnClickListener {
                questionAnswer = btLeftDown.text.toString()
            }
            btRightDown.setOnClickListener {
                questionAnswer = btRightDown.text.toString()
            }
            btConfirm.setOnClickListener {

                AlertDialog.Builder(requireContext())
                    .setMessage("確定答案為$questionAnswer 嗎?")
                    .setPositiveButton("確定"){_,_ ->
                        SendAnswer.postData("send_answer", userName, questionAnswer)
                        btConfirm.isEnabled = false
                    }
                    .setNegativeButton("取消"){_,_ ->

                    }
                    .show()

            }
        }
    }

    fun timerAction() {
        if (newQuestionNumber == 0) {
            //不做事
        }
        if (newQuestionNumber == 999) {   //跳到第三頁
            timer.cancel()
            timer.purge()
            activity?.runOnUiThread {
                arguments?.let {
                    it.getString("username").toString()
                }
            Navigation.findNavController(binding.imageViewBG2).navigate(R.id.pageThreeFragment,arguments)}
        } else {
            activity?.runOnUiThread {
                with(binding) {
                    getQuesitonAPI()
                    arguments?.let {
                        val questionData =
                            it.getSerializable("questionData") as? ArrayList<ResultMessage>
                        if (questionNumber != newQuestionNumber.toString()) {
                            if (questionData != null) {
                                for (data in questionData) {
                                    if (newQuestionNumber.toString() == data.題號) {
                                        btLeftTop.text = data.答案A
                                        btRightTop.text = data.答案B
                                        btLeftDown.text = data.答案C
                                        btRightDown.text = data.答案D
                                        questionNumber = newQuestionNumber.toString()
                                        btConfirm.isEnabled = true
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getQuesitonAPI() {

        val okHttpClient = OkHttpClient().newBuilder().build()

        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "get_question")
        jsonBody.put("user", userName)

        val requestBody: RequestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())

        val request1 = Request.Builder()
            .url("https://script.google.com/macros/s/AKfycbwANgTCnjKdPCLmDBtXutkNFkqeUb1K6Kz3868KFt5zoMCnIRLu7GLNVYQr1MuvVuPP/exec")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request1).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }

            //response得到的是json物件
            //需再decode成kotlin物件
            override fun onResponse(call: Call, response: Response) {
                println("星期五" + (response))

                val gson = Gson()
                val json = response.body!!.string()
                val jsonObject = gson.fromJson(json, JsonObject::class.java)
                val code = jsonObject.get("code").asString
                val data2 = jsonObject.get("result_message").asString
                println("code:$code, data2: $data2")
                if(data2 == "same") return
                this@PageTwoFragment.newQuestionNumber = data2.toInt()
            }
        })
    }
}

