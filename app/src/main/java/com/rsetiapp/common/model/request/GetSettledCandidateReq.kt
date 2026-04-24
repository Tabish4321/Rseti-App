package com.rsetiapp.common.model.request

data class GetSettledCandidateReq(val login: String, val appVersion :String, val imeiNo :String,
                                  val batchId: Int)