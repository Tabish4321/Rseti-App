package com.rsetiapp.common.model.request

data class FollowUpInsertReq(

    /*  val appVersion: String,
      val batchId: String,
      val candidateId: String,
      val candidatePhoto: String,
      val followUpDoneBy: String,
      val followUpType: String,
      val followUpdate: String,
      val followUpImage: String,
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

*/



    val appVersion: String,
    val batchId: String,
    val candidateId: String,
    val mobileNo: String,
    val guardianName: String,
    val guardianMobileNo: String,
    val candidatePhoto: String,
    val quarterOne: String,
    val quarterTwo: String,
    val quarterThree: String,
    val quarterFour: String,
    val quarterFive: String,
    val quarterSix: String,
    val quarterSeven: String,
    val quarterEight: String,
    val userId: String,
    val followUpType: String,
    val followUpdate: String,
    val followUpDoneBy: String,
    val sattlementStatus: String,  // Fix spelling from "sattlementStatus"
    val reason: String,
    val followupImage: String,
    val latitute: String,  // Fix spelling from "latitute"
    val longitute: String, // Fix spelling from "longitute"
    val statusItem: String,
    val selfInvestmentItem: String,
    val creditFromBankItem: String,
    val total: String,
    val upperCaseIfscText: String,
    val bankCode: String,
    val branchCode: String,
    val loanAcc: String,
    val city: String,
    val accountStatus: String,
    val rangeId: String,
    val employmentGiven: String,
    val familyMemberPartTime: String,
    val settlementPhoto: String,
    val passbookCopy: String,
    val appointmentLetter: String,
    val salaryRange: String,
      val settlementReason : String



    )