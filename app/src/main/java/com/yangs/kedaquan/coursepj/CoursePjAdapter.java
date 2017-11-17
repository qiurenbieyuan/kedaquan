package com.yangs.kedaquan.coursepj;

import android.graphics.Color;
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

public class CoursePjAdapter extends RecyclerView.Adapter<CoursePjAdapter.ViewHolder> {
    private List<CoursePJList> list;
    private LayoutInflater inflater;

    public CoursePjAdapter(List<CoursePJList> list, LayoutInflater inflater) {
        this.list = list;
        this.inflater = inflater;
    }

    public List<CoursePJList> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursepj_listview, parent, false);
        ViewHolder view2 = new ViewHolder(view);
        return view2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.index.setText((position + 1) + "");
        holder.name.setText(list.get(position).getName());
        holder.teacher.setText(list.get(position).getTeacher());
        if (list.get(position).getHasPj()) {
            holder.hasPj.setText("已评");
            holder.score.setText(list.get(position).getScore());
            holder.hasPj.setTextColor(Color.GREEN);
            holder.score.setTextColor(Color.GREEN);
        } else {
            holder.hasPj.setText("未评");
            holder.score.setText(0 + "");
            holder.score.setTextColor(Color.RED);
            holder.hasPj.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<CoursePJList> list) {
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
        public TextView teacher;
        public TextView hasPj;
        public TextView score;

        public ViewHolder(View view) {
            super(view);
            index = (TextView) view.findViewById(R.id.coursepj_listview_index);
            name = (TextView) view.findViewById(R.id.coursepj_listview_name);
            teacher = (TextView) view.findViewById(R.id.coursepj_listview_teacher);
            hasPj = (TextView) view.findViewById(R.id.coursepj_listview_haspj);
            score = (TextView) view.findViewById(R.id.coursepj_listview_score);
        }
    }
}
