package com.yangs.just.bbs;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangs.just.R;

import java.util.List;

/**
 * Created by winutalk on 2017/8/2.
 */

public class BBSAdapter extends RecyclerView.Adapter<BBSAdapter.ViewHolder> {
    private List<BBS> list;
    private ViewHolder holder;

    public BBSAdapter(List<BBS> list, Activity activity) {
        this.list = list;
    }

    @Override
    public BBSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bbs_listview, parent, false);
        return new ViewHolder(view);
    }

    public List<BBS> getList() {
        return list;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        this.holder = holder;
        holder.title.setText(Html.fromHtml(list.get(position).getTitle()));
        holder.user.setText(list.get(position).getUser());
        holder.num.setText(list.get(position).getNum());
        holder.title.setTextColor(list.get(position).getColor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<BBS> list) {
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
        public TextView title;
        public TextView user;
        public TextView num;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.bbs_listview_title);
            user = (TextView) view.findViewById(R.id.bbs_listview_user);
            num = (TextView) view.findViewById(R.id.bbs_listview_num);
        }
    }
}
