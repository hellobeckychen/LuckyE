package com.example.luckye.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoadQuestionsAPI {
    var data1 = mutableListOf<ResultMessage>()
    fun postData() {
        var isbool = false
        val urlString = API().appDomain
        val okHttpClient = OkHttpClient().newBuilder().build()

        //要先encode變成json格式


        val formBody: FormBody = FormBody.Builder()    //這段如何轉成json格式?  是因為這段formbody不是json所以L29錯誤嗎?
            .add("actions", "load_questions")
            .build()

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
                println("error")
            }

            //response得到的是json物件
            //需再decode成kotlin物件
            override fun onResponse(call: Call, response: Response) {
//                println("你好" + (response))
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
//                        println(getdata.答案A)
                        data1.add(getdata)
                    }
                    println("結束")
                    isbool = true
                } else {
                    isbool = false
                }
//                println(data1)
            }
        })
//        println("跑完$isbool")
    }
}



