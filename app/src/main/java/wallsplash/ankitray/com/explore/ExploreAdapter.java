package wallsplash.ankitray.com.explore;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import wallsplash.ankitray.com.bean.ExploreBean;
import wallsplash.ankitray.com.wallsplash.R;

public class ExploreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    Context context;
    private LayoutInflater inflater;
    ArrayList<ExploreBean> exploreBeanList = new ArrayList<>();
    OnExploreSelectedListner onExploreSelectedListner;
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

    public ExploreAdapter(Context context, ArrayList<ExploreBean> photosBeans,RecyclerView recyclerView) {
        this.context = context;
        this.exploreBeanList = photosBeans;
        this.mRecyclerView = recyclerView;
        inflater = LayoutInflater.from(context);
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
    }
    @Override
    public int getItemViewType(int position) {
        return exploreBeanList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void setOnExploreSelectedListner(OnExploreSelectedListner onExploreSelectedListner) {

        this.onExploreSelectedListner = onExploreSelectedListner;
    }

    public interface OnExploreSelectedListner {
        void setOnExploreSelatedListner(int position, ExploreBean dataBean);

    }

    @Override
    public RecyclerView.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
            return new MyViewholder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewholder) {
            final MyViewholder myHolder = (MyViewholder) holder;
            myHolder.tvExploreName.setText(Html.fromHtml(exploreBeanList.get(position).getTitle()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                //  private RecyclerView rvevenment;
                @Override
                public void onClick(View view) {
                    onExploreSelectedListner.setOnExploreSelatedListner(position, exploreBeanList.get(position));

                }
            });
        } else {

            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

         /*   if(!isLoading)
                ((LoadingViewHolder) holder).progressBar.setVisibility(View.GONE);*/
        }

    }

    @Override
    public int getItemCount() {
        return exploreBeanList.size();
    }


    public class MyViewholder extends RecyclerView.ViewHolder {
        TextView tvExploreName;

        public MyViewholder(View itemView) {
            super(itemView);

            tvExploreName = (TextView) itemView.findViewById(R.id.tvExploreName);
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