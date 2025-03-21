package com.rsetiapp.core.uidai.ekyc

data class IntentModel(
    val State_Code: String,
    val ekyc_id: String,//aadhaar
    val applicant_id: String,//jcn or regNO
    val beneficiary_name: String,
    val block_code: String,
    val dist_code: String,
    val ekyc_status: String,
    val gender: String,
    val mobile_no: String,
    val panchayat_code: String,
    val scheme: String,
    val userName: String,
    val consent: String,
    val deemed_scheme: String
)