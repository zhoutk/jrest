package com.tlwl.main;

import com.tlwl.utils.Tools;
import org.json.JSONObject;

public class GlobalConst {
    public final static JSONObject CONFIGS = Tools.jsonRead("./configs.json");
}
