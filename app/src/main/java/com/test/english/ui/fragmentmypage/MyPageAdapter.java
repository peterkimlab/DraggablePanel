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

    public static final int PROFILE = 0;
    public static final int PAIDITEM = 1;
    public static final int BASICROW = 2;

    public MyPageAdapter(Context context, List<Object> objects) {
        mContext = context;
        mObjects = objects;
    }

    @Override
    public int getItemViewType(int position) {
        if (mObjects.get(position) instanceof ProfileItems)
            return PROFILE;
        else if (mObjects.get(position) instanceof Integer)
            return PAIDITEM;
        else if (mObjects.get(position) instanceof BasicRowItems)
            return BASICROW;
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        switch (viewType) {
            case PROFILE:
                View itemView0 = li.inflate(R.layout.item_mypage_profile, parent, false);
                return new ProfileViewHolder(itemView0);
            case PAIDITEM:
                View itemView1 = li.inflate(R.layout.item_mypage_paiditem, parent, false);
                return new ImageViewHolder(itemView1);
            case BASICROW:
                View itemView2 = li.inflate(R.layout.item_mypage_basicrow, parent, false);
                return new BasicRowViewHolder(itemView2);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PROFILE:
                ProfileItems profileItem = (ProfileItems) mObjects.get(position);
                ProfileViewHolder profileViewHolder = (ProfileViewHolder) holder;
                profileViewHolder.tvUserName.setText(profileItem.getName());
                profileViewHolder.ivProfile.setImageResource(profileItem.getImage());
                break;
            case PAIDITEM:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.ivImage.setImageResource((int) mObjects.get(position));
                break;
            case BASICROW:
                BasicRowItems rowItems = (BasicRowItems) mObjects.get(position);
                BasicRowViewHolder userViewHolder = (BasicRowViewHolder) holder;
                userViewHolder.tvName.setText(rowItems.getName());
                userViewHolder.ivIcon.setImageResource(rowItems.getImage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUserName;
        private ImageView ivProfile;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            ivProfile = (ImageView) itemView.findViewById(R.id.iv_profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, mObjects.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.imv_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, mObjects.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public class BasicRowViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView ivIcon;

        public BasicRowViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_user_name);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BasicRowItems user = (BasicRowItems) mObjects.get(getAdapterPosition());
                    Toast.makeText(mContext, user.getName() + ", " , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
