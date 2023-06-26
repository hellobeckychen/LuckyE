package com.example.luckye.controller

import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.luckye.PageThreeViewModel
import com.example.luckye.R
import com.example.luckye.databinding.FragmentPageThreeBinding
import com.example.luckye.model.API
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class PageThreeFragment : Fragment() {
    private lateinit var binding: FragmentPageThreeBinding
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewModel: PageThreeViewModel by viewModels()
        binding = FragmentPageThreeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //此段code,for GIF背景動畫
        Glide.with(this)
            .asGif()
            .load(R.drawable.page3background) // 替換成你的 GIF 文件資源
            .into(object : SimpleTarget<GifDrawable>() {
                override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                    binding.imageViewBG3.setImageDrawable(resource)
                    resource.start() // 開始播放 GIF
                }
            })

        with(binding) {
            // arguments即為前頁傳來的bundle，如果不為null即可取值
            arguments?.let {
               username = it.getString("username").toString()

                getScore()
            }
        }
    }

    fun getScore( ) {

            val urlString = API().appDomain
            val okHttpClient = OkHttpClient().newBuilder().build()


            //要先encode變成json格式
            val jsonBody = JSONObject()
            jsonBody.put("actions", "get_score")
            jsonBody.put("user", username)

            val requestBody: RequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())

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
                    println("分數分數"+ (response))


                    val gson = Gson()
                    val json = response.body!!.string()

                    // 解析外層JSON字串
                    val outerJsonObject = gson.fromJson(json, JsonObject::class.java)
                    val code = outerJsonObject.get("code")?.asString

                    // 解析内层JSON字串
                    val message = outerJsonObject?.get("result_message")
                    val innerJsonObject = message?.let { gson.fromJson(it, JsonObject::class.java) }
                    val totalCount = innerJsonObject?.get("total_count")?.asString
                    val rightCount = innerJsonObject?.get("correct_count")?.asString
                    val score = innerJsonObject?.get("score")?.asString
                    binding.viewModel?.score?.postValue(score.toString())
                    println("code: $code")
                    println("total_count: $totalCount")
                    println("right_count: $rightCount")
                    println("score: $score")

                }
            })
        }
}