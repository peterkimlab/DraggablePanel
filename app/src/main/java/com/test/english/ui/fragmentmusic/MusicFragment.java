package com.test.english.ui.fragmentmusic;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.exam.english.databinding.FragmentMusicBinding;

public class MusicFragment extends Fragment {

    private FragmentMusicBinding bingding;

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bingding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false);
        return bingding.getRoot();
    }
}
