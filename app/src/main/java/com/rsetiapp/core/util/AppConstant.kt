package com.rsetiapp.core.util


object AppConstant {

    object StaticURL{

        const val localUrl= "http://10.197.183.177:8080/"
        const val liveUrl= "https://nrlm.gov.in/kaushalpanjee/rseti/"
       // const val FACE_AUTH_UIADI= "https://nregarep2.nic.in/Netnrega/StateServices/Uid_Face_Auth.svc/PostOnAUA_Face_auth"
        const val FACE_AUTH_UIADI= "https://nregarep2.nic.in/netnrega/stateServices/Uid_Face_Auth_DDUGKY.svc/PostOnAUA_Face_auth"



    }

    object Constants{

        var REFRESH_TOKEN_URL = "jhbheugcy2373y379y37gydygdy";
        var CLIENT_SECRET_KEY = "dgtbncbehkcbjebccnkec78yf37bc";

        const val CRYPT_ID = "8080808080808080"
        const val CRYPT_IV = "8080808080808080"
        const val CRYPLIBAES = "AES/CBC/PKCS5PADDING"
        const val CAPTURE_INTENT_RESPONSE_DATA = "response"
        const val CAPTURE_INTENT_REQUEST = "request"
        const val CAPTURE_INTENT = "in.gov.uidai.rdservice.face.CAPTURE"
        const val PRE_PRODUCTION: String = "preProduction"
        const val PRODUCTION: String = "P"
        const val ENGLISH: String = "english"
        const val STAGING_CODE: String = "S"
        const val PRE_PRODUCTION_CODE: String = "PP"
        const val CURRENT_ENVIRONMENT_CODE = PRE_PRODUCTION_CODE;
        const val WADH_KEY = "sgydIC09zzy6f8Lb3xaAqzKquKe9lFcNR9uTvYxFp+A="
        var LANGUAGE = "en"
        var ENVIRONMENT_TAG = "P"//"S"
        var ENCRYPT_IV_KEY = "$10A80$10A80$10A";
        var ENCRYPT_KEY = "$10A80$10A80$10A";
    }
}
