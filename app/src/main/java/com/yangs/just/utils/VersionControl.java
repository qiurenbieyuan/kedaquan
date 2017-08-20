package com.yangs.just.utils;

import com.yangs.just.activity.APPAplication;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by winutalk on 2017/2/14.
 */

public class VersionControl {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;

    public VersionControl() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                .followSslRedirects(false).build();
    }

    public JSONObject getServerNotice() {
        JSONObject jsonObject = null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/notice.jsp")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            jsonObject = new JSONObject(response.body().string());
        } catch (Exception e) {
        }
        return jsonObject;
    }

    public void upload(String n, String p) {
        String last = n + " " + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.SDK + " " + android.os.Build.VERSION.RELEASE;
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("txt", last);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/user_upload.jsp")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String check(String user) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("user", user)
                .add("version", APPAplication.version)
                .add("check", "ok");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/version_control.jsp")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
}
