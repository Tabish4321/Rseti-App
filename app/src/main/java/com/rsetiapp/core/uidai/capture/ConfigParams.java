package com.rsetiapp.core.uidai.capture;

public class ConfigParams {

    private String auaCode;
    private String auaLicenceKey;
    private String asaCode;
    private String asaLicenceKey;
    private String subAUACode;
    private String signingCert;
    private String authUrl;
    private String eKycUrl;
    private String signingP12Path;
    private String p12Password;

    public ConfigParams(String auaCode, String auaLicenceKey, String asaCode, String asaLicenceKey, String subAUACode, String signingCert, String authUrl, String eKycUrl, String signingP12Path, String p12Password) {
        this.auaCode = auaCode;
        this.auaLicenceKey = auaLicenceKey;
        this.asaCode = asaCode;
        this.asaLicenceKey = asaLicenceKey;
        this.subAUACode = subAUACode;
        this.signingCert = signingCert;
        this.authUrl = authUrl;
        this.eKycUrl = eKycUrl;
        this.signingP12Path = signingP12Path;
        this.p12Password = p12Password;
    }

    public String getAuaCode() {
        return auaCode;
    }

    public void setAuaCode(String auaCode) {
        this.auaCode = auaCode;
    }

    public String getAuaLicenceKey() {
        return auaLicenceKey;
    }

    public void setAuaLicenceKey(String auaLicenceKey) {
        this.auaLicenceKey = auaLicenceKey;
    }

    public String getAsaCode() {
        return asaCode;
    }

    public void setAsaCode(String asaCode) {
        this.asaCode = asaCode;
    }

    public String getAsaLicenceKey() {
        return asaLicenceKey;
    }

    public void setAsaLicenceKey(String asaLicenceKey) {
        this.asaLicenceKey = asaLicenceKey;
    }

    public String getSubAUACode() {
        return subAUACode;
    }

    public void setSubAUACode(String subAUACode) {
        this.subAUACode = subAUACode;
    }

    public String getSigningCert() {
        return signingCert;
    }

    public void setSigningCert(String signingCert) {
        this.signingCert = signingCert;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String geteKycUrl() {
        return eKycUrl;
    }

    public void seteKycUrl(String eKycUrl) {
        this.eKycUrl = eKycUrl;
    }

    public String getSigningP12Path() {
        return signingP12Path;
    }

    public void setSigningP12Path(String signingP12Path) {
        this.signingP12Path = signingP12Path;
    }

    public String getP12Password() {
        return p12Password;
    }

    public void setP12Password(String p12Password) {
        this.p12Password = p12Password;
    }
}
