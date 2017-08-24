package com.yangs.just.activity;
/**
 * Created by yangs on 2017/2/14.
 */

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yangs.just.R;

public class TitleBuilder {
    private View viewTitle;
    private TextView tvTitle;
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvLeft;
    private TextView tvRight;

    public TitleBuilder(Activity context) {
        viewTitle = context.findViewById(R.id.rl_titlebar);
        tvTitle = (TextView) viewTitle.findViewById(R.id.titlebar_tv);
        ivLeft = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_left);
        ivRight = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_right);
        tvLeft = (TextView) viewTitle.findViewById(R.id.titlebar_tv_left);
        tvRight = (TextView) viewTitle.findViewById(R.id.titlebar_tv_right);
    }

    public TitleBuilder(View context) {
        viewTitle = context.findViewById(R.id.rl_titlebar);
        tvTitle = (TextView) viewTitle.findViewById(R.id.titlebar_tv);
        ivLeft = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_left);
        ivRight = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_right);
        tvLeft = (TextView) viewTitle.findViewById(R.id.titlebar_tv_left);
        tvRight = (TextView) viewTitle.findViewById(R.id.titlebar_tv_right);
    }

    // title

    public TitleBuilder setTitleBgRes(int resid) {
        viewTitle.setBackgroundResource(resid);
        return this;
    }

    public TitleBuilder setTitleText(String text, Boolean flag) {
        tvTitle.setVisibility(TextUtils.isEmpty(text) ? View.GONE
                : View.VISIBLE);
        tvTitle.setText(text);
        if (flag == Boolean.FALSE) {
            tvTitle.setClickable(Boolean.FALSE);
        } else {
            tvTitle.setClickable(Boolean.TRUE);
        }
        return this;
    }

    // left

    public TitleBuilder setLeftImage(int resId) {
        ivLeft.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        ivLeft.setImageResource(resId);
        return this;
    }

    public TitleBuilder setLeftText(String text, Boolean flag) {
        tvLeft.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tvLeft.setVisibility(flag ? View.VISIBLE : View.GONE);
        tvLeft.setText(text);
        return this;
    }

    public TitleBuilder setLeftOnClickListener(View.OnClickListener listener) {
        if (ivLeft.getVisibility() == View.VISIBLE) {
            ivLeft.setOnClickListener(listener);
        } else if (tvLeft.getVisibility() == View.VISIBLE) {
            tvLeft.setOnClickListener(listener);
        }
        return this;
    }

    // right

    public TitleBuilder setRightImage(int resId) {
        if(resId>0){
            ivRight.setVisibility(View.VISIBLE);
            ivRight.setImageResource(resId);
        }else{
            ivRight.setVisibility(View.GONE);
        }

        return this;
    }

    public TitleBuilder setRightText(String text,View.OnClickListener listener) {
        tvRight.setVisibility(TextUtils.isEmpty(text) ? View.GONE
                : View.VISIBLE);
        tvRight.setOnClickListener(listener);
        tvRight.setText(text);
        return this;
    }

    public TitleBuilder setRightOnClickListener(View.OnClickListener listener) {
        ivRight.setOnClickListener(listener);
        return this;
    }

    public View build() {
        return viewTitle;
    }

    public TitleBuilder setTvTitleNoClick() {
        tvTitle.setClickable(Boolean.FALSE);
        return this;
    }
    public TitleBuilder setIvRightNoClick(){
        ivRight.setClickable(false);
        return this;
    }
}