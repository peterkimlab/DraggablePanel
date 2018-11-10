package com.test.english.ui.searchfragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.exam.english.R;
import com.test.english.api.APIClient;
import com.test.english.api.APIInterface;
import com.test.english.api.Datums;
import com.test.english.api.SearchResource;
import com.test.english.application.MyCustomApplication;
import com.test.english.ui.adapter.RecyclerItemClickListener;
import com.test.english.ui.adapter.SpacesItemDecoration;
import com.test.english.util.HummingUtils;
import java.util.ArrayList;
import java.util.List;
import cafe.adriel.androidaudiorecorder.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private int color;
    private String sentence;
    private Button mainSearchBtn;
    private ImageView mainClearBtn;
    private AppCompatEditText mSearchBox;
    private RelativeLayout contentLayout;
    private APIInterface apiInterface;
    private RecyclerView recyclerView;
    private List<Datums> datumList;
    private SearchHelperAdapter mAdapter;
    private RecyclerView recyclerViewHistoryw;
    private TextView searchHelperHistoryText;
    private TextView searchHelperText;
    private List<Datums> datumHistoryList;
    private SearchHelperAdapter mAdapterHistory;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchs, container, false);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Util.getDarkerColor(color)));
            //getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.aar_ic_clear));
        }
        datumList = new ArrayList<>();
        datumHistoryList = new ArrayList<>();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        contentLayout = (RelativeLayout) view.findViewById(R.id.content);
        contentLayout.setBackgroundColor(Util.getDarkerColor(color));

        if (Util.isBrightColor(color)) {
            ContextCompat.getDrawable(getContext(), R.drawable.aar_ic_clear)
                    .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            ContextCompat.getDrawable(getContext(), R.drawable.aar_ic_check)
                    .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        }

        searchHelperHistoryText = (TextView) view.findViewById(R.id.searchHelperHistoryText);
        searchHelperText = (TextView) view.findViewById(R.id.searchHelperText);
        searchHelperHistoryText.setVisibility(View.INVISIBLE);
        searchHelperText.setVisibility(View.INVISIBLE);

        mainSearchBtn = (Button) view.findViewById(R.id.mainSearchBtn);
        mainSearchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View p0) {
                        selectAudio();

                    }
                });
//        mainSearchBtn.setVisibility(View.GONE);

        mainClearBtn = (ImageView) view.findViewById(R.id.mainClearBtn);
        mainClearBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View p0) {
                        mSearchBox.setText("");
                    }
                });
        mainClearBtn.setVisibility(View.INVISIBLE);

        mSearchBox = (AppCompatEditText) view.findViewById(R.id.editText1);
        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        selectAudio();
                        break;
                    default:
                        selectAudio();
                        return false;
                }
                return true;
            }
        });
        mSearchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSearchBox.getText().toString().equals("")) {
                    mainClearBtn.setVisibility(View.INVISIBLE);
                } else {
                    mainClearBtn.setVisibility(View.VISIBLE);
                }
                getData(mSearchBox.getText().toString());
                getDataHistory(mSearchBox.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);

        recyclerView = (RecyclerView) view.findViewById(R.id.searchHelper);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),  LinearLayoutManager.VERTICAL
        );
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Datums datums = datumList.get(position);
                        mSearchBox.setText(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
                        selectAudio();
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        mAdapter = new SearchHelperAdapter(getContext(), datumList);
        recyclerView.setAdapter(mAdapter);

        recyclerViewHistoryw = (RecyclerView) view.findViewById(R.id.searchHelperHistory);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewHistoryw.setLayoutManager(layoutManager2);

        DividerItemDecoration mDividerItemDecoration2 = new DividerItemDecoration(
                recyclerView.getContext(),  LinearLayoutManager.VERTICAL
        );
        recyclerViewHistoryw.addItemDecoration(mDividerItemDecoration2);
        recyclerViewHistoryw.addItemDecoration(decoration);
        recyclerViewHistoryw.setHasFixedSize(true);
        recyclerViewHistoryw.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerViewHistoryw ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Datums datums = datumHistoryList.get(position);
                        mSearchBox.setText(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
                        selectAudio();
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        mAdapterHistory = new SearchHelperAdapter(getContext(), datumHistoryList);
        recyclerViewHistoryw.setAdapter(mAdapterHistory);
        setText(sentence);
        Handler handlers = new Handler();
        handlers.postDelayed(new Runnable() {
            @Override public void run() {
                getData(sentence);
            }
        }, 100);

        Handler handlers2 = new Handler();
        handlers2.postDelayed(new Runnable() {
            @Override public void run() {
                getDataHistory(sentence);
            }
        }, 500);

        return view;
    }

    public void getData(String sentence) {
        Call<SearchResource> call = apiInterface.doGetSearchHelper(2+"", sentence);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();

                datumList.clear();
                for(int i=0;i< resource.hits.hits.size() ;i++){
                    boolean check = true;
                    for(int j=0;j< datumList.size() ;j++){
                        if(datumList.get(j).source.get(HummingUtils.ElasticField.TEXT_EN).toString().equals(resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString())){
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        datumList.add(resource.hits.hits.get(i));
                    }
                }
                if (datumList.size() == 0) {
                    searchHelperText.setVisibility(View.INVISIBLE);
                } else {
                    searchHelperText.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void getDataHistory(String sentence) {
        Call<SearchResource> call = apiInterface.doGetSearchHistory(5+"", sentence);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                datumHistoryList.clear();
                for (int i=0;i< resource.hits.hits.size() ;i++) {
                    boolean check = true;
                    for (int j=0;j< datumHistoryList.size() ;j++) {
                        if (datumHistoryList.get(j).source.get(HummingUtils.ElasticField.TEXT_EN).toString().equals(resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString())) {
                            check = false;
                            break;
                        }
                    }
                    if(check) {
                        datumHistoryList.add(resource.hits.hits.get(i));
                    }
                    if(datumHistoryList.size() > 1){
                        break;
                    }
                }
                if (datumHistoryList.size() == 0) {
                    searchHelperHistoryText.setVisibility(View.INVISIBLE);
                } else {
                    searchHelperHistoryText.setVisibility(View.VISIBLE);
                }
                mAdapterHistory.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void selectAudio() {
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override public void run() {
                InputMethodManager immhide = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                immhide.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
            }
        }, 0);

        //Intent intent2 = new Intent();
        //intent2.putExtra("result", mSearchBox.getText().toString());

        MyCustomApplication.getMainInstance().searchSentencePopup(mSearchBox.getText().toString());

        //setResult(RESULT_OK, intent2);
        //finish();
    }

    public void setText(String sentence) {
        if (sentence == null) {
            sentence = "";
        }
        mSearchBox.setText(sentence);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override public void run() {
                mSearchBox.requestFocus();
                mSearchBox.setSelection(mSearchBox.getText().length());
                mSearchBox.requestFocus();
                mSearchBox.setPrivateImeOptions("defaultInputmode=english;");

                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 800);


    }

    /*@Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }*/

    @Override
    public void onResume() {
        super.onResume();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putInt(AsearchRecorder.EXTRA_COLOR, color);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aar_audio_recorder, menu);
        saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.aar_ic_check));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override public void run() {
                    InputMethodManager immhide = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immhide.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
                }
            }, 100);
            //finish();
        } else if (i == R.id.action_save) {
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.nomove, R.anim.push_right_out);
    }*/
}
