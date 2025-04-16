package com.rsetiapp.common.model.request

data class SettlementFormData(

val statusItem: String,
val selfInvestmentItem: String?,
val creditFromBankItem: String?,
val totalAmount: Int?,
val ifscCode: String?,
val loanAccount: String?,
val city: String,
val reason: String,
val accountStatus: String?,
val rangeId: String?,
val employmentGiven: String?,
val familyMemberPartTime: String?,
val settlementPhoto: String?,
val passbookCopy: String?,
val appointmentLetter: String?
)

