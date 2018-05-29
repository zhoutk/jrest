package com.tlwl.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;

public class Tools {
    public static JSONObject jsonRead(String file){
        JSONObject dataJson = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = "", tmp;
            while ((tmp = br.readLine()) != null) {
                s += tmp;
            }
            try {
                dataJson = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataJson;
    }
}
