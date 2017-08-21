package com.yangs.just;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.yangs.just.activity.MainActivity;

/**
 * Created by yangs on 2017/2/27.
 */

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(Splash.this, MainActivity.class));
        finish();
    }
}
