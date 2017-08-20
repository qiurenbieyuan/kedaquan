package com.yangs.just.bbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.yangs.just.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by winutalk on 2017/8/2.
 */

public class BBSSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    public String cookie;
    private String user;
    private String pwd;
    private String edit_url = "";   //发帖页面地址
    private String post_url = "";   //发帖提交地址
    private String reply_url = "";    //回复地址
    public String user_url = "";    //用户家地址
    private String hash = "";
    private Context context;
    private int page = 0;
    private int bigPage = 0;

    public BBSSource() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (Linux; Android ; kedaquan) AppleWebKit Chrome Mobile").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build();
        user = null;
        pwd = null;
        cookie = APPAplication.save.getString("bbs_cookie", "");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getPage() {
        return page;
    }

    public int getBigpage() {
        return bigPage;
    }

    public int login(String user, String pwd, Boolean flag) {
        this.user = user;
        this.pwd = pwd;
        Request request = new Request.Builder().url("http://www.myangs.com:81/member.php?mod=logging&action=login&mobile=2")
                .headers(requestHeaders).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            String loginurl = "http://www.myangs.com:81/" + document.select("form#loginform").attr("action");
            FormBody.Builder formBodyBuilder = new FormBody.Builder().add("fastloginfield", "username")
                    .add("cookietime", "2592000").add("username", this.user).add("password", this.pwd)
                    .add("questionid", "0").add("answer", "");
            RequestBody requestBody = formBodyBuilder.build();
            request = new Request.Builder().url(loginurl).post(requestBody).headers(requestHeaders).build();
            try {
                response = mOkHttpClient.newCall(request).execute();
                List<String> cookielist = response.headers("Set-Cookie");
                final String s = response.body().string();
                if (s.contains("欢迎您回来")) {
                    cookie = "";
                    for (int i = 0; i < cookielist.size(); i++) {
                        cookie += cookielist.get(i) + ";";
                    }
                    APPAplication.save.edit().putString("bbs_user", this.user)
                            .putString("bbs_cookie", cookie).apply();
                    if (flag)
                        APPAplication.save.edit().putString("bbs_pwd", this.pwd).apply();
                    APPAplication.bbs_login_status = true;
                    return 1;
                } else if (s.contains("登录失败，您")) {
                    return -1;
                } else {
                    APPAplication.sendHandler(new Runnable() {
                        @Override
                        public void run() {
                            APPAplication.showDialog2(context, s, "登录失败");
                        }
                    });
                    return -3;
                }
            } catch (Exception e) {
                return -2;
            }
        } catch (Exception e) {
            return -2;
        }
    }

    public String register(String user, String pwd, String email) {
        String url = "http://www.myangs.com:81/member.php?mod=register&mobile=2";
        Request request = new Request.Builder().url(url).headers(requestHeaders).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            List<String> cookielist = response.headers("Set-Cookie");
            cookie = "";
            for (int i = 0; i < cookielist.size(); i++) {
                cookie += cookielist.get(i) + ";";
            }
            String regis_hash = document.getElementsByAttributeValue("name", "formhash").attr("value");
            String a1 = document.getElementsByAttributeValue("placeholder", "用户名：3-15位").attr("name");
            String a2 = document.getElementsByAttributeValue("placeholder", "密码").attr("name");
            String a3 = document.getElementsByAttributeValue("placeholder", "确认密码").attr("name");
            String a4 = document.getElementsByAttributeValue("placeholder", "邮箱").attr("name");
            url = "http://www.myangs.com:81/member.php?mod=register&mobile=2&handlekey=registerform&inajax=1";
            FormBody.Builder formBodyBuilder = new FormBody.Builder().add("regsubmit", "yes")
                    .add("formhash", regis_hash).add("referer", "forum.php")
                    .add("activationauth", "").add("agreebbrule", "").add(a1, user)
                    .add(a2, pwd).add(a3, pwd)
                    .add(a4, email);
            RequestBody requestBody = formBodyBuilder.build();
            request = new Request.Builder().url(url).headers(requestHeaders).post(requestBody)
                    .header("cookie", cookie).build();
            try {
                response = mOkHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public List<BBS> getList(String url) {
        List<BBS> list = new ArrayList<BBS>();
        Request request = new Request.Builder().url(url).headers(requestHeaders).header("cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            if (document.select("div.footer").text().contains("退出")) {   //检验是否登录持久化
                APPAplication.bbs_login_status = true;
                user_url = document.select("div.footer").select("a").first().attr("href");
            } else {
                APPAplication.bbs_login_status = false;
            }
            edit_url = "http://www.myangs.com:81/" + document.select("div.icon_edit.y").select("a").attr("href");
            if (document.text().contains("本版块或指定的范围内尚无主题")) {
                return list;
            }
            try {
                bigPage = Integer.parseInt(document.select("div.pg").select("span").attr("title")
                        .replaceAll("共", "").replaceAll("页", "").replaceAll("\\s", ""));
            } catch (Exception e) {
                bigPage = 1;
            }
            Elements elements = document.select("div.threadlist").select("li");
            for (Element element : elements) {
                BBS bbs = new BBS();
                bbs.setUrl("http://www.myangs.com:81/" + element.select("a").attr("href"));
                bbs.setTitle(element.select("a").first().ownText());
                bbs.setNum(element.select("span.num").text());
                bbs.setUser(element.select("a").select("span.by").text());
                list.add(bbs);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<BBSDetail> getDetailList(String url) {
        List<BBSDetail> list = new ArrayList<BBSDetail>();
        Request request = new Request.Builder().url(url).headers(requestHeaders).header("cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            reply_url = document.select("form#fastpostform").attr("action")
                    .replaceAll("&amp;", "&");
            if (document.select("div.footer").text().contains("退出")) {   //检验是否登录持久化
                APPAplication.bbs_login_status = true;
                hash = document.getElementsByAttributeValue("name", "formhash").attr("value");
            } else {
                APPAplication.bbs_login_status = false;
                hash = "";
            }
            try {
                page = Integer.parseInt(document.select("div.pg").select("span").attr("title")
                        .replaceAll("共", "").replaceAll("页", "").replaceAll("\\s", ""));
            } catch (Exception e) {
                page = 1;
            }
            Elements elements = document.select("div.plc.cl");
            for (int i = 0; i < elements.size() - 1; i++) {
                Element element2 = elements.get(i);
                BBSDetail bbsDetail = new BBSDetail();
                bbsDetail.setAvatar(element2.select("span.avatar").select("img").attr("src"));
                bbsDetail.setIndex(element2.select("li.grey").first().select("em").text());
                bbsDetail.setUser_url(element2.select("li.grey").first().select("a").attr("href"));
                bbsDetail.setTime(element2.select("li.grey.rela").first().ownText());
                bbsDetail.setUser(element2.select("li.grey").first().select("a").text());
                bbsDetail.setContent(element2.select("div.message").html());
                bbsDetail.setReplay_me_url("http://www.myangs.com:81/" +
                        element2.select("input.redirect.button").attr("href").replaceAll("&amp;", "&"));
                list.add(bbsDetail);
            }

        } catch (Exception e) {
        }
        return list;
    }

    public int postArticle(String title, String msg) {
        if (edit_url.equals("")) {
            return -3;
        }
        Request request = new Request.Builder().url("http://www.myangs.com:81/forum.php?mod=forumdisplay&fid=46&mobile=1")
                .headers(requestHeaders).header("cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            edit_url = "http://www.myangs.com:81/" + document.select("div.icon_edit.y").select("a").attr("href");
        } catch (Exception e) {
            e.printStackTrace();
        }
        request = new Request.Builder().url(edit_url).headers(requestHeaders).header("cookie", cookie)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            final Document document = Jsoup.parse(response.body().string());
            if (document.body().toString().contains("目前处于见习期间")) {
                APPAplication.sendHandler(new Runnable() {
                    @Override
                    public void run() {
                        APPAplication.showDialog(context, "抱歉，您目前处于见习期间，需要等待 2" +
                                " 分钟后才能进行本操作");
                    }
                });
                return -4;
            }
            hash = document.select("input#formhash").attr("value");
            post_url = "http://www.myangs.com:81/" + document.select("form#postform").attr("action")
                    + "&geoloc=&handlekey=postform&inajax=1";
            String posttime = document.select("input#posttime").attr("value");
            FormBody.Builder formBodyBuilder = new FormBody.Builder().add("formhash", hash)
                    .add("posttime", posttime).add("topicsubmit", "yes").add("subject", title)
                    .add("message", msg);
            RequestBody requestBody = formBodyBuilder.build();
            request = new Request.Builder().url(post_url).headers(requestHeaders)
                    .header("Cookie", cookie).post(requestBody).build();
            try {
                response = mOkHttpClient.newCall(request).execute();
                final String result = response.body().string();
                if (result.contains("您的主题已发布"))
                    return 0;
                else {
                    APPAplication.sendHandler(new Runnable() {
                        @Override
                        public void run() {
                            if (result.contains("小于 10 个字符")) {
                                APPAplication.showDialog(context, "发表失败,内容小于 10 个字符！");
                            } else if (result.contains("两次发表间隔少于")) {
                                APPAplication.showDialog(context, "发表失败,两次发表间隔少于15秒！");
                            } else {
                                APPAplication.showDialog2(context, result, "发表失败");
                            }
                        }
                    });
                    return -4;
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return -2;
    }

    public int replayArticle(String msg) {
        if (reply_url.equals(""))
            return -1;     //没有取得有效的回复地址
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("formhash", hash).add("message", msg);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:81/" + reply_url
                + "&handlekey=fastpost&loc=1&inajax=1")
                .headers(requestHeaders).header("cookie", cookie).post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            String s = response.body().string();
            if (s.contains("发布成功")) {
                return 0;   //Success
            } else if (s.contains("您的帖子小于")) {
                return -4;
            } else if (s.contains("两次发表间隔少于")) {
                return -5;
            } else if (s.contains("目前处于见习期间")) {
                return -6;
            } else {
                return -2;  //您的请求来路不正确或表单验证串不符Or Other
            }
        } catch (Exception e) {
        }
        return -3;
    }

    public int replayOne(String url, String msg) {
        Request request = new Request.Builder().url(url)
                .headers(requestHeaders)
                .header("cookie", cookie)
                .build();
        try {
            Response response = new OkHttpClient.Builder().followRedirects(true)
                    .followSslRedirects(true).build().newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            if (document.body().toString().contains("目前处于见习期间")) {
                APPAplication.sendHandler(new Runnable() {
                    @Override
                    public void run() {
                        APPAplication.showDialog(context, "抱歉，您目前处于见习期间，需要等待 2 " +
                                "分钟后才能进行本操作");
                    }
                });
                return -1;
            }
            String formhash = document.select("input#formhash").attr("value");
            String posttime = document.select("input#posttime").attr("value");
            String noticeauthor = document.getElementsByAttributeValue("name", "noticeauthor").attr("value");
            String noticetrimstr = document.getElementsByAttributeValue("name", "noticetrimstr").attr("value");
            String noticeauthormsg = document.getElementsByAttributeValue("name", "noticeauthormsg").attr("value");
            String reppid = document.getElementsByAttributeValue("name", "reppid").attr("value");
            String reppost = document.getElementsByAttributeValue("name", "reppost").attr("value");
            String new_url = url.replaceAll("amp;", "") + "&replysubmit=yes&geoloc=&handlekey=postform&inajax=1";
            FormBody.Builder formBodyBuilder = new FormBody.Builder()
                    .add("formhash", formhash).add("posttime", posttime)
                    .add("noticeauthor", noticeauthor).add("noticetrimstr", noticetrimstr)
                    .add("noticeauthormsg", noticeauthormsg).add("reppid", reppid)
                    .add("reppost", reppost).add("message", msg);
            RequestBody requestBody = formBodyBuilder.build();
            request = new Request.Builder().url(new_url)
                    .headers(requestHeaders).header("cookie", cookie).post(requestBody)
                    .build();
            try {
                response = mOkHttpClient.newCall(request).execute();
                final String s = response.body().string();
                if (s.contains("回复发布成功")) {
                    return 1;
                } else if (s.contains("个字符的限制")) {
                    APPAplication.sendHandler(new Runnable() {
                        @Override
                        public void run() {
                            APPAplication.showDialog(context, "不能少于5个字符");
                        }
                    });
                } else {
                    APPAplication.sendHandler(new Runnable() {
                        @Override
                        public void run() {
                            APPAplication.showDialog(context, s);
                        }
                    });
                }
                return -1;
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public String my_favorite_url;
    public String my_thread_url;
    public String my_avator_url;
    public String info_url;
    public String user_exit_url;

    public int userExit() {
        Request request = new Request.Builder().url("http://www.myangs.com:81/" + user_exit_url)
                .headers(requestHeaders)
                .header("cookie", cookie)
                .build();
        try {
            Response response = new OkHttpClient.Builder().followRedirects(true)
                    .followSslRedirects(true).build().newCall(request).execute();
            String s = response.body().string();
            if (s.contains("已退出站点")) {
                APPAplication.bbs_login_status = false;
                APPAplication.save.edit().putString("bbs_user", "").putString("bbs_pwd", "")
                        .putString("bbs_cookie", "").apply();
                this.cookie = "";
                return 1;
            } else {
                return -1;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public Bitmap getDrawble(String url) {
        Bitmap bitmap = null;
        Request request = new Request.Builder().url(url).headers(requestHeaders)
                .header("cookie", cookie)
                .build();
        try {
            if (url.contains("http://www.myangs.com")) {
                Response response = new OkHttpClient.Builder().followRedirects(true)
                        .followSslRedirects(true).build().newCall(request).execute();
                Document document = Jsoup.parse(response.body().string());
                String url2 = document.select("img.postalbum_i").attr("orig");
                bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                request = new Request.Builder().url(url2).headers(requestHeaders)
                        .header("cookie", cookie)
                        .build();
                try {
                    response = new OkHttpClient.Builder().followRedirects(true)
                            .followSslRedirects(true).build().newCall(request).execute();
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {

                }
            } else {
                try {
                    Response response = new OkHttpClient.Builder().followRedirects(true)
                            .followSslRedirects(true).build().newCall(request).execute();
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            APPAplication.showToast("URL: " + url + "\n获取帖子图片失败: " + e.toString() + "\n请反馈!", 1);
        }
        return bitmap;
    }
}
