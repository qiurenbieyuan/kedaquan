package com.yangs.kedaquan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.yangs.kedaquan.R;

/**
 * Created by yangs on 2017/9/1 0001.
 */

public class KebiaoDialogActivity extends Activity {
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kebiaodialog_layout);
        textView = findViewById(R.id.kebiadialog_tv);
        Bundle bundle = getIntent().getExtras();
        textView.setText(bundle.getString("课程名") + "\n" +
                bundle.getString("老师") + "\n" +
                bundle.getString("教室"));
    }
}
