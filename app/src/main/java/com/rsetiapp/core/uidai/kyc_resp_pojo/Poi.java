package com.rsetiapp.core.uidai.kyc_resp_pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Poi")
public class Poi {
    @XStreamAsAttribute
     String dob;

    @XStreamAsAttribute
     String gender;

    @XStreamAsAttribute
     String name;


    // Getter for dob with null check
    public String getDob() {
        return dob != null ? dob : "";
    }

    // Getter for gender with null check
    public String getGender() {
        return gender != null ? gender : "";
    }

    // Getter for name with null check
    public String getName() {
        return name != null ? name : "";
    }

    // Setters for the fields (if needed)
    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }}
