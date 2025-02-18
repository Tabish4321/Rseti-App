package com.rsetiapp.common.model.response

data class VillageResponse( val responseCode: Int,
                           val responseDesc: String,
                           val villageList: MutableList<VillageList>)

data class VillageList(
    val villageName: String,
    val villageCode: String,
    val lgdVillageCode:String)
