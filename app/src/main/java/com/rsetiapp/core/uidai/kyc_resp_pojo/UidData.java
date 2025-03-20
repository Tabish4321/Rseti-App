package com.rsetiapp.core.uidai.kyc_resp_pojo;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UidData")
public class UidData {
    @JacksonXmlProperty(localName = "Pht")
    private String Pht;

    @JacksonXmlProperty(localName = "Poi")
    private Poi Poi;

    @JacksonXmlProperty(localName = "Poa")
    private Poa Poa;

    @JacksonXmlProperty(localName = "LData")
    private String LData;

    public Poi getPoi() {
        return Poi;
    }

    // Setter for Poi
    public void setPoi(Poi poi) {
        this.Poi = poi;
    }

    // Getter for Poa
    public Poa getPoa() {
        return Poa;
    }

    // Setter for Poa
    public void setPoa(Poa poa) {
        this.Poa = poa;
    }


    public String getPht() {
        if (Pht == null) {
            return "";
        }
        return Pht;
    }

    @Nullable
    public String getName(){
        if (Poi==null)
            return "";
        return Poi.getName();
    }

    public void setName(){
        if (Poi==null)
            getPoi().name = "";
        else getPoi().setName(getPoi().name);
    }

    @Nullable
    public String getGender(){
        if (Poi==null)
            return "";
        return Poi.getGender();
    }

    @Nullable
    public String getDob(){
        if (Poi==null)
            return "";
        return Poi.getDob();
    }

    @Nullable
    public String getCareOf(){
        if (Poa==null)
            return "";
        return Poa.getCo();
    }
    @Nullable
    public String getDistrict(){
        if (Poa==null)
            return "";
        return Poa.getDist();
    }

    @Nullable
    public String getPc(){
        if (Poa==null)
            return "";
        return Poa.getPc();
    }

    @Nullable
    public String getPo(){
        if (Poa==null)
            return "";
        return Poa.getPo();
    }
     @Nullable
    public String getState(){
        if (Poa==null)
            return "";
        return Poa.getState();
    }

    @Nullable
    public String getsubDist(){
        if (Poa==null)
            return "";
        return Poa.getSubdist();
    }

   /* @Nullable
    public String getStreet(){
        if (Poa==null)
            return "";
        return Poa.getStreet();
    }*/
    @Nullable
    public String getVtc(){
        if (Poa==null)
            return "";
        return Poa.getVtc();
    }

    @Nullable
    public String getHouse(){
        if (Poa==null)
            return "";
        return Poa.getHouse();
    }

    @Nullable
    public String getLandmark(){
        if (Poa==null)
            return "";
        return Poa.getLandmark();
    }

    @Nullable
    public String getLoc(){
        if (Poa==null)
            return "";
        return Poa.getLoc();
    }

}
