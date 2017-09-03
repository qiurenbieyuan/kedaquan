package com.yangs.just.bbs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.Browser;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by yangs on 2017/8/29 0029.
 */

public class BBSReplyAdapter extends RecyclerView.Adapter<BBSReplyAdapter.ViewHolder> {
    private List<BBSReply> list;
    private Context context;

    public BBSReplyAdapter(List<BBSReply> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bbsreply_listview, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.iv.setImageURI(list.get(position).getAvatar());
        holder.tv_time.setText(list.get(position).getTime());
        holder.tv_user.setText(list.get(position).getUser());
        holder.tv_title.setText(list.get(position).getTitle());
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Browser.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", list.get(position).getUserUrl());
                bundle.putString("cookie", APPAplication.bbsSource.cookie);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<BBSReply> list) {
        int lastIndex = this.list.size();
        if (this.list.addAll(list)) {
            notifyItemRangeInserted(lastIndex, this.list.size());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView iv;
        public TextView tv_time;
        public TextView tv_user;
        public TextView tv_title;

        public ViewHolder(View itemView) {
            super(itemView);
            iv = (SimpleDraweeView) itemView.findViewById(R.id.bbsreply_listview_iv);
            tv_time = (TextView) itemView.findViewById(R.id.bbsreply_listview_tv_time);
            tv_user = (TextView) itemView.findViewById(R.id.bbsreply_listview_tv_user);
            tv_title = (TextView) itemView.findViewById(R.id.bbsreply_listview_tv_title);
        }
    }
}
