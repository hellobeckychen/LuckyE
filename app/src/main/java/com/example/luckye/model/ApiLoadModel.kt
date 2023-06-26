package com.example.luckye.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.Serializable


data class ApiloadModel(
    var code: Int,
    var result_message: List<ResultMessage>?
)

data class ResultMessage(
    var 題號: String?,
    var 題目: String?,
    var 答案A: String?,
    var 答案B: String?,
    var 答案C: String?,
    var 答案D: String?,
): Serializable

