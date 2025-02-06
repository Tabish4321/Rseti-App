package com.rsetiapp.core.util


object AppConstant {

    object BundleConstant{
        const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
        const val HEADING = "HEADING"
    }
    object StaticURL{
        const val BUCKET_PROFILE= "profile/"
        const val BUCKET_DOCUMENTS= "documents/"
        const val BUCKET_CATEGORY= "category/"
        const val FACE_AUTH_UIADI= "https://nregarep1.nic.in/Netnrega/StateServices/Uid_Face_Auth.svc/PostOnAUA_Face_auth"
        const val localUrl= "http://10.197.183.177:8080/"


    }

    object Constants{
        //AADHAAR CONST
        const val CAPTURE_INTENT = "in.gov.uidai.rdservice.face.CAPTURE"
        const val DEVICE_CHECK_INTENT = "in.gov.uidai.rdservice.face.CHECK_DEVICE"
        const val CAPTURE_INTENT_REQUEST = "request"
        const val CAPTURE_INTENT_RESULT = "in.gov.uidai.rdservice.face.CAPTURE_RESULT"
        const val CAPTURE_INTENT_RESPONSE_DATA = "response"
        const val DEVICE_CHECK_INTENT_RESULT = "in.gov.uidai.rdservice.face.CHECK_DEVICE_RESULT"
        const val WADH_KEY = "sgydIC09zzy6f8Lb3xaAqzKquKe9lFcNR9uTvYxFp+A="
        var ENVIRONMENT_TAG = "P"//"S"
        var LANGUAGE = "en"
         var ENCRYPT_IV_KEY = "$10A80$10A80$10A";
        var ENCRYPT_KEY = "$10A80$10A80$10A";
        var REFRESH_TOKEN_URL = "jhbheugcy2373y379y37gydygdy";
        var CLIENT_SECRET_KEY = "dgtbncbehkcbjebccnkec78yf37bc";

        const val PRE_PRODUCTION: String = "preProduction"
        const val PRODUCTION: String = "production"
        const val ENGLISH: String = "english"
        const val STAGING_CODE: String = "S"
        const val PRE_PRODUCTION_CODE: String = "PP"
        const val CURRENT_ENVIRONMENT_CODE = PRE_PRODUCTION_CODE;



        const val PRODUCTION_CODE: String = "P"


    }
}
