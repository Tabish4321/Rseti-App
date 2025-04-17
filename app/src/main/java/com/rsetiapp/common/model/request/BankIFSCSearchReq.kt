package com.rsetiapp.common.model.request

data class BankIFSCSearchReq(val appVersion : String,val ifscCode : String, val imeiNo :String,
                             val login: String)
