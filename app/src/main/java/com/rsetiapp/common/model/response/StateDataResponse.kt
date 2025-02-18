package com.rsetiapp.common.model.response


data class StateDataResponse(
    val responseCode: Int,
    val responseDesc: String,
    val stateList: MutableList<WrappedList>
)

data class WrappedList(
    val stateName: String,
    val stateCode: String,
    val lgdStateCode:String,
    var isSelected: Boolean = false
)