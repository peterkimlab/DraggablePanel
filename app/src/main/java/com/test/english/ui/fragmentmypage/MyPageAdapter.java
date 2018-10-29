package com.test.english.ui.fragmentmypage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exam.english.R;

import java.util.List;

public class MyPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Object> mObjects;


    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int BASICROW = 2;


    public MyPageAdapter(Context context, List<Object> objects) {
        mContext = context;
        mObjects = objects;
    }

    @Override
    public int getItemViewType(int position) {
        if (mObjects.get(position) instanceof String)
            return TEXT;
        else if (mObjects.get(position) instanceof Integer)
            return IMAGE;
        else if (mObjects.get(position) instanceof RowItems)
            return BASICROW;
        return -1;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        switch (viewType) {
            case TEXT:
                View itemView0 = li.inflate(R.layout.row_text, parent, false);
                return new TextViewHolder(itemView0);
            case IMAGE:
                View itemView1 = li.inflate(R.layout.row_image, parent, false);
                return new ImageViewHolder(itemView1);
            case BASICROW:
                View itemView2 = li.inflate(R.layout.item_mypage_basicrow, parent, false);
                return new BasiRowViewHolder(itemView2);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TEXT:
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.tvText.setText(mObjects.get(position).toString());
                break;
            case IMAGE:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.imvImage.setImageResource((int) mObjects.get(position));
                break;
            case BASICROW:
                RowItems rowItems = (RowItems) mObjects.get(position);
                BasiRowViewHolder userViewHolder = (BasiRowViewHolder) holder;
                userViewHolder.tvName.setText(rowItems.getName());
                userViewHolder.ivIcon.setImageResource(rowItems.getImage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public class BasiRowViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView ivIcon;

        public BasiRowViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RowItems user = (RowItems) mObjects.get(getAdapterPosition());
                    Toast.makeText(mContext, user.getName() + ", " , Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView tvText;

        public TextViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, mObjects.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvImage;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imvImage = (ImageView) itemView.findViewById(R.id.imv_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, mObjects.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
