package com.yangs.kedaquan.activity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangs.kedaquan.R;

import java.util.List;

/**
 * Created by yangs on 2017/9/2 0002.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private List<Notice> list;

    public NoticeAdapter(List<Notice> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_title.setText(list.get(position).getTitle());
        holder.tv_time.setText(list.get(position).getTime());
        switch (list.get(position).getLevel()) {
            case 1:
                holder.tv_title.setTextColor(Color.BLACK);
                break;
            case 2:
                holder.tv_title.setTextColor(Color.RED);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<Notice> list) {
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
        public TextView tv_title;
        public TextView tv_time;

        public ViewHolder(View view) {
            super(view);
            tv_time = (TextView) view.findViewById(R.id.notice_listview_time);
            tv_title = (TextView) view.findViewById(R.id.notice_listview_title);
        }
    }
}
