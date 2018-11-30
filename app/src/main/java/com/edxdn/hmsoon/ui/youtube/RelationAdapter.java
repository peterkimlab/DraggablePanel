package com.edxdn.hmsoon.ui.youtube;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.edxdn.hmsoon.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.ArrayList;
import java.util.List;

public class RelationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = RelationAdapter.class.getSimpleName();
    private Context context;
    private List<Datums> playlists;
    private Integer seletedPosition = null;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    int color;

    public RelationAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        color = context.getResources().getColor(R.color.orange5);

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_relation_header, parent, false);
            return new RelationHeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_relation, parent, false);
            return new RelationViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_relation, parent, false);

        return new RelationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RelationHeaderViewHolder) {
            if (seletedPosition != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(seletedPosition == position){
                        int color = R.color.selected2;
                        ((RelationHeaderViewHolder) holder).clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    } else {
                        int color = R.color.transparent;
                        ((RelationHeaderViewHolder) holder).clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                }
            }

            final Datums playlistObject = playlists.get(position);

            ((RelationHeaderViewHolder) holder).sentence.setText(playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
            ((RelationHeaderViewHolder) holder).vtitle.setText(playlistObject.source.get(HummingUtils.ElasticField.TITLE).toString());
            ((RelationHeaderViewHolder) holder).vtitle.setVisibility(View.GONE);

            String time = "";
            if (playlistObject.source.get(HummingUtils.ElasticField.STIME) != null && playlistObject.source.get(HummingUtils.ElasticField.ETIME) != null) {
                time = HummingUtils.time(playlistObject.source.get(HummingUtils.ElasticField.STIME).toString(), playlistObject.source.get(HummingUtils.ElasticField.ETIME).toString());
            }
            ((RelationHeaderViewHolder) holder).time.setText(time);
            if (time.equals("")) {
                ((RelationHeaderViewHolder) holder).time.setVisibility(View.GONE);
            }

            //Picasso.with(context).load("https://s3.ap-northeast-2.amazonaws.com/my.test0002/"+playlistObject.source.thumbPath).placeholder(R.drawable.ic_menu_gallery).into(holder.thumbnail, new Callback() {
            Picasso.with(context).load(HummingUtils.IMAGE_PATH+playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(((RelationHeaderViewHolder) holder).thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    ((RelationHeaderViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                }
                @Override
                public void onError() {
                    ((RelationHeaderViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                }
            });
        } else {
            if (seletedPosition != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (seletedPosition == position) {
                        int color = R.color.selected2;
                        ((RelationViewHolder) holder).clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    } else {
                        int color = R.color.transparent;
                        ((RelationViewHolder) holder).clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                }
            }

            final Datums playlistObject = playlists.get(position);
            final SpannableStringBuilder sp = new SpannableStringBuilder(playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString());

            if (playlistObject.highlight != null) {
                ArrayList<String> high = (ArrayList<String>) playlistObject.highlight.get(HummingUtils.ElasticField.TEXT_EN);
                for (int i = 0; i < high.size(); i++) {
                    String light = high.get(i);
                    String temp = light.replaceAll("<em>", "").replaceAll("</em>", "");
                    int idx = playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString().indexOf(temp);
                    Log.e("test", playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
                    Log.e("test", light);
                    int pos = light.indexOf("<em>");
                    int pos2 = light.indexOf("</em>");
                    int cnt = 0;
                    while (pos > -1) {
                        int ccc = pos2 - (cnt*9);
                        sp.setSpan(new ForegroundColorSpan(color), pos - (cnt*9)+ idx, ccc -4+ idx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //sp.setSpan(new StyleSpan(Typeface.BOLD), pos - (cnt*9)+ idx, ccc -4+ idx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        cnt++;
                        pos =  light.indexOf("<em>", pos+1);
                        pos2 = light.indexOf("</em>",pos2+1);
                    }
                }
            }

            if (playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString().trim().equals("I'm out of here.")) {
                sp.setSpan(new ForegroundColorSpan(color), 0, playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString().trim().equals("I'm out of here!")) {
                sp.setSpan(new ForegroundColorSpan(color), 0, playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ((RelationViewHolder) holder).sentence.setText(sp);
            ((RelationViewHolder) holder).vtitle.setText(playlistObject.source.get(HummingUtils.ElasticField.TITLE).toString());
            String time = "";
            if (playlistObject.source.get(HummingUtils.ElasticField.STIME) != null && playlistObject.source.get(HummingUtils.ElasticField.ETIME) != null) {
                time = HummingUtils.time(playlistObject.source.get(HummingUtils.ElasticField.STIME).toString(), playlistObject.source.get(HummingUtils.ElasticField.ETIME).toString());
            }
            ((RelationViewHolder) holder).time.setText(time);
            if (time.equals("")) {
                ((RelationViewHolder) holder).time.setVisibility(View.GONE);
            }
//        Picasso.with(context).load("https://s3.ap-northeast-2.amazonaws.com/my.test0002/"+playlistObject.source.thumbPath).placeholder(R.drawable.ic_menu_gallery).into(holder.thumbnail, new Callback() {
            Picasso.with(context).load(HummingUtils.IMAGE_PATH+playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(((RelationViewHolder) holder).thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    ((RelationViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                }
                @Override
                public void onError() {
                    ((RelationViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void setSeletedPosition(int seletedPosition){
        this.seletedPosition = seletedPosition;
    }

    public int getSeletedPosition(){
        if(seletedPosition == null){
            return 0;
        }
        return seletedPosition;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}