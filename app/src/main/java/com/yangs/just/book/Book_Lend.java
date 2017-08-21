package com.yangs.just.book;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.R;
import com.yangs.just.activity.TitleBuilder;

import java.util.ArrayList;

/**
 * Created by yangs on 2017/4/22.
 */

public class Book_Lend extends Activity implements OnRefreshListener {

    private TitleBuilder titleBuilder;
    private ArrayList<Book> book_list;
    private LRecyclerView book_lend_lr;
    private LRecyclerViewAdapter book_lend_lr_adapter;
    private BookAdapter bookAdapter;
    private String user;
    private String pwd;
    private Bitmap code_bitmap;
    private Handler handler;
    private Dialog dialog;
    private BookSource bookSource;
    private ImageView dia_code;
    private int type;
    private String type_tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_lend);
        titleBuilder = new TitleBuilder(Book_Lend.this);
        titleBuilder.setLeftImage(R.drawable.ic_arraw_back_white).setTitleText("我的借阅", Boolean.FALSE).setTvTitleNoClick().setIvRightNoClick()
                .setRightText("刷新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        book_lend_lr.refresh();
                    }
                });
        titleBuilder.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHandler();
        user = APPAplication.save.getString("book_name", "");
        pwd = APPAplication.save.getString("book_pwd", "");
        book_lend_lr = (LRecyclerView) findViewById(R.id.book_lend_lr);
        book_lend_lr.setLayoutManager(new LinearLayoutManager(Book_Lend.this));
        book_list = new ArrayList<Book>();
        bookAdapter = new BookAdapter(book_list);
        book_lend_lr_adapter = new LRecyclerViewAdapter(bookAdapter);
        book_lend_lr_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                new AlertDialog.Builder(Book_Lend.this).setTitle("提示").setMessage("是否需要续借此书?")
                        .setPositiveButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        APPAplication.showToast("正在续借 [ " + book_list.get(position).getBook_name() + " ] ...", 0);
                    }
                }).create().show();
            }
        });
        book_lend_lr.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        book_lend_lr.setAdapter(book_lend_lr_adapter);
        book_lend_lr.setHasFixedSize(true);
        book_lend_lr.setOnRefreshListener(this);
        bookSource = new BookSource();
        if (TextUtils.isEmpty(user)) {
            new AlertDialog.Builder(Book_Lend.this).setTitle("提示").setCancelable(false)
                    .setMessage("默认密码有3种情况\n1.学号\n2.借书证上的条形码号\n3.身份证后6位")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            book_lend_lr.refresh();
                        }
                    }).create().show();
        } else {
            SQLiteDatabase db = APPAplication.db;
            String sql = "select * from book";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    String book_name = cursor.getString(1);
                    String book_num = cursor.getString(2);
                    String start = cursor.getString(3);
                    String end = cursor.getString(4);
                    int xj = cursor.getInt(5);
                    Book book = new Book(book_name, book_num, start, end, xj + "");
                    book_list.add(book);
                } while (cursor.moveToNext());
            }
            book_lend_lr.refreshComplete(10);
            book_lend_lr_adapter.notifyDataSetChanged();
        }
    }

    public void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        book_lend_lr.refreshComplete(10);
                        book_lend_lr_adapter.notifyDataSetChanged();
                        new AlertDialog.Builder(Book_Lend.this).setTitle("提示")
                                .setMessage("登录失败!\n默认密码有3种情况\n1.学号\n2.借书证上的条形码号\n3.身份证后6位")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                        break;
                    case 2:
                        type = R.id.book_login_type_1;
                        View view = LayoutInflater.from(Book_Lend.this).inflate(R.layout.book_login_dialog, null);
                        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.book_login_type);
                        type_tmp = APPAplication.save.getString("book_login_type", "cert_no");
                        switch (type_tmp) {
                            case "cert_no":
                                radioGroup.check(R.id.book_login_type_1);
                                break;
                            case "bar_no":
                                radioGroup.check(R.id.book_login_type_2);
                                break;
                            case "email":
                                radioGroup.check(R.id.book_login_type_3);
                                break;
                        }
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                                type = group.getCheckedRadioButtonId();
                            }
                        });
                        final EditText dia_user = (EditText) view.findViewById(R.id.book_login_name);
                        final EditText dia_pwd = (EditText) view.findViewById(R.id.book_login_pwd);
                        final EditText dia_incode = (EditText) view.findViewById(R.id.book_login_incode);
                        dia_code = (ImageView) view.findViewById(R.id.book_login_code);
                        dia_user.setText(user);
                        dia_pwd.setText(pwd);
                        if (code_bitmap != null)
                            dia_code.setImageBitmap(code_bitmap);
                        dialog = new AlertDialog.Builder(Book_Lend.this)
                                .setTitle("登录").setView(view).setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        user = dia_user.getText().toString();
                                        pwd = dia_pwd.getText().toString();
                                        dialog.dismiss();
                                        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd)) {
                                            APPAplication.showToast("账号或密码为空!", 0);
                                            book_lend_lr.refreshComplete(10);
                                            book_lend_lr_adapter.notifyDataSetChanged();
                                        } else {
                                            switch (type) {
                                                case R.id.book_login_type_1:
                                                    type_tmp = "cert_no";
                                                    break;
                                                case R.id.book_login_type_2:
                                                    type_tmp = "bar_no";
                                                    break;
                                                case R.id.book_login_type_3:
                                                    type_tmp = "email";
                                                    break;
                                            }
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    book_list = bookSource.getList(user, pwd, dia_incode.getText().toString(), type_tmp);
                                                    switch (book_list.get(0).getStatus()) {
                                                        case 0:
                                                            handler.sendEmptyMessage(3);
                                                            break;
                                                        case -1:
                                                            handler.sendEmptyMessage(5);
                                                            break;
                                                        case -2:
                                                            handler.sendEmptyMessage(1);
                                                            break;
                                                        case -3:
                                                            handler.sendEmptyMessage(6);
                                                            break;
                                                    }
                                                }
                                            }).start();
                                        }
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        book_lend_lr.refreshComplete(10);
                                        book_lend_lr_adapter.notifyDataSetChanged();
                                    }
                                }).create();
                        dialog.show();
                        break;
                    case 3:
                        APPAplication.save.edit().putString("book_name", user)
                                .putString("book_pwd", pwd)
                                .putString("book_login_type", type_tmp).apply();
                        bookAdapter.clear();
                        bookAdapter.addAll(book_list);
                        book_lend_lr.refreshComplete(10);
                        book_lend_lr_adapter.notifyDataSetChanged();
                        break;
                    case 4:
                        dia_code.setImageBitmap(code_bitmap);
                        break;
                    case 5:
                        bookAdapter.clear();
                        book_lend_lr.refreshComplete(10);
                        book_lend_lr_adapter.notifyDataSetChanged();
                        new AlertDialog.Builder(Book_Lend.this).setTitle("提示").setMessage("您当前没有借书!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                        break;
                    case 6:
                        APPAplication.showToast("网络错误", 0);
                        book_lend_lr.refreshComplete(10);
                        book_lend_lr_adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    @Override
    public void onRefresh() {
        bookAdapter.clear();
        book_lend_lr_adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                code_bitmap = bookSource.getCode();
                handler.sendEmptyMessage(2);
            }
        }).start();
    }
}
