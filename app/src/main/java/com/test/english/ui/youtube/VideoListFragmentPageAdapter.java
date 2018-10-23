package com.test.english.ui.youtube;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.exam.english.R;
import java.util.HashMap;
import java.util.Map;

public class VideoListFragmentPageAdapter extends FragmentPagerAdapter {

    private static final String TAG = VideoListFragmentPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    Context context;

    public VideoListFragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                //return RelationFragment.newInstance();
            case 1:
                //return EpisodeFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;

       /* Fragment fragment = (Fragment) object;
        String tag = fragment.getTag();
        String last = tag.charAt(tag.length() - 1)+"";
        if (fullChange) {
            return POSITION_NONE;
        } else {
            fullChange = true;
            if(relationTag.equals(tag)){
                return super.getItemPosition(object);
            }else{
                return POSITION_NONE;
            }


        }*/
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getString(R.string.player_relation);
            case 1:
                return context.getString(R.string.player_episode);
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
}
