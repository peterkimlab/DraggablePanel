package com.test.english.ui.frag1;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.exam.english.databinding.Fragment1Binding;

public class Fragment1 extends Fragment {

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    private Fragment1Binding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //DataBindingUtil.inflate(inflater, R.layout.fragment_1, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_1, container, false);

        /*ArrayList<Product> items = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.rv.setLayoutManager(mLayoutManager);
        ProductAdapter mCardAdapter = new ProductAdapter(getActivity().getSupportFragmentManager(), items);
        binding.rv.setAdapter(mCardAdapter);*/
        return binding.getRoot();
    }
}
