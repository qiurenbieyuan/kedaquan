package com.yangs.just.bbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.Browser;

import java.util.List;

/**
 * Created by winutalk on 2017/8/3.
 */

public class BBSDetailAdapter extends RecyclerView.Adapter<BBSDetailAdapter.ViewHolder> {
    private List<BBSDetail> list;
    private Context context;


    public BBSDetailAdapter(List<BBSDetail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bbsdetail_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bbsdetail_listview_iv.setImageURI(list.get(position).getAvatar());
        holder.user.setText(list.get(position).getUser());
        holder.time.setText(list.get(position).getTime());
        holder.index.setText(list.get(position).getIndex());
        holder.content.setText(Html.fromHtml(list.get(position).getContent()));
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Browser.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.myangs.com:81/" + list.get(position).getUser_url());
                bundle.putString("cookie", APPAplication.bbsSource.cookie);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.bbsdetail_listview_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Browser.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.myangs.com:81/" + list.get(position).getUser_url());
                bundle.putString("cookie", APPAplication.bbsSource.cookie);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public List<BBSDetail> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<BBSDetail> list) {
        int lastIndex = this.list.size();
        if (this.list.addAll(list)) {
            notifyItemRangeInserted(lastIndex, this.list.size());
        }
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView time;
        private TextView index;
        private TextView content;
        public SimpleDraweeView bbsdetail_listview_iv;

        public ViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.bbsdetail_listview_user);
            index = (TextView) view.findViewById(R.id.bbsdetail_listview_index);
            time = (TextView) view.findViewById(R.id.bbsdetail_listview_time);
            content = (TextView) view.findViewById(R.id.bbsdetail_listview_content);
            bbsdetail_listview_iv = (SimpleDraweeView) view.findViewById(R.id.bbsdetail_listview_iv);
        }
    }
}
