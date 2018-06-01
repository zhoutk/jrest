package com.tlwl.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.tlwl.main.GlobalConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.json.JSONException;

public class Tools {
    public static String encodeJsonWebToken(JSONObject payload) {
        return Jwts.builder()
                .setPayload(payload.toString())
                .signWith(SignatureAlgorithm.HS512, GlobalConst.JWT_KEY)
                .compact();
    }

    public static JSONObject decodeJsonWebToken(String pass) {
        Claims payload = null;
        try {
            payload = Jwts.parser().setSigningKey(GlobalConst.JWT_KEY)
                    .parseClaimsJws(pass).getBody();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new JSONObject(payload);
    }

    public static JSONObject jsonRead(String file) {
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
