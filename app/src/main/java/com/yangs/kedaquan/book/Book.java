package com.yangs.kedaquan.book;

import java.io.Serializable;

/**
 * Created by yangs on 2017/4/22.
 */

public class Book implements Serializable {
    private String book_name;
    private String number;
    private String date_start;
    private String date_end;
    private String xj;
    private int status;

    public Book(String book_name, String number, String date_start, String date_end, String xj) {
        status = 0;
        this.book_name = book_name;
        this.number = number;
        this.date_start = date_start;
        this.date_end = date_end;
        this.xj = xj;
    }

    public Book(int x) {
        status = x;
    }

    public int getStatus() {
        return status;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getXj() {
        return xj;
    }

    public void setXj(String xj) {
        this.xj = xj;
    }
}
