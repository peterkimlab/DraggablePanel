package com.edxdn.hmsoon.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.ui.adapter.EndlessRecyclerOnScrollListener;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.adapter.SpacesItemDecoration;
import com.edxdn.hmsoon.ui.main.MainActivity;
import com.edxdn.hmsoon.util.HummingUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FavoriteActivity extends AppCompatActivity {

    private String Tag = "FavoriteFragment";

    private RecyclerView playlisRecyclerView;
    private APIInterface apiInterface;
    private List<Datums> datumList;
    private FavoriteAdapter mAdapter;
    private Handler mHandler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String email = "";
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        datumList = new ArrayList<>();

        playlisRecyclerView = (RecyclerView) findViewById(R.id.your_play_list);

        getData(1, MyCustomApplication.getMainInstance().getEmailInfo());

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        playlisRecyclerView.addItemDecoration(decoration);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                playlisRecyclerView.getContext(),  LinearLayoutManager.VERTICAL
        );
        playlisRecyclerView.addItemDecoration(mDividerItemDecoration);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playlisRecyclerView.setLayoutManager(layoutManager);
        playlisRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(current_page+1, email);
            }

            @Override
            public void onScrolledToTop(int current_page) {
            }

            @Override
            public void onScrolledUpDown(int dy) {
            }
        });
        playlisRecyclerView.setHasFixedSize(true);
        playlisRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, playlisRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Message message= Message.obtain();
                        message.what = position;
                        mHandler.sendMessage(message);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                Datums datums = datumList.get(msg.what);
                MyCustomApplication.getMainInstance().setVideoUrl(datums);
            }
        };

        mAdapter = new FavoriteAdapter(this, datumList);
        playlisRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.SEARCH_CHECK = false;
                //datumList.clear();
                mAdapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(1, email);
                    }
                }, 500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 800);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    public void getData(int current_page, String email) {
        disposable.add(
                apiInterface.getWatched(current_page + "", email)
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    datumList.addAll(searchResource.hits.hits);
                                    //MyCustomApplication.getMainInstance().setFavoriteList(datumList);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                            }
                        })
        );
        /*if (!email.equals("")) {
            //Call<SearchResource> call = apiInterface.getFavorite(current_page+"", email);
            Call<SearchResource> call = apiInterface.getWatched(current_page + "", email);
            call.enqueue(new retrofit2.Callback<SearchResource>() {
                @Override
                public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                    SearchResource resource = response.body();
                    if (resource != null && resource.hits != null) {
                        datumList.addAll(resource.hits.hits);
                        mainActivity.setFavoriteList(datumList);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(Call<SearchResource> call, Throwable t) {
                    call.cancel();
                }
            });
        }*/
    }

    public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

        private final String TAG = FavoriteAdapter.class.getSimpleName();

        private Context context;
        private List<Datums> playlists;

        public FavoriteAdapter(Context context, List<Datums> playlists) {
            this.context = context;
            this.playlists = playlists;
        }

        @Override
        public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
            return new FavoriteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FavoriteViewHolder holder, int position) {
            final Datums playlistObject = playlists.get(position);
            holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
            holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
            holder.time.setText(HummingUtils.getTime(playlistObject, context));
            if (HummingUtils.isEmpty(holder.time)) {
                holder.time.setVisibility(View.GONE);
            }
            Picasso.with(context).load(HummingUtils.IMAGE_PATH+playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(holder.thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    holder.thumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.thumbnail.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return playlists.size();
        }

        public class FavoriteViewHolder extends RecyclerView.ViewHolder{

            public TextView sentence;
            public TextView vtitle;
            public TextView time;
            public ImageView thumbnail;

            public FavoriteViewHolder(View itemView) {
                super(itemView);
                sentence = (TextView)itemView.findViewById(R.id.sentence);
                vtitle = (TextView)itemView.findViewById(R.id.vtitle);
                time = (TextView)itemView.findViewById(R.id.time);
                thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            }
        }
    }
}