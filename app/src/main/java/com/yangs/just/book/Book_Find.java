package com.yangs.just.book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.Browser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/5/10.
 */

public class Book_Find extends Activity implements View.OnClickListener, View.OnKeyListener, OnRefreshListener, OnItemClickListener, OnLoadMoreListener {
    private ImageView iv_back;
    private ImageView iv_find;
    private EditText et_text;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private LRecyclerView lRecyclerView;
    private BookFindAdapter bookFindAdapter;
    private List<Book2> list;
    private Handler handler;
    private BookFindSource bookFindSource;
    private View header_view;
    private TextView header_text;
    private Button bt_lend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_find);
        setHandler();
        iv_back = (ImageView) findViewById(R.id.book_find_ti_back);
        iv_find = (ImageView) findViewById(R.id.book_find_ti_find);
        et_text = (EditText) findViewById(R.id.book_find_ti_text);
        bt_lend = (Button) findViewById(R.id.book_find_bt_lend);
        bt_lend.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_find.setOnClickListener(this);
        et_text.setOnKeyListener(this);
        et_text.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        list = new ArrayList<Book2>();
        lRecyclerView = (LRecyclerView) findViewById(R.id.book_find_lr);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(Book_Find.this));
        bookFindAdapter = new BookFindAdapter(list, getLayoutInflater());
        lRecyclerViewAdapter = new LRecyclerViewAdapter(bookFindAdapter);
        header_view = LayoutInflater.from(this).inflate(R.layout.book_find_header_view,
                (ViewGroup) findViewById(android.R.id.content), false);
        header_text = (TextView) header_view.findViewById(R.id.book_find_header_tv);
        lRecyclerViewAdapter.addHeaderView(header_view);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewHint("拼命加载中", "只有这么多书啦", "网络不给力啊，点击再试一次吧");
        lRecyclerViewAdapter.setOnItemClickListener(Book_Find.this);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.setOnRefreshListener(this);
        lRecyclerView.setOnLoadMoreListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_find_ti_find:
                lRecyclerView.refresh();
                break;
            case R.id.book_find_ti_back:
                finish();
                break;
            case R.id.book_find_bt_lend:
                Intent intent = new Intent(Book_Find.this, Book_Lend.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private void initFind() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(Book_Find.this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        header_text.setText("江科大图书检索系统");
        if (TextUtils.isEmpty(et_text.getText().toString().trim())) {
            lRecyclerView.refreshComplete(10);
            lRecyclerViewAdapter.notifyDataSetChanged();
            APPAplication.showToast("请输入书名!", 0);
        } else {
            bt_lend.setVisibility(View.GONE);
            bookFindAdapter.clear();
            lRecyclerViewAdapter.notifyDataSetChanged();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bookFindSource = new BookFindSource();
                    list = bookFindSource.getList(et_text.getText().toString().trim());
                    if (list.size() > 0)
                        handler.sendEmptyMessage(1);
                    else
                        handler.sendEmptyMessage(2);
                }
            }).start();
        }
    }

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        header_text.setText("共找到" + bookFindSource.getNumber() + "本书");
                        bookFindAdapter.addAll(list);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        header_text.setText("图书馆暂时没有此书!");
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 3:
                        bookFindAdapter.addAll(list);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 4:
                        lRecyclerView.setNoMore(true);
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onRefresh() {
        initFind();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            lRecyclerView.refresh();
            return true;
        }

        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(Book_Find.this, Browser.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://lib.just.edu.cn:8080/opac/" + bookFindAdapter.getList().get(position).getUrl());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = bookFindSource.getList2();
                if (list.size() > 0) {
                    handler.sendEmptyMessage(3);
                } else {
                    handler.sendEmptyMessage(4);
                }

            }
        }).start();
    }

}
