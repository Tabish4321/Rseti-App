package com.rsetiapp.common.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UidaiResp(
  //  val d: String
    @SerializedName("PostOnAUA_Face_authResult")
    val PostOnAUA_Face_authResult:String
):Serializable