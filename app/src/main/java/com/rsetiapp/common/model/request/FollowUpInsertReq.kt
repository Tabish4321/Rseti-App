package com.rsetiapp.common.model.request

data class FollowUpInsertReq(
    val appVersion: String,
    val batchId: String,
    val candidateId: String,
    val candidatePhoto: String,
    val followUpDoneBy: String,
    val followUpType: String,
    val followUpdate: String,
    val followupimage: String,
    val guardianMobileNo: String,
    val guardianName: String,
    val latitute: String,
    val longitute: String,
    val mobileNo: String,
    val quarterEight: String,
    val quarterFive: String,
    val quarterFour: String,
    val quarterOne: String,
    val quarterSeven: String,
    val quarterSix: String,
    val quarterThree: String,
    val quarterTwo: String,
    val reason: String,
    val sattlementStatus: String,
    val userId: String
)