package com.test.english.ui.fragmentmypage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyPage extends Fragment {

    private RecyclerView rvMultipleViewType;
    private List<Object> mData;

    public static FragmentMyPage newInstance() {
        return new FragmentMyPage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // https://dreamaz.tistory.com/345
        // niceguy0825@edxdn.com
        // http://a-student.github.io/SvgToVectorDrawableConverter.Web/

        rvMultipleViewType = (RecyclerView) view.findViewById(R.id.rv_multipe_view_type);
        mData = new ArrayList<>();

        mData.add(R.drawable.adele);
        mData.add(new RowItems("test", R.drawable.icon_logout));
        mData.add(new RowItems("test", R.drawable.icon_alarm));
        mData.add(new RowItems("test", R.drawable.icon_cash));
        mData.add(new RowItems("test", R.drawable.icon_playlist));
        mData.add(new RowItems("test", R.drawable.icon_favorite));
        mData.add(new RowItems("test", R.drawable.icon_opencon));
        mData.add("Text 0");

        MyPageAdapter adapter = new MyPageAdapter(getActivity(), mData);
        rvMultipleViewType.setAdapter(adapter);
        rvMultipleViewType.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
