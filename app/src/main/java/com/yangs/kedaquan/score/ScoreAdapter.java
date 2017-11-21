package com.yangs.kedaquan.score;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yangs.kedaquan.R;

import java.util.List;

/**
 * Created by yangs on 2017/8/1.
 */

public class ScoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Score> list;
    private OnItemOnClickListener onItemOnClickListener;
    private OnDeatilItemClickListener onDeatilItemClickListener;

    public ScoreAdapter(List<Score> list) {
        this.list = list;
    }

    public List<Score> getList() {
        return list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.score_header_view, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemOnClickListener != null)
                        onItemOnClickListener.onItemClick(0);
                }
            });
            return new HeadViewHolder(view);
        } else {
            View view = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.score_listview, parent, false);
            ScoreViewHolder scoreViewHolder = new ScoreViewHolder(view);
            scoreViewHolder.score_ll_first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemOnClickListener != null)
                        onItemOnClickListener.onItemClick((int) v.getTag());
                }
            });
            scoreViewHolder.score_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeatilItemClickListener != null)
                        onDeatilItemClickListener.onDetailItemClickListener((int) v.getTag());
                }
            });
            return scoreViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            if (position == 0) {
                HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                headViewHolder.tv_jd.setText("绩点: " + list.get(0).getJd());
                headViewHolder.tv_term.setText(list.get(0).getTerm());
            } else {
                ScoreViewHolder scoreViewHolder = (ScoreViewHolder) holder;
                scoreViewHolder.score_ll_first.setTag(position);
                scoreViewHolder.score_ll.setTag(position);
                scoreViewHolder.index.setText(position + "");
                scoreViewHolder.name.setText(list.get(position).getName());
                try {
                    int s = Integer.parseInt(list.get(position).getScore());
                    scoreViewHolder.score.setText(s + "");
                    if (s >= 60) {
                        scoreViewHolder.score.setTextColor(Color.GRAY);
                    } else {
                        scoreViewHolder.score.setTextColor(Color.RED);
                    }
                } catch (NumberFormatException e) {
                    String s = list.get(position).getScore();
                    scoreViewHolder.score.setText(s);
                    if (s.equals("不及格") || s.equals("不通过")) {
                        scoreViewHolder.score.setTextColor(Color.RED);
                    } else {
                        scoreViewHolder.score.setTextColor(Color.GRAY);
                    }
                }
                if (list.get(position).getCBVisil()) {
                    scoreViewHolder.cb.setVisibility(View.VISIBLE);
                    scoreViewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            list.get(position).setCheck(isChecked);
                        }
                    });
                    if (list.get(position).getCheck())
                        scoreViewHolder.cb.setChecked(true);
                    else
                        scoreViewHolder.cb.setChecked(false);
                } else
                    scoreViewHolder.cb.setVisibility(View.GONE);
            }
        } else {
            if (!(holder instanceof ScoreViewHolder))
                return;
            ScoreViewHolder scoreViewHolder = (ScoreViewHolder) holder;
            int code = (int) payloads.get(0);
            if (code == 1) {
                if (list.get(position).getClick()) {
                    scoreViewHolder.score_ll.setVisibility(View.VISIBLE);
                    scoreViewHolder.ll_cno.setText(list.get(position).getCno() + "");
                    scoreViewHolder.ll_ks.setText(list.get(position).getKs() + "");
                    scoreViewHolder.ll_xf.setText(list.get(position).getXf() + "");
                    scoreViewHolder.ll_kcsx.setText(list.get(position).getKcsx() + "");
                    scoreViewHolder.ll_khfs.setText(list.get(position).getKhfx() + "");
                } else {
                    scoreViewHolder.score_ll.setVisibility(View.GONE);
                }
            } else if (code == 2) {
                scoreViewHolder.ll_xf.setText(list.get(position).getXf() + "");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 1;
        else
            return 2;
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

    public void setOnItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.onItemOnClickListener = onItemOnClickListener;
    }

    public void setOnDeatilItemClickListener(OnDeatilItemClickListener onDeatilItemClickListener) {
        this.onDeatilItemClickListener = onDeatilItemClickListener;
    }

    public interface OnItemOnClickListener {
        void onItemClick(int position);
    }

    public interface OnDeatilItemClickListener {
        void onDetailItemClickListener(int position);
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        TextView tv_jd;
        TextView tv_term;
        ProgressBar pb;

        HeadViewHolder(View view) {
            super(view);
            tv_jd = view.findViewById(R.id.score_header_tv_jd);
            tv_term = view.findViewById(R.id.score_header_tv_term);
            pb = view.findViewById(R.id.score_header_pb);
        }
    }

    public class ScoreViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;
        TextView index;
        TextView name;
        TextView score;
        LinearLayout score_ll;
        LinearLayout score_ll_first;
        TextView ll_cno;
        TextView ll_xf;
        TextView ll_ks;
        TextView ll_khfs;
        TextView ll_kcsx;

        public ScoreViewHolder(View view) {
            super(view);
            cb = view.findViewById(R.id.score_listview_cb);
            index = view.findViewById(R.id.score_listview_tv_index);
            name = view.findViewById(R.id.score_listview_tv_name);
            score = view.findViewById(R.id.score_listview_tv_score);
            score_ll = view.findViewById(R.id.score_listview_ll);
            score_ll_first = view.findViewById(R.id.score_listview_ll_first);
            ll_cno = view.findViewById(R.id.score_listview_ll_cno);
            ll_xf = view.findViewById(R.id.score_listview_ll_xf);
            ll_ks = view.findViewById(R.id.score_listview_ll_ks);
            ll_khfs = view.findViewById(R.id.score_listview_ll_khfs);
            ll_kcsx = view.findViewById(R.id.score_listview_ll_kcsx);
        }
    }
}