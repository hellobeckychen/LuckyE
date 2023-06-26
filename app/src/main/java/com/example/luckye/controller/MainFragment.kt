package com.example.luckye.controller

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.provider.Settings.Secure
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.luckye.MainViewModel
import com.example.luckye.R
import com.example.luckye.databinding.FragmentMainBinding
import com.example.luckye.model.API
import com.example.luckye.model.ResultMessage
import com.example.luckye.model.SendAnswer
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    var loadQuestionData = mutableListOf<ResultMessage>()
    var data3: String = ""
    val viewModel: MainViewModel by viewModels()
    private var id: String = ""

    //取得hardwareId
    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //上線用:
        id = Secure.getString(requireContext().contentResolver, Secure.ANDROID_ID)

        //測試用:
//      var id1 = Secure.getString(requireContext().contentResolver, Secure.ANDROID_ID)
//      id = id1+3

        runLoadApi()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //此段code,for GIF背景動畫
        Glide.with(this)
            .asGif()
            .load(R.drawable.luckye1) // 替換成你的 GIF 文件資源
            .into(object : SimpleTarget<GifDrawable>() {
                override fun onResourceReady(
                    resource: GifDrawable,
                    transition: Transition<in GifDrawable>?
                ) {
                    binding.imageViewBG1.setImageDrawable(resource)
                    resource.start() // 開始播放 GIF
                }
            })
        with(binding) {

            button.setOnClickListener {
                val textUsername = editTextTextPersonName.text.toString()
                if (textUsername.isNotBlank()) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("輸入名字為$textUsername")
                        .setPositiveButton("確定"){_,_ ->
                            addUserMachine(id, textUsername)

                            val bundle =
                                Bundle()
                            val uid = viewModel!!.username.value
                            bundle.putString("uid", id)
                            bundle.putSerializable("questionData", ArrayList(loadQuestionData))

                            val sharedViewModel =
                                ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
                            sharedViewModel.questionData.value = ArrayList(loadQuestionData)

                            Navigation.findNavController(binding.imageViewBG1).navigate(   //it指的是button
                                R.id.action_mainFragment_to_page2Fragment,
                                bundle  //因為要帶資料,到下一頁
                            )
                        }
                        .setNegativeButton("取消"){_,_ ->

                        }
                        .show()
                } else {
                    Toast.makeText(context, "請輸入姓名", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
        }
    }

    fun runLoadApi() {
        var isbool = false
        val urlString = API().appDomain
        val okHttpClient = OkHttpClient().newBuilder().build()

        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "load_questions")
        val requestBody: RequestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())
        val request1 = Request.Builder()
            .url("https://script.google.com/macros/s/AKfycbwANgTCnjKdPCLmDBtXutkNFkqeUb1K6Kz3868KFt5zoMCnIRLu7GLNVYQr1MuvVuPP/exec")
            .post(requestBody)
            .build()
        okHttpClient.newCall(request1).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error1")
            }

            //response得到的是json物件
            //需再decode成kotlin物件
            override fun onResponse(call: Call, response: Response) {
                println("收到" + (response))
                val sb = StringBuilder()
                val gson = Gson()
                val json = response.body!!.string()
                sb.append("Object to JSON:\n $json\n")
                val jsonObject = gson.fromJson(json, JsonObject::class.java)

                //用JsonArray去decode把jsonObject變成kotlin物件
                val jsonArray = jsonObject.get("result_message").asJsonArray
                if (jsonObject.get("code").asString == "200") {
                    for (element in jsonArray) {
                        val jsonObject = element.asJsonObject
                        val questionNum = jsonObject.get("題號").asString
                        val topic = jsonObject.get("題目").asString
//                        println("題號: $questionNum, 題目: $topic")

                        //把for迴圈拿出來的資料,存進val getdata裡面
                        //在宣告一個mutableList data1 (L15) 去接getdata的資料
                        val getdata = ResultMessage(
                            題號 = questionNum,
                            題目 = jsonObject.get("題號").asString,
                            答案A = jsonObject.get("答案A").asString,
                            答案B = jsonObject.get("答案B").asString,
                            答案C = jsonObject.get("答案C").asString,
                            答案D = jsonObject.get("答案D").asString
                        )

                        loadQuestionData.add(getdata)
                    }


                    GetUserByMachine(id)
                    isbool = true
                } else {
                    isbool = false
                }
                println("這邊,$loadQuestionData")
            }
        })
    }

    fun addUserMachine(machine_id: String, user: String) {

        val okHttpClient = OkHttpClient().newBuilder().build()

        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "add_user_machine")
        jsonBody.put("machine_id", machine_id)
        jsonBody.put("user", user)

        val requestBody: RequestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())

        val request1 = Request.Builder()
            .url("https://script.google.com/macros/s/AKfycbwANgTCnjKdPCLmDBtXutkNFkqeUb1K6Kz3868KFt5zoMCnIRLu7GLNVYQr1MuvVuPP/exec")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request1).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error2")
            }

            //response得到的是json物件
            //需再decode成kotlin物件
            override fun onResponse(call: Call, response: Response) {
                println("星期六$response")

                val gson = Gson()
                val json = response.body!!.string()

                //把從後端拿回來的資料從json格式轉回kotlin
                val jsonObject = gson.fromJson(json, JsonObject::class.java)

                //把轉回kotlin的物件取出來
                val code = jsonObject.get("code").asInt


                if (code == 200) {
                    data3 = jsonObject.get("result_message").asString
//                    println("code: $code, data3: $data3")

                } else {
                    return
                }
            }
        })
    }

    fun GetUserByMachine(machine_id: String) {
        var data4: String = ""
        val okHttpClient = OkHttpClient().newBuilder().build()

        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "get_user_by_machine")
        jsonBody.put("machine_id", machine_id)
        println("MAS$machine_id")

        val requestBody: RequestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())

        val request1 = Request.Builder()
            .url("https://script.google.com/macros/s/AKfycbwANgTCnjKdPCLmDBtXutkNFkqeUb1K6Kz3868KFt5zoMCnIRLu7GLNVYQr1MuvVuPP/exec")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request1).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error3")
            }

            //response得到的是json物件
            //需再decode成kotlin物件
            override fun onResponse(call: Call, response: Response) {
                println("星期七" + (response))

                val gson = Gson()
                val json = response.body!!.string()

                //把從後端拿回來的資料從json格式轉回kotlin
                val jsonObject = gson.fromJson(json, JsonObject::class.java)

                //把轉回kotlin的物件取出來
                val code = jsonObject.get("code").asInt
                data4 = jsonObject.get("result_message").asString
                println("code: $code, data4: $data4")

                if (code == 200) {
                    val bundle =
                        Bundle()
                    val uid =
                        viewModel!!.username.value  //敢用!!是因為前面已經檢查過不會為空值了,所以這邊要把"值"取出來,放進val uid這個data
                    // 可將基本類型或字串放進Bundle
                    bundle.putString("uid", uid)  //device id
                    bundle.putString("username", data4) //username
                    // 可將Serializable物件放進Bundle
                    bundle.putSerializable(
                        "questionData",
                        ArrayList(loadQuestionData)
                    )  //要讓user"物件"可以被續列化,這邊才可以存.putSerializable
                    activity?.runOnUiThread {
                        // 將Bundle帶至下一頁  //需要實作序列化
                        Navigation.findNavController(requireView()).navigate(   //it指的是button
                            //(findNavController要寫:起點上的任何一個View)
                            //navigate要寫:指定目的地
                            R.id.action_mainFragment_to_page2Fragment,
                            bundle
                        ) //要帶資料
                    }
                }

            }
        })
    }
}
















