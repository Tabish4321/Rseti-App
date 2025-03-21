package com.rsetiapp.core.uidai;

import android.content.Context;
import android.util.Base64;

import com.rsetiapp.core.uidai.kotlinpojo.CanonicalizationMethod;
import com.rsetiapp.core.uidai.kotlinpojo.DigestMethod;
import com.rsetiapp.core.uidai.kotlinpojo.Reference;
import com.rsetiapp.core.uidai.kotlinpojo.Signature;
import com.rsetiapp.core.uidai.kotlinpojo.SignatureMethod;
import com.rsetiapp.core.uidai.kotlinpojo.SignedInfo;
import com.rsetiapp.core.uidai.kotlinpojo.Transform;
import com.rsetiapp.core.uidai.kotlinpojo.Transforms;
import com.rsetiapp.core.uidai.kyc_resp_pojo.KycRes;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

public class XstreamCommonMethods {


    public static PidData pidXmlToPojo(String xml) {
        String receivedXml = xml;
        XStream xstream = new XStream();
        xstream.processAnnotations(PidData.class);
        xstream.processAnnotations(Param.class);
        xstream.processAnnotations(Resp.class);
        xstream.processAnnotations(Skey.class);
        xstream.processAnnotations(CustOpts.class);
        xstream.autodetectAnnotations(true);

        if (receivedXml.equals("")) {
            return null;
        }

        PidData data = (PidData) xstream.fromXML(receivedXml);
        return data;

    }

    public static String respXmlToPojo(String xml) {
        String receivedXml = xml;
        XStream xstream = new XStream();
        xstream.processAnnotations(AuthRes.class);
        xstream.autodetectAnnotations(true);

        if (receivedXml.equals("")) {
            return null;
        }
        return receivedXml;
        // Object obj=xstream.fromXML(receivedXml);
        //  KycRes data = (KycRes) xstream.fromXML(receivedXml);
        //  AuthRes data = (AuthRes) xstream.fromXML(receivedXml);
        // return data;

    }

    public static KycRes respDecodedXmlToPojoEkyc(String xml) {
        String receivedXml = xml;
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.processAnnotations(KycRes.class);
        xstream.autodetectAnnotations(true);


        if (receivedXml.equals("")) {
            return null;
        }

        KycRes data = (KycRes) xstream.fromXML(receivedXml);
        return data;

    }

    public static com.rsetiapp.core.uidai.kotlinpojo.AuthRes parseXmlWithXStream(String xml) {
        XStream xstream = new XStream(new DomDriver());

        // Security setup
        xstream.addPermission(AnyTypePermission.ANY); // Allow all types (use carefully in trusted environments)

        // Allow only specific types
        xstream.allowTypes(new Class[] {
                com.rsetiapp.core.uidai.kotlinpojo.AuthRes.class,
                Signature.class,
                SignedInfo.class,
                CanonicalizationMethod.class,
                SignatureMethod.class,
                Reference.class,
                Transforms.class,
                Transform.class,
                DigestMethod.class
        });

        // Process annotations
        xstream.processAnnotations(AuthRes.class);

        // Parse XML to object
        return (com.rsetiapp.core.uidai.kotlinpojo.AuthRes) xstream.fromXML(xml);
    }


    public static AuthRes respDecodedXmlToPojoAuth(String xml) {
        String receivedXml = xml;
        XStream xstream = new XStream();
        xstream.processAnnotations(AuthRes.class);
        xstream.autodetectAnnotations(true);

        if (receivedXml.equals("")) {
            return null;
        }

        AuthRes data = (AuthRes) xstream.fromXML(receivedXml);
        return data;
    }

