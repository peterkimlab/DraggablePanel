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
    public static final int STUDIED = 1;
    public static final int PAIDITEM = 2;
    public static final int BASICROW = 3;

    public MyPageAdapter(Context context, List<Object> objects) {
        mContext = context;
        mObjects = objects;
    }

    @Override
    public int getItemViewType(int position) {
        if (mObjects.get(position) instanceof ProfileItem)
            return PROFILE;
        else if (mObjects.get(position) instanceof StudiedLevelItems)
            return STUDIED;
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
            case STUDIED:
                View itemView1 = li.inflate(R.layout.item_mypage_studied_level, parent, false);
                return new StudiedViewHolder(itemView1);
            case PAIDITEM:
                View itemView2 = li.inflate(R.layout.item_mypage_paiditem, parent, false);
                return new ImageViewHolder(itemView2);
            case BASICROW:
                View itemView3 = li.inflate(R.layout.item_mypage_basicrow, parent, false);
                return new BasicRowViewHolder(itemView3);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PROFILE:
                ProfileItem profileItem = (ProfileItem) mObjects.get(position);
                ProfileViewHolder profileViewHolder = (ProfileViewHolder) holder;
                profileViewHolder.tvUserName.setText(profileItem.getName());
                profileViewHolder.ivProfile.setImageResource(profileItem.getImage());
                break;
            case STUDIED:
                StudiedLevelItems studiedItem = (StudiedLevelItems) mObjects.get(position);
                StudiedViewHolder studiedViewHolder = (StudiedViewHolder) holder;
                studiedViewHolder.ivRectangural1.setBackgroundResource(R.color.app_identity_color);
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

    public class StudiedViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivRectangural1;

        public StudiedViewHolder(View itemView) {
            super(itemView);

            ivRectangural1 = (ImageView) itemView.findViewById(R.id.rectangural1);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, mObjects.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
                }
            });*/
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
