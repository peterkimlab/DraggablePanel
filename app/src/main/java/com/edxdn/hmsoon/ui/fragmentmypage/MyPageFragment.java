package com.edxdn.hmsoon.ui.fragmentmypage;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.databinding.FragmentMypageBinding;
import java.util.ArrayList;
import java.util.List;

public class MyPageFragment extends Fragment {

    private RecyclerView rvMultipleViewType;
    private List<Object> mData;

    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentMypageBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mypage, container, false);

        rvMultipleViewType = (RecyclerView) binding.rvMultipeViewType;
        mData = new ArrayList<>();
        rvMultipleViewType.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        initViewData();

        MyPageAdapter adapter = new MyPageAdapter(getActivity(), mData);
        rvMultipleViewType.setAdapter(adapter);
        rvMultipleViewType.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }

    private void initViewData() {

        mData.add(new ProfileItem("Choi", R.drawable.icon_user));
        mData.add(new StudiedLevelItems(1 + ""));
        mData.add(R.drawable.icon_hummingsoon);
        mData.add(new BasicRowItems("로그아웃", R.drawable.icon_logout));
        mData.add(new BasicRowItems("알림", R.drawable.icon_alarm));
        mData.add(new BasicRowItems("결제", R.drawable.icon_cash));
        mData.add(new BasicRowItems("재생이력", R.drawable.icon_playlist));
        mData.add(new BasicRowItems("즐겨찾기", R.drawable.icon_favorite));
        mData.add(new BasicRowItems("About", R.drawable.icon_opencon));
        //mData.add("Text 0");
    }
}