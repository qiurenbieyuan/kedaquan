package com.yangs.just;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.yangs.just.activity.APPAplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yangs on 2017/5/17 0017.
 */

public class AppWidget extends AppWidgetProvider {

    private final String ACTION_BUTTON = "Kedaquan_Widget_Update";

    /**
     * 接受广播事件
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent == null)
            return;
        if (intent.getAction().equals(ACTION_BUTTON)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, AppWidget.class);
            appWidgetManager.updateAppWidget(componentName, refreshKebiao(context));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetManager.updateAppWidget(appWidgetIds, refreshKebiao(context));
    }

    /**
     * 删除AppWidget
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * AppWidget首次创建调用
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    public RemoteViews refreshKebiao(Context context) {
        int week = APPAplication.week;
        List<Integer> single_list = new ArrayList<Integer>();
        single_list.add(R.id.widget_1);
        single_list.add(R.id.widget_2);
        single_list.add(R.id.widget_3);
        single_list.add(R.id.widget_4);
        single_list.add(R.id.widget_5);
        single_list.add(R.id.widget_6);
        single_list.add(R.id.widget_7);
        List<Integer> myImageList = new ArrayList<>();
        myImageList.add(R.drawable.textview_border_bohelv);
        myImageList.add(R.drawable.textview_border_cheng);
        myImageList.add(R.drawable.textview_border_lan);
        myImageList.add(R.drawable.textview_border_huang);
        myImageList.add(R.drawable.textview_border_lv);
        myImageList.add(R.drawable.textview_border_pulan);
        myImageList.add(R.drawable.textview_border_fen);
        myImageList.add(R.drawable.textview_border_cyan);
        myImageList.add(R.drawable.textview_border_zi);
        myImageList.add(R.drawable.textview_kafei);
        myImageList.add(R.drawable.texivew_border_qing);
        myImageList.add(R.drawable.textview_border_molan);
        myImageList.add(R.drawable.textview_border_tuhuang);
        myImageList.add(R.drawable.textview_border_tao);
        List<Integer> single_index = new ArrayList<Integer>();
        single_index.add(R.id.widget_single_1);
        single_index.add(R.id.widget_single_2);
        single_index.add(R.id.widget_single_3);
        single_index.add(R.id.widget_single_4);
        single_index.add(R.id.widget_single_5);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
        remoteViews.setTextViewText(R.id.widget_week, "第" + week + "周");
        for (int i = 1; i <= 7; i++) {
            RemoteViews nestedView = new RemoteViews(context.getPackageName(), R.layout.widget_single_layout);
            for (int j = 1; j <= 5; j++) {
                String sql = "select * from course where 星期=" + i + " and 节次=" + j + ";";
                Cursor cursor = null;
                try {
                    cursor = APPAplication.db.rawQuery(sql, null);
                    if (cursor.getCount() > 0) {
                        if (cursor.moveToFirst()) {
                            do {
                                String[] t5 = cursor.getString(7).replaceAll("\\(单周\\)|\\(周\\)|\\(双周\\)", "").split(",");
                                boolean flag = false;
                                try {
                                    for (int n = 0; n < t5.length; n++) {
                                        String[] t6 = t5[n].split("-");
                                        int start, end;
                                        start = Integer.parseInt(t6[0]);
                                        try {
                                            end = Integer.parseInt(t6[1]);
                                        } catch (Exception e) {
                                            end = start;
                                        }
                                        if (start <= week && week <= end) {
                                            flag = true;
                                            break;
                                        } else {
                                            if (n == t5.length - 1) {
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {

                                }
                                if (flag) {
                                    nestedView.setInt(single_index.get(j - 1), "setBackgroundResource", myImageList.get(Integer.parseInt(cursor.getString(8))));
                                } else {
                                    nestedView.setInt(single_index.get(j - 1), "setBackgroundResource", R.drawable.textview_border_hui);
                                    nestedView.setTextColor(single_index.get(j - 1), Color.rgb(167, 174, 174));
                                }
                                if (!TextUtils.isEmpty(cursor.getString(3).trim()))
                                    nestedView.setTextViewText(single_index.get(j - 1), cursor.getString(1) + "@" + cursor.getString(3));
                                else
                                    nestedView.setTextViewText(single_index.get(j - 1), cursor.getString(1));
                            } while (cursor.moveToNext());
                        }
                    } else {
                        nestedView.setTextViewText(single_index.get(j - 1), "");
                    }
                } catch (Exception e) {
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
            remoteViews.addView(single_list.get(i - 1), nestedView);
        }
        return remoteViews;
    }
}
