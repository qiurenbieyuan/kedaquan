package com.yangs.just.book;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yangs.just.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yangs on 2017/5/10.
 */

public class BookFindSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private List<Book2> list;
    private int number;
    private int page;
    private int index;
    private String text;
    private Boolean isVPN = true;
    private String cookie;

    public BookFindSource() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                .build();
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .followRedirects(false).followSslRedirects(false).build();
        index = 1;
        list = new ArrayList<Book2>();
    }

    public int getNumber() {
        return number;
    }

    public List<Book2> getList(String text) {
        Log.i("TAG", "start");
        this.text = text;
        String url;
        if (isVPN) {
            url = "https://vpn.just.edu.cn/opac/,DanaInfo=lib.just.edu.cn,Port=8080+";
        } else {
            url = "http://lib.just.edu.cn:8080/opac/";
        }
        if (cookie == null)
            cookie = APPAplication.save.getString("vpn_cookie", "");
        Log.i("TAG", cookie);
        Request request = new Request.Builder()
                .url(url + "openlink.php?strSearchType=title&match_flag=forward&historyCount=1&strText=" + text + "&doctype=ALL&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&location=ALL")
                .headers(requestHeaders).header("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            String s = response.body().string();
            Log.i("TAG", s);
            Document document = Jsoup.parse(s);
            try {
                number = Integer.parseInt(document.getElementsByAttributeValue("class", "red").get(0).text());
            } catch (Exception e) {
                number = 0;
            }
            if (number > 20)
                page = Integer.parseInt(document.getElementsByAttributeValue("color", "black").get(0).text());
            else
                page = 1;
            Elements elements = document.getElementsByClass("book_list_info");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                List<TextNode> list2 = element.select("p").first().textNodes();
                Book2 book2 = new Book2();
                book2.setBook_name(element.select("a").first().text());
                book2.setTotal(element.select("span").get(1).text());
                book2.setUrl(element.select("a").first().attr("href"));
                book2.setAuthor(list2.get(1).text());
                book2.setPublish(list2.get(2).text());
                list.add(book2);
            }
        } catch (Exception e) {
            APPAplication.showToast("连接图书馆服务器失败,请稍后再试", 1);
        }
        return list;
    }

    public List<Book2> getList2() {
        list = new ArrayList<Book2>();
        if (index == page)
            return list;
        index++;
        String url;
        if (isVPN) {
            url = "https://vpn.just.edu.cn/opac/,DanaInfo=lib.just.edu.cn,Port=8080+";
        } else {
            url = "http://lib.just.edu.cn:8080/opac/";
        }
        Request request = new Request.Builder()
                .url(url + "openlink.php?location=ALL&title=" + text + "&doctype=ALL&lang_code=ALL&match_flag=forward&displaypg=20&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&count=267&with_ebook=off&page=" + index)
                .headers(requestHeaders).header("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Elements elements = document.getElementsByClass("book_list_info");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                List<TextNode> list2 = element.select("p").first().textNodes();
                Book2 book2 = new Book2();
                book2.setBook_name(element.select("a").first().text());
                book2.setTotal(element.select("span").get(1).text());
                book2.setUrl(element.select("a").first().attr("href"));
                book2.setAuthor(list2.get(1).text());
                book2.setPublish(list2.get(2).text());
                list.add(book2);
            }
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        }
        return list;
    }
}
