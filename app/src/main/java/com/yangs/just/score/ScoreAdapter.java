package com.yangs.just.score;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangs.just.R;

import java.util.List;

/**
 * Created by yangs on 2017/8/1.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private List<Score> list;
    private LayoutInflater inflater;

    public ScoreAdapter(List<Score> list, LayoutInflater inflater) {
        this.list = list;
        this.inflater = inflater;
    }

    public List<Score> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_listview, parent, false);
        ViewHolder view2 = new ViewHolder(view);
        return view2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.index.setText((position + 1) + "");
        holder.name.setText(list.get(position).getName());
        try {
            int s = Integer.parseInt(list.get(position).getScore());
            holder.score.setText(s);
            if (s >= 60) {
                holder.score.setTextColor(Color.GREEN);
            } else {
                holder.score.setTextColor(Color.YELLOW);
            }
        } catch (Exception e) {
            String s = list.get(position).getScore();
            holder.score.setText(s);
            if (s.equals("不及格") || s.equals("不通过")) {
                holder.score.setTextColor(Color.RED);
            } else {
                holder.score.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<Score> list) {
        int lastIndex = this.list.size();
        if (this.list.addAll(list)) {
            notifyItemRangeInserted(lastIndex, this.list.size());
        }
    }

    public void remove(int position) {
        this.list.remove(position);
        notifyItemRemoved(position);
        if (position != (list.size())) {
            notifyItemRangeChanged(position, this.list.size() - position);
        }
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView index;
        public TextView name;
        public TextView score;

        public ViewHolder(View view) {
            super(view);
            index = (TextView) view.findViewById(R.id.score_listview_tv_index);
            name = (TextView) view.findViewById(R.id.score_listview_tv_name);
            score = (TextView) view.findViewById(R.id.score_listview_tv_score);
        }
    }
}