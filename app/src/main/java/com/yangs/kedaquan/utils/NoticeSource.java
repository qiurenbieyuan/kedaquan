package com.yangs.kedaquan.utils;

import com.yangs.kedaquan.activity.APPAplication;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangs on 2017/9/2 0002.
 */

public class NoticeSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;

    public NoticeSource() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "kedaquan get notice").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                .followSslRedirects(false).build();
    }

    public JSONObject getNotice() {
        JSONObject jsonObject = null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs")
                .add("xh", APPAplication.save.getString("xh", "未登录"));
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/noticeAll.jsp")
                .headers(requestHeaders).post(requestBody).build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            jsonObject = new JSONObject(response.body().string());
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        } finally {
            if (response != null)
                response.close();
        }
        return jsonObject;
    }
}
