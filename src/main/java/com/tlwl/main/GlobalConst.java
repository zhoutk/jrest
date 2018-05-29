package com.tlwl.main;

import com.tlwl.utils.Tools;
import org.json.JSONObject;

public class GlobalConst {
    public final static JSONObject CONFIGS = Tools.jsonRead("./configs.json");

    public final static JSONObject OPSUCCESS = new JSONObject("{" +
            "\"code\": 200, \"message\":\"the operation is success.\"" +
            "}");

    public final static JSONObject ERRORS = new JSONObject("{" +
            "\"204\": {\"code\": 204, \"err\":\"the rest api is banned.\"}" + "," +
            "\"404\": {\"code\": 404, \"err\":\"the rest api is not exist.\"}" +
            "}");
}
