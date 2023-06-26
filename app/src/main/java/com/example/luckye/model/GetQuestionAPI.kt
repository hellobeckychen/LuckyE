package com.example.luckye.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class GetQuestionAPI {
    var data2 :String = ""
    fun postData() {

        val urlString = API().appDomain
        val okHttpClient = OkHttpClient().newBuilder().build()

        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "get_question")
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
                println("星期五"+ (response))
//                Log.d("嗨嗨嗨", "onResponse: ${response.body?.string()}")

//                val sb = StringBuilder()
                val gson = Gson()
                val json = response.body!!.string()
//                sb.append("Object to JSON:\n $json\n")
//                println(sb)
                val jsonObject = gson.fromJson(json, JsonObject::class.java)
//                val jsonArray = jsonObject.get("result_message")
                //用JsonArray去decode把jsonObject變成kotlin物件
//                val jsonArray = jsonObject.get("result_message").asJsonArray
//                for(element in jsonObject) {
//                    val jsonObject2 = element.asJsonObject
                    val code = jsonObject.get("code").asString
                    data2 = jsonObject.get("result_message").asString
                    println("code:$code, data2: $data2")



            }
        })
    }

    fun getQuestionData():String {
        return data2
    }


}


