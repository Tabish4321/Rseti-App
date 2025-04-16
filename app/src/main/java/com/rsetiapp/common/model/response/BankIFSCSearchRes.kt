package com.rsetiapp.common.model.response


data class BankIFSCSearchRes(

    val bankDetailsList: List<BankDetailsList>,
    val responseCode: Int,
    val responseDesc: String,
    val responseMsg: String,
)

data class BankDetailsList(

    val branchCode: Int,
    val bankCode: Int,
    val branchName: String,
    val accLength: String,
    val bankName: String

)
