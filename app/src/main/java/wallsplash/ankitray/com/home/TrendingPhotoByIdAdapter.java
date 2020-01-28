package wallsplash.ankitray.com.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import wallsplash.ankitray.com.bean.TrendingBean;
import wallsplash.ankitray.com.wallsplash.R;

public class TrendingPhotoByIdAdapter extends RecyclerView.Adapter<TrendingPhotoByIdAdapter.RecyclerVH> {
    Context c;
    ArrayList<TrendingBean> trendingList = new ArrayList<>();
    OnCategorybyidSelectedListner onCategorybyidSelectedListner;
    TrendingBean trendingBean;

    public TrendingPhotoByIdAdapter(Context c, ArrayList<TrendingBean> trendingList) {
        this.c = c;
        this.trendingList = trendingList;
    }

    public void setOnCategorybyidSelectedListner(OnCategorybyidSelectedListner onCategorybyidSelectedListner) {

        this.onCategorybyidSelectedListner = onCategorybyidSelectedListner;
    }

    public interface OnCategorybyidSelectedListner {
        void setOnCategorybyidSelatedListner(int position, TrendingBean trendingBean);

    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_trendingphotosbyid, parent, false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
        //holder.tvTrendingName.setText(Html.fromHtml(trendingList.get(position).getTitle()));
      /*  Picasso.get()
                .load(trendingList.get(position).getRegular())
                .into(holder.imgview);*/
        Glide.with(c).load(trendingList.get(position).getRegular())
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_placeholder_photos)
                .error(R.drawable.ic_placeholder_photos)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgview);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //  private RecyclerView rvevenment;
            @Override
            public void onClick(View view) {
                onCategorybyidSelectedListner.setOnCategorybyidSelatedListner(position, trendingList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }


    public class RecyclerVH extends RecyclerView.ViewHolder {
        ImageView imgview;


        public RecyclerVH(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);

        }
    }
}