package com.edxdn.hmsoon.ui.fragmentcommon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.main.MainActivity;
import com.edxdn.hmsoon.util.HummingUtils;
import com.exam.english.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class ConversationFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private String Tag = "ConversationFragment";

    private APIInterface apiInterface;
    private List<HashMap<String, Object>> datumList;
    private Handler mHandler;
    private MainActivity mainActivity;

    private RecyclerView mRecyclerView;
    private Button textaBtn;
    private Button texttBtn;
    private Button textsBtn;
    private ConversationAdapter mAdapter;
    private String icode = "";
    private int textaMode = 0;
    private int texttMode = 0;
    private int textsMode = 0;

    private HashMap<String, List<Datums>> sentenceListMap;
    private HashMap<String, Integer> sentenceListIndex;

   // private EditText text;
    //private Button send;

    public ConversationFragment() {
    }

    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        icode = MainActivity.CURRENT_ICODE;
        sentenceListMap = new HashMap<>();
        sentenceListIndex = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        datumList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationAdapter(getContext(),datumList);
        mRecyclerView.setAdapter(mAdapter);
       /* mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }, 1000);*/

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Message message= Message.obtain();
                        message.what = position;
                        mHandler.sendMessage(message);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        textaBtn = (Button) view.findViewById(R.id.textaBtn);
        textaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textaMode == 0){
                    textaMode = 1;
                }else if(textaMode == 1){
                    textaMode = 0;
                }
                mAdapter.setTextAMode(textaMode);
                mAdapter.notifyDataSetChanged();
            }
        });

        texttBtn = (Button) view.findViewById(R.id.texttBtn);
        texttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(texttMode == 0){
                    texttMode = 1;
                }else if(texttMode == 1){
                    texttMode = 0;
                }
                mAdapter.setTextTMode(texttMode);
                mAdapter.notifyDataSetChanged();
            }
        });

        textsBtn = (Button) view.findViewById(R.id.textsBtn);
        textsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textsMode == 0){
                    textsMode = 1;
                }else if(textsMode == 1){
                    textsMode = 0;
                }
                mAdapter.setTextSMode(textsMode);
                mAdapter.notifyDataSetChanged();
            }
        });

        /*text = (EditText) view.findViewById(R.id.et_message);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                    }
                }, 500);
            }
        });*/
        /*send = (Button) view.findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().equals("")){
                    List<ChatData> data = new ArrayList<ChatData>();
                    ChatData item = new ChatData();
                    item.setTime("6:00pm");
                    item.setType("2");
                    item.setText(text.getText().toString());
                    data.add(item);
                    mAdapter.addItem(data);
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                    text.setText("");
                }
            }
        });*/


        mainActivity = (MainActivity) getActivity();

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                HashMap<String, Object> datums = datumList.get(msg.what);
                playVideo(datums.get(HummingUtils.ElasticField.TEXT_EN).toString()
                        ,datums.get(HummingUtils.ElasticField.TEXT_LO).toString()
                        ,datums.get(HummingUtils.ElasticField.SPEAK_KO).toString()
                );


            }
        };

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                getData(1);
            }
        }, 10);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

   /* public List<ChatData> setData(){
        List<ChatData> data = new ArrayList<>();

        String text[] = {"15 September","Hi, Julia! How are you?", "Hi, Joe, looks great! :) ", "I'm fine. Wanna go out somewhere?", "Yes! Coffe maybe?", "Great idea! You can come 9:00 pm? :)))", "Ok!", "Ow my good, this Kit is totally awesome", "Can you provide other kit?", "I don't have much time, :`("};
        String time[] = {"", "5:30pm", "5:35pm", "5:36pm", "5:40pm", "5:41pm", "5:42pm", "5:40pm", "5:41pm", "5:42pm"};
        String type[] = {"0", "2", "1", "1", "2", "1", "2", "2", "2", "1"};

        for (int i=0; i<text.length; i++){
            ChatData item = new ChatData();
            item.setType(type[i]);
            item.setText(text[i]);
            item.setTime(time[i]);
            data.add(item);
        }

        return data;
    }*/

    public void getData(int current_page) {
        Call<SearchResource> call = apiInterface.getInterest(current_page+"", icode);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();

                if (resource != null && resource.hits != null) {

                    String date = resource.hits.hits.get(0).source.get(HummingUtils.ElasticField.CONTENTSDATE).toString();
                    HashMap<String, Object> source = new HashMap<String, Object>();
                    source.put(HummingUtils.ElasticField.SPEAKER, "0");
                    source.put(HummingUtils.ElasticField.CONTENTSDATE, date);
                    datumList.add(0, source);

                    for (int i = 0; i < resource.hits.hits.size(); i++) {
                        HashMap<String, Object> item = resource.hits.hits.get(i).source;
                        datumList.add(item);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void playVideo( String sentence, String textko, String speakko){

        //아마존 스피치
        //mainActivity.setSentence(sentence, textko, speakko);

        if (sentenceListMap.containsKey(sentence)) {
            int ids = 0;
            if (sentenceListIndex.containsKey(sentence)) {
                ids = sentenceListIndex.get(sentence);
            }
            mainActivity.setVideoUrl(sentenceListMap.get(sentence).get(ids));
            sentenceListIndex.put(sentence, ids++);
        } else {
            //getDataSentence(1, sentence, textko, speakko);
        }
    }

    public void getDataSentence(int current_page, final String sentence, final String textko, final String speakko) {
        /*Call<SearchResource> call = apiInterface.getSentences(current_page+"", sentence, "", "");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();

                if (resource != null && resource.hits != null && resource.hits.hits.size() != 0) {
                    List<Datums> datum = resource.hits.hits;
                    sentenceListMap.put(sentence, datum);
                    sentenceListIndex.put(sentence, 0);
                    playVideo(sentence, textko, speakko);
                } else {
                    //아마존 스피치
                    //mainActivity.setSentence(sentence, textko, speakko);
                }
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                Log.d("test","================================================2"+t.getMessage());
                call.cancel();
            }
        });*/
    }

    @Override
    public void onBack() {
        mainActivity.setOnKeyBackPressedListener(null);
        mainActivity.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    //    ((MainActivity) getActivity()).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
