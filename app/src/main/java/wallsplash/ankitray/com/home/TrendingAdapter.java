package wallsplash.ankitray.com.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import wallsplash.ankitray.com.bean.TrendingBean;
import wallsplash.ankitray.com.wallsplash.R;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.RecyclerVH> {
    Context c;
    ArrayList<TrendingBean> trendingList = new ArrayList<>();
    OnCategorySelectedListner onCategorySelectedListner;
    TrendingBean trendingBean;

    public TrendingAdapter(Context c, ArrayList<TrendingBean> trendingList) {
        this.c = c;
        this.trendingList = trendingList;
    }

    public void setOnCategorySelectedListner(OnCategorySelectedListner onCategorySelectedListner) {

        this.onCategorySelectedListner = onCategorySelectedListner;
    }

    public interface OnCategorySelectedListner {
        void setOnCategorySelatedListner(int position, TrendingBean trendingBean);

    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_trendingcat, parent, false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
        holder.tvTrendingName.setText(Html.fromHtml(trendingList.get(position).getTitle()));
        if (trendingList.get(position).isSelected()) {
            holder.catview.setBackgroundTintList(ColorStateList.valueOf(c.getResources().getColor(R.color.colorBlack)));
            holder.tvTrendingName.setTextColor(c.getResources().getColor(R.color.colorBlack));
        } else {
            holder.catview.setBackgroundTintList(ColorStateList.valueOf(c.getResources().getColor(R.color.colortrendingLine)));
            holder.tvTrendingName.setTextColor(c.getResources().getColor(R.color.colortrendingLine));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //  private RecyclerView rvevenment;
            @Override
            public void onClick(View view) {
                onCategorySelectedListner.setOnCategorySelatedListner(position, trendingList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }


    public class RecyclerVH extends RecyclerView.ViewHolder {
        TextView tvTrendingName;

        View catview;
        public RecyclerVH(View itemView) {
            super(itemView);
            tvTrendingName = (TextView) itemView.findViewById(R.id.tvTrendingName);
            catview = (View) itemView.findViewById(R.id.catview);
        }
    }
}