package com.yangs.kedaquan;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.yangs.kedaquan.activity.APPAplication;

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
                        List<HashMap<String, String>> course_info = new ArrayList<>();
                        if (cursor.moveToFirst()) {
                            do {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("课程代码", cursor.getString(cursor.getColumnIndex("课程代码")));
                                map.put("课程名", cursor.getString(cursor.getColumnIndex("课程名")));
                                map.put("老师", cursor.getString(cursor.getColumnIndex("老师")));
                                map.put("教室", cursor.getString(cursor.getColumnIndex("教室")));
                                map.put("节次", i + " " + j);
                                map.put("颜色代码", cursor.getString(cursor.getColumnIndex("颜色代码")));
                                map.put("周次", cursor.getString(cursor.getColumnIndex("周次")));
                                map.put("index", cursor.getInt(0) + "");
                                course_info.add(map);
                            } while (cursor.moveToNext());
                        }
                        boolean flag = false;
                        for (int m = 0; m < course_info.size(); m++) {
                            try {
                                String[] t5 = course_info.get(m).get("周次")
                                        .replaceAll("\\(单周\\)|\\(周\\)|\\(双周\\)", "").split(",");
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
                                APPAplication.showToast(course_info.get(m).get("课程名") + " 的周次有语法问题，请修改!", 1);
                            }
                            if (flag) {
                                if (!TextUtils.isEmpty(course_info.get(m).get("教室").trim()))
                                    nestedView.setTextViewText(single_index.get(j - 1),
                                            course_info.get(m).get("课程名") + "@" + course_info.get(m).get("教室"));
                                else
                                    nestedView.setTextViewText(single_index.get(j - 1), course_info.get(m).get("课程名"));
                                nestedView.setInt(single_index.get(j - 1), "setBackgroundResource",
                                        myImageList.get(Integer.parseInt(course_info.get(m).get("颜色代码"))));
//                                Intent intent = new Intent();
//                                intent.setClass(context, KebiaoDialogActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//                                Bundle bundle = new Bundle();
//                                bundle.putString("课程名", course_info.get(m).get("课程名"));
//                                bundle.putString("老师", course_info.get(m).get("老师"));
//                                bundle.putString("教室", course_info.get(m).get("教室"));
//                                intent.putExtras(bundle);
//                                PendingIntent pi = PendingIntent.getActivity
//                                        (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                nestedView.setOnClickPendingIntent(single_index.get(j - 1), pi);
                                break;
                            } else {
                                if (m == course_info.size() - 1) {
                                    nestedView.setInt(single_index.get(j - 1), "setBackgroundResource", R.drawable.textview_border_hui);
                                    nestedView.setTextColor(single_index.get(j - 1), Color.rgb(167, 174, 174));
                                    if (!TextUtils.isEmpty(course_info.get(0).get("教室").trim()))
                                        nestedView.setTextViewText(single_index.get(j - 1),
                                                course_info.get(0).get("课程名") + "@" + course_info.get(0).get("教室"));
                                    else
                                        nestedView.setTextViewText(single_index.get(j - 1), course_info.get(0).get("课程名"));
                                }
                            }
                        }
                    } else {
                        nestedView.setTextViewText(single_index.get(j - 1), "");
                    }
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
