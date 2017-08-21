package com.yangs.just.utils;

import com.yangs.just.activity.APPAplication;

import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by yangs on 2017/3/17.
 */

public class Advice {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Advice() {
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                .followSslRedirects(false).build();
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
    }

    public boolean post(String text, String lx) {
        JSONObject json = new JSONObject();
        try {
            json.put("xh", APPAplication.save.getString("xh", "null"));
            json.put("name", APPAplication.save.getString("name", "null"));
            json.put("model", android.os.Build.MODEL);
            json.put("sdk", android.os.Build.VERSION.SDK);
            json.put("versioncode", android.os.Build.VERSION.RELEASE);
            json.put("text", text);
            json.put("lx", lx);
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
            return false;
        }
        RequestBody requestBody = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("http://www.myangs.com:8080/Advice.jsp")
                .post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
            return false;
        }
        return false;
    }
}
