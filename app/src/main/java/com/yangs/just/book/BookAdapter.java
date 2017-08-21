package com.yangs.just.book;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yangs.just.activity.APPAplication;
import com.yangs.just.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/4/22.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private ArrayList<Book> list;

    public BookAdapter(ArrayList<Book> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getBook_name());
        holder.number.setText("编号: " + list.get(position).getNumber());
        holder.date_start.setText("借: " + list.get(position).getDate_start());
        holder.date_end.setText("还: " + list.get(position).getDate_end());
        holder.xj.setText("续借次数: " + list.get(position).getXj());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<Book> list) {
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
        public TextView name;
        public TextView number;
        public TextView date_start;
        public TextView date_end;
        public TextView xj;

        public ViewHolder(View view) {
            super(view);
            number = (TextView) view.findViewById(R.id.book_listview_num);
            date_start = (TextView) view.findViewById(R.id.book_listview_start);
            date_end = (TextView) view.findViewById(R.id.book_listview_end);
            xj = (TextView) view.findViewById(R.id.book_listview_xj);
            name = (TextView) view.findViewById(R.id.book_listview_name);
        }
    }
}
