package com.rsetiapp.core.uidai.capture;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Data {
    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlText
    public String value;
}
