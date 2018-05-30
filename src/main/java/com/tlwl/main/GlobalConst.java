package com.tlwl.main;

import com.tlwl.utils.Tools;
import org.json.JSONObject;

public class GlobalConst {
    public final static JSONObject CONFIGS = Tools.jsonRead("./configs.json");

    private final static JSONObject CONSTS = Tools.jsonRead("./GlobalConst.json");
    public final static JSONObject OPSUCCESS = CONSTS.getJSONObject("OPSUCCESS");
    public final static JSONObject ERRORS = CONSTS.getJSONObject("ERRORS");
}
