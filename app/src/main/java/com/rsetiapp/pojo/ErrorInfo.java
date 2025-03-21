package com.rsetiapp.pojo;

public class ErrorInfo {

    private String txnId;
    private String kycErrorCode;
    private String authErrorCode;

    public ErrorInfo(String txnId, String kycErrorCode, String authErrorCode) {
        this.txnId = txnId;
        this.kycErrorCode = kycErrorCode;
        this.authErrorCode = authErrorCode;
    }

    public ErrorInfo() {
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getKycErrorCode() {
        return kycErrorCode;
    }

    public void setKycErrorCode(String kycErrorCode) {
        this.kycErrorCode = kycErrorCode;
    }

    public String getAuthErrorCode() {
        return authErrorCode;
    }

    public void setAuthErrorCode(String authErrorCode) {
        this.authErrorCode = authErrorCode;
    }
}
