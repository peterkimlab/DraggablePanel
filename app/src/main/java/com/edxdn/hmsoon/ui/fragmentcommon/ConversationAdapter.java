package com.edxdn.hmsoon.ui.fragmentcommon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.HashMap;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<HashMap<String, Object>>  items;
    private Context mContext;
    private int textAMode = 0;
    private int textTMode = 0;
    private int textSMode = 0;

    private final int DATE = 0, YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConversationAdapter(Context context, List<HashMap<String, Object>>  items) {
        this.mContext = context;
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {

        //More to come
        if (items.get(position).get(HummingUtils.ElasticField.SPEAKER).equals("0")) {
            return DATE;
        } else if (items.get(position).get(HummingUtils.ElasticField.SPEAKER).equals("A")) {
            return YOU;
        } else if (items.get(position).get(HummingUtils.ElasticField.SPEAKER).equals("B")) {
            return ME;
        } else {
            return YOU;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case DATE:
                View v1 = inflater.inflate(R.layout.layout_holder_date, viewGroup, false);
                viewHolder = new HolderDate(v1);
                break;
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
                viewHolder = new HolderYou(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
                viewHolder = new HolderMe(v);
                break;
        }
        return viewHolder;
    }
    public void addItem(List<HashMap<String, Object>> item) {
        items.addAll(item);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case DATE:
                HolderDate vh1 = (HolderDate) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case YOU:
                HolderYou vh2 = (HolderYou) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                HolderMe vh = (HolderMe) viewHolder;
                configureViewHolder3(vh, position);
                break;
        }
    }

    private void configureViewHolder3(HolderMe vh1, int position) {
        vh1.getTime().setText("now");
        String text = "";

        int t = 0;
        int a = 0;
        int s = 0;

        if (textSMode == 1) {
            text += items.get(position).get(HummingUtils.ElasticField.SPEAK_KO).toString();
            s = text.length();
        }
        if (textAMode == 0) {
            if (text.length() != 0) {
                text += "\n\n";
            }
            text += items.get(position).get(HummingUtils.ElasticField.TEXT_EN).toString();
            a = text.length();
        }
        if (textTMode == 1) {
            if (text.length() != 0) {
                text += "\n\n";
            }
            text += items.get(position).get(HummingUtils.ElasticField.TEXT_LO).toString();
            t = text.length();
        }

        final SpannableStringBuilder sp = new SpannableStringBuilder(text);

        if (textSMode == 1) {
            sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.grey6)), 0, s, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (textAMode == 0) {
            sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)), s, a, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (textTMode == 1) {
            sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.grey2)), a, t, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        vh1.getChatText().setText("");
        vh1.getChatText().append(sp);
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
        vh1.getTime().setText("now");
        String text = "";
        int s = 0;
        int a = 0;
        int t = 0;

        if (textSMode == 1) {
            text += items.get(position).get(HummingUtils.ElasticField.SPEAK_KO).toString();
            s = text.length();
        }
        if (textAMode == 0) {
            if (text.length() != 0) {
                text += "\n\n";
            }
            text += items.get(position).get(HummingUtils.ElasticField.TEXT_EN).toString();
            a = text.length();
        }
        if (textTMode == 1) {
            if (text.length() != 0) {
                text += "\n\n";
            }
            text += items.get(position).get(HummingUtils.ElasticField.TEXT_LO).toString();
            t = text.length();
        }

        final SpannableStringBuilder sp = new SpannableStringBuilder(text);

        if (textSMode == 1) {
            sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.grey6)), 0, s, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (textAMode == 0) {
            sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorTextBlack)), s, a, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (textTMode == 1) {
            sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.grey2)), a, t, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        vh1.getChatText().setText("");
        vh1.getChatText().append(sp);
       // vh1.getChatText().setText(text);
    }
    private void configureViewHolder1(HolderDate vh1, int position) {
        vh1.getDate().setText(items.get(position).get(HummingUtils.ElasticField.CONTENTSDATE).toString());
    }

    public class HolderDate extends RecyclerView.ViewHolder {

        private TextView date;

        public HolderDate(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.tv_date);
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }
    }

    public class HolderMe extends RecyclerView.ViewHolder {

        private TextView time, chatText;

        public HolderMe(View v) {
            super(v);
            time = (TextView) v.findViewById(R.id.tv_time);
            chatText = (TextView) v.findViewById(R.id.tv_chat_text);
        }

        public TextView getTime() {
            return time;
        }

        public void setTime(TextView time) {
            this.time = time;
        }

        public TextView getChatText() {
            return chatText;
        }

        public void setChatText(TextView chatText) {
            this.chatText = chatText;
        }
    }

    public class HolderYou extends RecyclerView.ViewHolder {

        private TextView time, chatText;

        public HolderYou(View v) {
            super(v);
            time = (TextView) v.findViewById(R.id.tv_time);
            chatText = (TextView) v.findViewById(R.id.tv_chat_text);
        }

        public TextView getTime() {
            return time;
        }

        public void setTime(TextView time) {
            this.time = time;
        }

        public TextView getChatText() {
            return chatText;
        }

        public void setChatText(TextView chatText) {
            this.chatText = chatText;
        }
    }

    public void setTextAMode(int textAMode) {
        this.textAMode = textAMode;
    }
    public void setTextTMode(int textTMode) {
        this.textTMode = textTMode;
    }
    public void setTextSMode(int textSMode) {
        this.textSMode = textSMode;
    }
}
