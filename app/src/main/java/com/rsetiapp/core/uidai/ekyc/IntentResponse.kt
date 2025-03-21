package com.rsetiapp.core.uidai.ekyc

data class IntentResponse(
    var kycStatus: Boolean,
    var faceAuthStatus: Boolean,
    var partialKycStatus:Boolean,

    var uidaiStatusCode: String,
    var txnId:String?,

    var kycTimeStamp:String?,
    var faceAuthTimeStamp:String,
    var partialKycTimeStamp:String,

    var similarity:Double,
    var version:String
)