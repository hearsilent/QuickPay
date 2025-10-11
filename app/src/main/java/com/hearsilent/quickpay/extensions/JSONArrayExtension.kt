package com.hearsilent.quickpay.extensions

import org.json.JSONArray
import org.json.JSONObject
import java.util.Random

val JSONArray.indices get() = 0 until this.length()

fun JSONArray.randomJSONObject(): JSONObject {
    return this.getJSONObject(Random().nextInt(this.length()))
}

fun JSONArray.randomString(): String {
    return this.getString(Random().nextInt(this.length()))
}