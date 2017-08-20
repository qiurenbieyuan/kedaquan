package com.yangs.just.utils;

import com.yangs.just.activity.APPAplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by winutalk on 2017/4/27.
 */

public class FindUrl {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;

    public FindUrl() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "KeDaQuan FindUrl").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                .followSslRedirects(false).build();
    }

    public List<String> getImgUrl() {
        List<String> list = new ArrayList<String>();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/getImgUrl.jsp")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            JSONObject object = new JSONObject(response.body().string());
            list.add(object.getString("1"));
            list.add(object.getString("2"));
            list.add(object.getString("3"));
        } catch (Exception e) {
            APPAplication.showToast(e.getMessage(), 1);
        }
        return list;
    }

    public List<String> getUrl() {
        List<String> list = new ArrayList<String>();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/getFindUrl.jsp")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            JSONObject object = new JSONObject(response.body().string());
            list.add(object.getString("1"));
            list.add(object.getString("2"));
            list.add(object.getString("3"));
        } catch (Exception e) {
            //APPAplication.showToast(e.getMessage(), 1);
        }
        return list;
    }
}
