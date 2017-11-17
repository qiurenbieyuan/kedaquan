package com.yangs.kedaquan.book;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangs.kedaquan.R;

import java.util.List;

/**
 * Created by yangs on 2017/5/10.
 */

public class BookFindAdapter extends RecyclerView.Adapter<BookFindAdapter.ViewHolder> {
    private List<Book2> list;
    private LayoutInflater inflater;

    public BookFindAdapter(List<Book2> list, LayoutInflater inflater) {
        this.list = list;
        this.inflater = inflater;
    }

    public List<Book2> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_find_listview, parent, false);
        ViewHolder view2 = new ViewHolder(view);
        return view2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.book_name.setText(list.get(position).getBook_name());
        holder.author.setText(list.get(position).getAuthor());
        holder.publish.setText(list.get(position).getPublish());
        holder.total.setText(list.get(position).getTotal());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<Book2> list) {
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
        public TextView book_name;
        public TextView author;
        public TextView publish;
        public TextView total;

        public ViewHolder(View view) {
            super(view);
            book_name = (TextView) view.findViewById(R.id.book_find_listview_bookname);
            author = (TextView) view.findViewById(R.id.book_find_listview_author);
            publish = (TextView) view.findViewById(R.id.book_find_listview_publish);
            total = (TextView) view.findViewById(R.id.book_find_listview_total);
        }
    }
}
