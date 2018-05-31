package com.tlwl.main;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.tlwl.utils.Tools;
import org.json.JSONObject;

public class GlobalConst {
    private static TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
    public final static JSONObject CONFIGS = Tools.jsonRead("./configs.json");

    private final static JSONObject CONSTS = Tools.jsonRead("./GlobalConst.json");
    public final static JSONObject OPSUCCESS = CONSTS.getJSONObject("OPSUCCESS");
    public final static JSONObject ERRORS = CONSTS.getJSONObject("ERRORS");
    public final static int PAGESIZE = CONSTS.getInt("PAGESIZE");

    public enum RESTMETHOD  { GET, POST, PUT, DELETE }

    public static String uuid(){
        return uuidGenerator.generate().toString().split("-")[0];
    }
}
