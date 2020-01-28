package wallsplash.ankitray.com.search;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import wallsplash.ankitray.com.bean.ExploreBean;
import wallsplash.ankitray.com.wallsplash.R;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    Context c;
    private LayoutInflater inflater;
    ArrayList<ExploreBean> exploreList = new ArrayList<>();
    OnCategorybyidSelectedListner onCategorybyidSelectedListner;
    ExploreBean exploreBean;
    RecyclerView mRecyclerView;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public SearchAdapter(final Context c, ArrayList<ExploreBean> exploreList, RecyclerView recyclerView) {
        this.c = c;
        this.exploreList = exploreList;
        this.mRecyclerView = recyclerView;
        inflater = LayoutInflater.from(c);
//        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

      /*  final StaggeredGridLayoutManager linearLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
             //   lastVisibleItem = linearLayoutManager.findLastVisibleItemPositions(null)[0];
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPositions(new int[linearLayoutManager.getSpanCount()])[0];

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
*/
    /*    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });*/
    }
    @Override
    public int getItemViewType(int position) {
        return exploreList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void setOnCategorybyidSelectedListner(OnCategorybyidSelectedListner onCategorybyidSelectedListner) {

        this.onCategorybyidSelectedListner = onCategorybyidSelectedListner;
    }

    public interface OnCategorybyidSelectedListner {
        void setOnCategorybyidSelatedListner(int position, ExploreBean exploreBean);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explorephotosbyid, parent, false);
            return new MyViewholder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //holder.tvTrendingName.setText(Html.fromHtml(trendingList.get(position).getTitle()));

        if (holder instanceof MyViewholder) {
            final MyViewholder myHolder = (MyViewholder) holder;
           /* Picasso.get()
                    .load(exploreList.get(position).getRegular())
                    .into(myHolder.imgview);*/
            Glide.with(c).load(exploreList.get(position).getRegular())
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.ic_placeholder_photos)
                    .error(R.drawable.ic_placeholder_photos)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(myHolder.imgview);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                //  private RecyclerView rvevenment;
                @Override
                public void onClick(View view) {
                    onCategorybyidSelectedListner.setOnCategorybyidSelatedListner(position, exploreList.get(position));

                }
            });
        } else {

            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

            if(!isLoading)
                ((LoadingViewHolder) holder).progressBar.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return exploreList.size();
    }


    public class MyViewholder extends RecyclerView.ViewHolder {
        ImageView imgview;


        public MyViewholder(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);

        }
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }
}