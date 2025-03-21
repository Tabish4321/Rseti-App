package com.rsetiapp.core.uidai.kyc_resp_pojo;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Poa")
public class Poa {
    @XStreamAsAttribute
    private String co;
    @XStreamAsAttribute
    private String country;
    @XStreamAsAttribute
    private String dist;
    @XStreamAsAttribute
    private String house;
    @XStreamAsAttribute
    private String landmark;
    @XStreamAsAttribute
    private String loc;
    @XStreamAsAttribute
    private String pc;
    @XStreamAsAttribute
    private String po;
    @XStreamAsAttribute
    private String state;

    @XStreamAsAttribute
    private String subdist;

    @XStreamAsAttribute
    private String vtc;


    // Getters with null checks
    public String getCo() {
        return co != null ? co : "";
    }

    public String getCountry() {
        return country != null ? country : "";
    }

    public String getDist() {
        return dist != null ? dist : "";
    }

    public String getHouse() {
        return house != null ? house : "";
    }

    public String getLandmark() {
        return landmark != null ? landmark : "";
    }

    public String getLoc() {
        return loc != null ? loc : "";
    }

    public String getPc() {
        return pc != null ? pc : "";
    }

    public String getPo() {
        return po != null ? po : "";
    }

    public String getState() {
        return state != null ? state : "";
    }

    public String getSubdist() {
        return subdist != null ? subdist : "";
    }

    public String getVtc() {
        return vtc != null ? vtc : "";
    }

    // Setters for all fields (if needed)
    public void setCo(String co) {
        this.co = co;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setSubdist(String subdist) {
        this.subdist = subdist;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }
}
