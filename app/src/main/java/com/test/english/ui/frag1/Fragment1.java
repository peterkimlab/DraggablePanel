package com.test.english.ui.frag1;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.exam.english.databinding.Fragment1Binding;
import com.test.english.ui.adapter.ProductAdapter;
import com.test.english.ui.data.Product;
import java.util.ArrayList;

public class Fragment1 extends Fragment {

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    private Fragment1Binding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_1, container, false);

        ArrayList<Product> items = new ArrayList<>();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.rv.setLayoutManager(mLayoutManager);
        ProductAdapter mCardAdapter = new ProductAdapter(getActivity().getSupportFragmentManager(), items);
        binding.rv.setAdapter(mCardAdapter);
        return binding.getRoot();
    }
}
