package com.rsetiapp.common.model.response

data class grampanchayatResponse(  val responseCode: Int,
                                   val responseDesc: String,
                                   val grampanchayatList: MutableList<GrampanchayatList>)

data class GrampanchayatList(
    val gpName: String,
    val gpCode: String,
    val lgdGpCode:String)
