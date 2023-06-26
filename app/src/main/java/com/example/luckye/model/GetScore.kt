package com.example.luckye.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class GetScore {
    var data6 = ""

    fun postData() {

        val urlString = API().appDomain
        val okHttpClient = OkHttpClient().newBuilder().build()


        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "get_score")
        jsonBody.put("user", "test")

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
                val message = outerJsonObject?.get("message")?.asString
                val innerJsonObject = message?.let { gson.fromJson(it, JsonObject::class.java) }
                val totalCount = innerJsonObject?.get("total_count")?.asString
                val rightCount = innerJsonObject?.get("right_count")?.asString
                val score = innerJsonObject?.get("score")?.asString

                println("code: $code")
                println("total_count: $totalCount")
                println("right_count: $rightCount")
                println("score: $score")



            }
        })
    }

    fun getScoreData():String {
        return data6
    }
}
