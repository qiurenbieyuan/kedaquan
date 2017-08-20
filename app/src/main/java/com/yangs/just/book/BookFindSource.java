package com.yangs.just.book;

import android.text.TextUtils;
import android.widget.Toast;

import com.yangs.just.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by winutalk on 2017/5/10.
 */

public class BookFindSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private List<Book2> list;
    private int number;
    private int page;
    private int index;
    private String text;

    public BookFindSource() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build();
        index = 1;
        list = new ArrayList<Book2>();
    }

    public int getNumber() {
        return number;
    }

    public List<Book2> getList(String text) {
        this.text = text;
        Request request = new Request.Builder()
                .url("http://lib.just.edu.cn:8080/opac/openlink.php?strSearchType=title&match_flag=forward&historyCount=1&strText=" + text + "&doctype=ALL&displaypg=20&showmode=list&sort=CATA_DATE&orderby=desc&location=ALL")
                .headers(requestHeaders).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
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
        Request request = new Request.Builder()
                .url("http://lib.just.edu.cn:8080/opac/openlink.php?location=ALL&title=" + text + "&doctype=ALL&lang_code=ALL&match_flag=forward&displaypg=20&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&count=267&with_ebook=off&page=" + index)
                .headers(requestHeaders).build();
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
