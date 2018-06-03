package com.tlwl.main;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.tlwl.utils.Tools;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class GlobalConst {
    private static TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
    private static SecretKey generalKey(String jwt_secret) {
        SecretKey key = null;
        try {
            String stringKey = jwt_secret;
            byte[] encodedKey = Base64.decodeBase64(stringKey);
            key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return key;
    }

    public final static JSONObject CONFIGS = Tools.jsonRead("./configs.json");

    private final static JSONObject CONSTS = Tools.jsonRead("./GlobalConst.json");
    public final static int PAGESIZE = CONSTS.getInt("PAGESIZE");

    public static String uuid(){
        return uuidGenerator.generate().toString().split("-")[0];
    }

    public final static SecretKey JWT_KEY = generalKey(CONFIGS.getString("jwt_secret"));

    public  static JSONObject getErrorsJSON(int code){
        return new JSONObject(CONSTS.getJSONObject("ERRORS").get(Integer.valueOf(code).toString()).toString());
    }

    public  static JSONObject getSuccessJSON(){
        return  new JSONObject(CONSTS.getJSONObject("OPSUCCESS").toString());
    }
}
