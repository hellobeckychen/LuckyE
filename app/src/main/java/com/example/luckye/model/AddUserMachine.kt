package com.example.luckye.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class AddUserMachine {
    var data3 : String = ""

    open fun postData() {

        val urlString = API().appDomain
        val okHttpClient = OkHttpClient().newBuilder().build()


        //要先encode變成json格式
        val jsonBody = JSONObject()
        jsonBody.put("actions", "add_user_machine")
        jsonBody.put("machine_id", "uuid_test")
        jsonBody.put("user", "test2")

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
                println("星期六"+ (response))


                val gson = Gson()
                val json = response.body!!.string()

                //把從後端拿回來的資料從json格式轉回kotlin
                val jsonObject = gson.fromJson(json, JsonObject::class.java)

                //把轉回kotlin的物件取出來
                val code = jsonObject.get("code").asString
                data3 = jsonObject.get("result_message").asString
                println("code: $code, data3: $data3")
//                }
//                //用for迴圈,走訪把裡面的物件取出來
//                for (element in jsonArray) {
//                    val jsonObject = element.asJsonObject
//                    val questionNum = jsonObject.get("題號").asString
//                    val topic = jsonObject.get("題目").asString
//                    println("題號: $questionNum, 題目: $topic")
//                }

//            private fun println(s: String, string: String) {

            }
        })
    }

    fun addUserMachineData():String {
        return data3
    }

}