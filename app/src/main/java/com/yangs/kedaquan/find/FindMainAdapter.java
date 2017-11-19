package com.yangs.kedaquan.find;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangs.kedaquan.R;

import java.util.List;

/**
 * Created by yangs on 2017/7/30.
 */

public class FindMainAdapter extends RecyclerView.Adapter<FindMainAdapter.ViewHolder> {
    private List<String> list;

    public FindMainAdapter(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        public ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item_tv);
        }
    }
}