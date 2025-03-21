package com.rsetiapp.core.uidai.capture;

import android.content.Context;


import com.rsetiapp.core.uidai.AssetsPropertyReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class ConfigUtils {

    public static String STAGING_CONFIG_DATA = "stagingConfigData";
    public static String PREPROD_CONFIG_DATA = "preProdConfigData";
    public static String PROD_CONFIG_DATA = "prodConfigData";
    public static String DEFAULT_CONFIG_DATA = "defaultConfigData";
    public static String certFileSuffix = "-cert.p12";

    public static ConfigParams getConfigData(Context context) {

        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(context);
        Properties faceAuthProperties = assetsPropertyReader.getProperties("face_auth.properties");

        InputStream cert = null;
        String certContent = "";
        try {
            cert = context.getAssets().open(faceAuthProperties.getProperty("PUBLIC_KEY_CER"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(cert, "UTF-8"));
            certContent = br.readLine();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigParams configParams = new ConfigParams(faceAuthProperties.getProperty("AUA_CODE"),
                faceAuthProperties.getProperty("AUA_LICENSE_KEY"),
                faceAuthProperties.getProperty("ASA_CODE"),
                faceAuthProperties.getProperty("ASA_LICENSE_KEY"),
                faceAuthProperties.getProperty("SUB_AUA_CODE"),
                certContent,
                faceAuthProperties.getProperty("KYC_STAGING_URL"),
                faceAuthProperties.getProperty("KYC_STAGING_URL"),
                "default",
                faceAuthProperties.getProperty("P12_PASSWORD"));

        return configParams;
    }
}