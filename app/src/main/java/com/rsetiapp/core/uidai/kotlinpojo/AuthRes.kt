package com.rsetiapp.core.uidai.kotlinpojo

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

// Root element: AuthRes
@XStreamAlias("AuthRes")
data class AuthRes(
    @XStreamAsAttribute val code: String,
    @XStreamAsAttribute val err: String,
    @XStreamAsAttribute val info: String,
    @XStreamAsAttribute val ret: String,
    @XStreamAsAttribute val ts: String,
    @XStreamAsAttribute val txn: String,
    val Signature: Signature
)

// Nested element: Signature
@XStreamAlias("Signature")
data class Signature(
    val SignedInfo: SignedInfo,
    val SignatureValue: String
)

// Nested within Signature: SignedInfo
@XStreamAlias("SignedInfo")
data class SignedInfo(
    val CanonicalizationMethod: CanonicalizationMethod,
    val SignatureMethod: SignatureMethod,
    val Reference: Reference
)

@XStreamAlias("CanonicalizationMethod")
data class CanonicalizationMethod(@XStreamAsAttribute val Algorithm: String)

@XStreamAlias("SignatureMethod")
data class SignatureMethod(@XStreamAsAttribute val Algorithm: String)

@XStreamAlias("Reference")
data class Reference(
    @XStreamAsAttribute val URI: String,
    val Transforms: Transforms,
    val DigestMethod: DigestMethod,
    val DigestValue: String
)

@XStreamAlias("Transforms")
data class Transforms(val Transform: Transform)

@XStreamAlias("Transform")
data class Transform(@XStreamAsAttribute val Algorithm: String)

@XStreamAlias("DigestMethod")
data class DigestMethod(@XStreamAsAttribute val Algorithm: String)