    public static String processPidBlockEkyc(String pidXml, String uid, Boolean isOtpUsed, Context context) throws Exception {

        AssetsPropertyReader assetsPropertyReader;
        Properties face_auth_properties;
        // Parsing received pid block xml
        assetsPropertyReader = new AssetsPropertyReader(context);
        face_auth_properties = assetsPropertyReader.getProperties("face_auth.properties");

        PidData pidDataObject = pidXmlToPojo(pidXml);

        //  ConfigParams configParams = ConfigUtils.Companion.getConfigData(ConfigUtils.Companion.getSelectedConfigEnv());
        // Constructing Auth xml

        XStream xtremeForAuthXml = new XStream();
        xtremeForAuthXml.processAnnotations(Auth.class);
        Auth authxml = new Auth();
        authxml.setUid(uid);
        authxml.setVer(face_auth_properties.getProperty("AUTH_VERSION"));
        authxml.setTid(face_auth_properties.getProperty("AUTH_TID"));
        authxml.setRc(face_auth_properties.getProperty("RESIDENT_CONCENT"));


        authxml.setAc(face_auth_properties.getProperty("AUA_CODE"));
        authxml.setSa(face_auth_properties.getProperty("SUB_AUA_CODE"));
        authxml.setLk(face_auth_properties.getProperty("AUA_LICENSE_KEY"));


        Skey skey = new Skey();
        skey.setCi(pidDataObject.getSkey().getCi());
        skey.setContent(pidDataObject.getSkey().getContent());
        authxml.setSkey(skey);
        Meta meta = new Meta();
        meta.setDc(pidDataObject.getDeviceInfo().getDc());
        meta.setDpId(pidDataObject.getDeviceInfo().getDpId());
        meta.setRdsVer(pidDataObject.getDeviceInfo().getRdsVer());
        meta.setRdsId(pidDataObject.getDeviceInfo().getRdsId());
        meta.setMi(pidDataObject.getDeviceInfo().getMi());
        meta.setMc(pidDataObject.getDeviceInfo().getMc());
        Data data = new Data();
        data.setContent(pidDataObject.getData().getContent());
        data.setType(pidDataObject.getData().getType());
        authxml.setHmac(pidDataObject.getHmac());


        Uses uses = new Uses();
        uses.setBio("y");
        uses.setBt("FID");
        if (isOtpUsed) {
            uses.setOtp("y");
        } else {
            uses.setOtp("n");
        }
        uses.setPa("n");
        uses.setPfa("n");
        uses.setPi("n");
        uses.setPin("n");

        authxml.setUses(uses);

        authxml.setTxn("UKC:" + UUID.randomUUID().toString());

        //  authxml.setTxn(UUID.randomUUID().toString());


        authxml.setData(data);
        authxml.setMeta(meta);

        String authXml = xtremeForAuthXml.toXML(authxml);


        DigitalSigner ds = new DigitalSigner(context);


        authXml = ds.signXML(authXml, true);

        XStream xtremeForKycXml = new XStream();
        xtremeForKycXml.processAnnotations(Kyc.class);
        xtremeForKycXml.autodetectAnnotations(true);
        Kyc kycxml = new Kyc();
        kycxml.setRc("Y");
        kycxml.setVer(face_auth_properties.getProperty("AUTH_VERSION"));
        kycxml.setLr("N");
        kycxml.setRa("P");
        kycxml.setDe("N");
        kycxml.setPfr("N");
        String rad = "";
        rad = Base64.encodeToString(authXml.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        kycxml.setRad(rad);

        String kycXml = xtremeForKycXml.toXML(kycxml);

       // return authXml;
        return kycXml;
    }

    public static String processPidBlockAuth(String pidXml, String uid, Boolean isOtpUsed, Context context) throws Exception {

        AssetsPropertyReader assetsPropertyReader;
        Properties face_auth_properties;
        // Parsing received pid block xml
        assetsPropertyReader = new AssetsPropertyReader(context);
        face_auth_properties = assetsPropertyReader.getProperties("face_auth.properties");

        PidData pidDataObject = pidXmlToPojo(pidXml);

        //  ConfigParams configParams = ConfigUtils.Companion.getConfigData(ConfigUtils.Companion.getSelectedConfigEnv());
        // Constructing Auth xml

        XStream xtremeForAuthXml = new XStream();
        xtremeForAuthXml.processAnnotations(Auth.class);
        Auth authxml = new Auth();
        authxml.setUid(uid);
        authxml.setVer(face_auth_properties.getProperty("AUTH_VERSION"));
        authxml.setTid(face_auth_properties.getProperty("AUTH_TID"));
        authxml.setRc(face_auth_properties.getProperty("RESIDENT_CONCENT"));


        authxml.setAc(face_auth_properties.getProperty("AUA_CODE"));
        authxml.setSa(face_auth_properties.getProperty("SUB_AUA_CODE"));
        authxml.setLk(face_auth_properties.getProperty("AUA_LICENSE_KEY"));

/*        authxml.setAc("public");
        authxml.setSa("ZZ1111PMAYG");
        authxml.setLk("PMAYGPDLyTd23aDYEC7T");*/


        Skey skey = new Skey();
        skey.setCi(pidDataObject.getSkey().getCi());
        skey.setContent(pidDataObject.getSkey().getContent());
        authxml.setSkey(skey);
        Meta meta = new Meta();
        meta.setDc(pidDataObject.getDeviceInfo().getDc());
        meta.setDpId(pidDataObject.getDeviceInfo().getDpId());
        meta.setRdsVer(pidDataObject.getDeviceInfo().getRdsVer());
        meta.setRdsId(pidDataObject.getDeviceInfo().getRdsId());
        meta.setMi(pidDataObject.getDeviceInfo().getMi());
        meta.setMc(pidDataObject.getDeviceInfo().getMc());
        Data data = new Data();
        data.setContent(pidDataObject.getData().getContent());
        data.setType(pidDataObject.getData().getType());
        authxml.setHmac(pidDataObject.getHmac());


        Uses uses = new Uses();
        uses.setBio("y");
        uses.setBt("FID");
        if (isOtpUsed) {
            uses.setOtp("y");
        } else {
            uses.setOtp("n");
        }
        uses.setPa("n");
        uses.setPfa("n");
        uses.setPi("n");
        uses.setPin("n");

        authxml.setUses(uses);

       // authxml.setTxn("UKC:" + UUID.randomUUID().toString());

        authxml.setTxn(UUID.randomUUID().toString());


        authxml.setData(data);
        authxml.setMeta(meta);

        String authXml = xtremeForAuthXml.toXML(authxml);


        DigitalSigner ds = new DigitalSigner(context);


        authXml = ds.signXML(authXml, true);

    /*    XStream xtremeForKycXml = new XStream();
        xtremeForKycXml.processAnnotations(Kyc.class);
        xtremeForKycXml.autodetectAnnotations(true);
        Kyc kycxml = new Kyc();
        kycxml.setRc("Y");
        kycxml.setVer(face_auth_properties.getProperty("AUTH_VERSION"));
        kycxml.setLr("N");
        kycxml.setRa("P");
        kycxml.setDe("N");
        kycxml.setPfr("N");
        String rad = "";
        try {
            rad = Base64.encodeToString(authXml.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        kycxml.setRad(rad);

        String kycXml = xtremeForKycXml.toXML(kycxml);*/

        return authXml;
      //  return kycXml;
    }

    public static String getAuthErrorDescription(String err) {
        switch (err) {
            case "100":
                return "100 - Pi (basic) attributes of demographic data did not match.";
            case "200":
                return "200 - Pa (address) attributes of demographic data did not match";
            case "300":
                //    return "300 - Face did not match with aadhaar details";
                return "300 - Biometric data did not match";
            case "310":
                return "310 - Duplicate fingers used";
            case "311":
                return "311 - Duplicate Irises used.";
            case "312":
                return "312 - FMR and FIR cannot be used in same transaction";
            case "313":
                return "313 - Single FIR record contains more than one finger";
            case "314":
                return "314 - Number of FMR/FIR should not exceed 10";
            case "315":
                return "315 - Number of IIR should not exceed 2";
            case "400":
                return "400 - Invalid OTP value";
            case "401":
                return "401 - Invalid TKN value";
            case "500":
                return "500 - Invalid encryption of Skey";
            case "501":
                return "501 - Invalid certificate identifier in ci attribute of Skey";
            case "502":
                return "502 - Invalid encryption of Pid";
            case "503":
                return "503 - Invalid encryption of Hmac";
            case "504":
                return "504 - Session key re-initiation required due texpiry or key out of sync";
            case "505":
                return "505 - Synchronized Key usage not allowed for the AUA";
            case "510":
                return "510 - Invalid Auth XML format";
            case "511":
                return "511 - Invalid PID XML format";
            case "520":
                return "520 - Invalid device";
            case "521":
                return "521 - Invalid FDC code under Meta tag";
            case "522":
                return "522 - Invalid IDC code under Meta tag";
            case "530":
                return "530 - Invalid authenticator code";
            case "540":
                return "540 - Invalid Auth XML version";
            case "541":
                return "541 - Invalid PID XML version";
            case "542":
                return "542 - AUA not authorized for ASA. This error will be returned if AUA and ASA dnot have linking in the portal";
            case "543":
                return "543 - Sub-AUA not associated with AUA. This error will be returned if Sub-AUA specified in sa attribute is not added as Sub-AUA in portal";
            case "550":
                return "550 - Invalid Uses element attributes";
            case "551":
                return "551 - Invalid tid value for registered device";
            case "552":
                return "552 - Invalid registered device key, please reset";
            case "553":
                return "553 - Invalid registered device HOTP, please reset";
            case "554":
                return "554 - Invalid registered device encryption";
            case "555":
                return "555 - Mandatory reset required for registered device";
            case "561":
                return "561 - Request expired (Pid->ts value is older than N hours where N is a configured threshold in authentication server)";
            case "562":
                return "562 - Timestamp value is future time (value specified Pid->ts is ahead of authentication server time beyond acceptable threshold)";
            case "563":
                return "563 - Duplicate request (this error occurs when exactly same authentication request was re-sent by AUA)";
            case "564":
                return "564 - HMAC Validation failed";
            case "565":
                return "565 - AUA license has expired";
            case "566":
                return "566 - Invalid non-decryptable license key";
            case "567":
                return "567 - Invalid input (this error occurs when some unsupported characters were found in Indian language values, lname or lav)";
            case "568":
                return "568 - Unsupported Language";
            case "569":
                return "569 - Digital signature verification failed (means that authentication request XML was modified after it was signed)";
            case "570":
                return "570 - Invalid key infin digital signature (this means that certificate used for signing the authentication request is not valid – it is either expired, or does not belong tthe AUA or is not created by a well-known Certification Authority)";
            case "571":
                return "571 - PIN Requires reset (this error will be returned if resident is using the default PIN which needs tbe reset before usage)";
            case "572":
                return "572 - Invalid biometric position";
            case "573":
                return "573 - Pi usage not allowed as per license";
            case "574":
                return "574 - Pa usage not allowed as per license";
            case "575":
                return "575 - Pfa usage not allowed as per license";
            case "576":
                return "576 - FMR usage not allowed as per license";
            case "577":
                return "577 - FIR usage not allowed as per license";
            case "578":
                return "578 - IIR usage not allowed as per license";
            case "579":
                return "579 - OTP usage not allowed as per license";
            case "580":
                return "580 - PIN usage not allowed as per license";
            case "581":
                return "581 - Fuzzy matching usage not allowed as per license";
            case "582":
                return "582 - Local language usage not allowed as per license";
            case "584":
                return "584 - Invalid pincode in LOV attribute under Meta tag";
            case "585":
                return "585 - Invalid geo-code in LOV attribute under Meta tag";
            case "710":
                return "710 - Missing Pi data as specified in Uses";
            case "720":
                return "720 - Missing Pa data as specified in Uses";
            case "721":
                return "721 - Missing Pfa data as specified in Uses";
            case "730":
                return "730 - Missing PIN data as specified in Uses";
            case "740":
                return "740 - Missing OTP data as specified in Uses";
            case "800":
                return "800 - Invalid biometric data";
            case "810":
                return "810 - Missing biometric data as specified in Uses";
            case "811":
                return "811 - Missing biometric data in CIDR for the given Aadhaar number";
            case "812":
                return "812 - Resident has not done Best Finger Detection. Application should initiate BFD application thelp resident identify their best fingers. See Aadhaar Best Finger Detection API specification.";
            case "820":
                return "820 - Missing or empty value for bt attribute in Uses element";
            case "821":
                return "821 - Invalid value in the bt attribute of Uses element";
            case "901":
                return "901 - Nauthentication data found in the request (this corresponds ta scenariwherein none of the auth data – Demo, Pv, or Bios – is present)";
            case "902":
                return "902 - Invalid dob value in the Pi element (this corresponds ta scenarios wherein dob attribute is not of the format YYYY or YYYY-MM-DD, or the age of resident is not in valid range)";
            case "910":
                return "910 - Invalid mv value in the Pi element";
            case "911":
                return "911 - Invalid mv value in the Pfa element";
            case "912":
                return "912 - Invalid ms value";
            case "913":
                return "913 - Both Pa and Pfa are present in the authentication request (Pa and Pfa are mutually exclusive)";
            case "930 t939":
                return "930 t939 - Technical error that are internal tauthentication server";
            case "940":
                return "940 - Unauthorized ASA channel";
            case "941":
                return "941 - Unspecified ASA channel";
            case "980":
                return "980 - Unsupported option";
            case "997":
                return "997 - Invalid Aadhaar status (Aadhaar is not in authenticatable status)";
            case "998":
                return "998 - Invalid Aadhaar Number";
            case "999":
                return "999 - Unknown error";
            default:
                return "NA - " + err;
        }
    }
}
