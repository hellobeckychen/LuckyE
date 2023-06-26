package com.example.luckye.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

object SendAnswer {
    var data5: String = ""

    fun postData(action: String, user: String, answer: String) {

        val urlString = API().appDomain
        val okHttpClient = OkHttpClient().newBuilder().build()


        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", action)
        jsonBody.put("user", user)
        jsonBody.put("answer", answer)

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
                println("答案答案" + (response))


                val gson = Gson()
                val json = response.body!!.string()

                //把從後端拿回來的資料從json格式轉回kotlin
                val jsonObject = gson.fromJson(json, JsonObject::class.java)

                //把轉回kotlin的物件取出來
                val code = jsonObject.get("code").asString
                data5 = jsonObject.get("result_message").asString
                println("code: $code, data5: $data5")
//                }

            }
        })
    }

    fun sendAnswerData(): String {
        return data5
    }

}