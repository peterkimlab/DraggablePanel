package com.test.english.ui.frag3;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.test.english.ui.main.MainActivity;

public class SearchFragment extends Fragment {

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        mainActivity = (MainActivity) getActivity();

        return view;
    }
}
